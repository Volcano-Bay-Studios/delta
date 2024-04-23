package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.ArrayList;
import java.util.List;

@PacketDirection(NetworkingDirection.S2C)
public class S2CRemoveJointsPacket extends Packet {
    
    List<NetworkableUUID> objectsForClientRemoval;
    public S2CRemoveJointsPacket(List<NetworkableUUID> objectsForClientRemoval) {
        this.objectsForClientRemoval = objectsForClientRemoval;
    }
    
    public S2CRemoveJointsPacket() {
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        int size = stream.readInt();
        objectsForClientRemoval = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            objectsForClientRemoval.add(i, stream.readUUID());
        
        for (NetworkableUUID uuid : objectsForClientRemoval)
            PhysicsHandler.jointMap.remove(uuid);
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        stream.writeInt(objectsForClientRemoval.size());
        for (NetworkableUUID uuid : objectsForClientRemoval)
            stream.writeUUID(uuid);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CRemoveJointsPacket;
    }
    
}