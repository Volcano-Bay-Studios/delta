package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.ArrayList;
import java.util.List;

import static xyz.volcanobay.modog.physics.PhysicsHandler.getWorldJointFromJoint;

@PacketDirection(NetworkingDirection.S2C)
public class S2CRemoveJointsPacket extends Packet {
    
    List<NetworkableUUID> objectsForClientRemoval;
    public S2CRemoveJointsPacket(List<NetworkableUUID> objectsForClientRemoval) {
        this.objectsForClientRemoval = objectsForClientRemoval;
    }
    
    public S2CRemoveJointsPacket() {
    }
    
    @Override
    public void receive(NetworkReadStream stream) {
        int size = stream.readInt();
        objectsForClientRemoval = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            objectsForClientRemoval.add(i, stream.readUUID());
        
        for (NetworkableUUID uuid : objectsForClientRemoval) {
            WorldJoint worldJoint = PhysicsHandler.jointMap.get(uuid);
            if (worldJoint != null) {
                PhysicsHandler.world.destroyJoint(worldJoint.joint);
                PhysicsHandler.jointMap.remove(worldJoint.uuid);
            }
        }
    }
    
    @Override
    public void write(NetworkWriteStream stream) {
        stream.writeInt(objectsForClientRemoval.size());
        for (NetworkableUUID uuid : objectsForClientRemoval)
            stream.writeUUID(uuid);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CRemoveJointsPacket;
    }
    
}
