package xyz.volcanobay.modog.networking.packets.world;

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
public class S2CFillStageContentsUpdatePacket extends Packet {
    
    /**
     * Data ethically sourced from static fields in PhysicsHandler.java, so nothing is needed
     */
    public S2CFillStageContentsUpdatePacket() {
    }
    
    public S2CFillStageContentsUpdatePacket(NetworkByteReadStream networkByteReadStream) {
        super(networkByteReadStream);
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        ConcurrentHashMap<NetworkableUUID, PhysicsObject> newPhysicsObjects = new ConcurrentHashMap<>();
        ConcurrentHashMap<NetworkableUUID, WorldJoint> newJoints = new ConcurrentHashMap<>();
        
        //Load in the ones from the network
        int objectsLength = stream.readInt();
        for (int i = 0; i < objectsLength; i++) {
            newPhysicsObjects.put(stream.readUUID(), PhysicsObject.readNewFromNetwork(stream));
        }
        int jointsLength = stream.readInt();
        for (int i = 0; i < jointsLength; i++) {
            newJoints.put(stream.readUUID(), WorldJoint.readNewFromNetwork(stream));
        }
        
        //Delete the old stage contents
        for (Map.Entry<NetworkableUUID, PhysicsObject> entry : newPhysicsObjects.entrySet()) {
            entry.getValue().dispose();
            PhysicsHandler.world.destroyBody(entry.getValue().body);
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
            stream.writeUUID(entry.getKey());
            entry.getValue().writeNewToNetwork(stream);
        }
    }
    
}
