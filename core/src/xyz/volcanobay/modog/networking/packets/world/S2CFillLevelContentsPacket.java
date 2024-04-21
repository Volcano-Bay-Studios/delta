package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.volcanobay.modog.physics.PhysicsHandler.jointMap;
import static xyz.volcanobay.modog.physics.PhysicsHandler.physicsObjectMap;

/**Called when a client joins to send them the state of everything in the world*/
@PacketDirection(NetworkingDirection.S2C)
public class S2CFillLevelContentsPacket extends Packet {
    
    /**
     * Data ethically sourced from static fields in PhysicsHandler.java, so nothing is needed
     */
    public S2CFillLevelContentsPacket() {
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        System.out.println("Received level data ton");
        ConcurrentHashMap<NetworkableUUID, PhysicsObject> newPhysicsObjects = new ConcurrentHashMap<>();
        ConcurrentHashMap<NetworkableUUID, WorldJoint> newJoints = new ConcurrentHashMap<>();
        
        //Load in the ones from the network
        int objectsLength = stream.readInt();
        for (int i = 0; i < objectsLength; i++) {
            NetworkableUUID uuid = stream.readUUID();
            newPhysicsObjects.put(uuid, PhysicsObject.readNewFromNetwork(stream).setUuid(uuid));
        }
        int jointsLength = stream.readInt();
        for (int i = 0; i < jointsLength; i++) {
            WorldJoint worldJoint = WorldJoint.readNewFromNetwork(stream);
            newJoints.put(worldJoint.uuid, worldJoint);
        }
        
        //Delete the old stage contents
        for (Map.Entry<NetworkableUUID, PhysicsObject> entry : physicsObjectMap.entrySet()) {
            PhysicsHandler.world.destroyBody(entry.getValue().body);
            entry.getValue().dispose();
        }
        for (Map.Entry<NetworkableUUID, WorldJoint> entry : jointMap.entrySet()) {
            PhysicsHandler.world.destroyJoint(entry.getValue().joint);
        }
    
        //And replace with the read ones
        physicsObjectMap = newPhysicsObjects;
        jointMap = newJoints;
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        stream.writeInt(physicsObjectMap.size());
        for (Map.Entry<NetworkableUUID, PhysicsObject> entry : physicsObjectMap.entrySet()) {
            stream.writeUUID(entry.getKey());
            entry.getValue().writeNewToNetwork(stream);
        }
        
        stream.writeInt(jointMap.size());
        for (Map.Entry<NetworkableUUID, WorldJoint> entry : jointMap.entrySet()) {
            entry.getValue().writeNewToNetwork(stream);
        }
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CFillStageContentsUpdatePacket;
    }
    
}
