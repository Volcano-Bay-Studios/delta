package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;

import java.util.ArrayList;
import java.util.List;

@PacketDirection(NetworkingDirection.S2C)
public class S2CRemoveObjectsPacket extends Packet {
    
    List<NetworkableUUID> objectsForClientRemoval;
    
    public S2CRemoveObjectsPacket(List<NetworkableUUID> objectsForClientRemoval) {
        this.objectsForClientRemoval = objectsForClientRemoval;
    }
    
    public S2CRemoveObjectsPacket() {
    }
    
    @Override
    public void receive(NetworkReadStream stream) {
        int size = stream.readInt();
        objectsForClientRemoval = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            objectsForClientRemoval.add(i, stream.readUUID());
        
        for (NetworkableUUID uuid : objectsForClientRemoval) {
            PhysicsObject object = PhysicsHandler.physicsObjectMap.get(uuid);
            if (object != null) {
                PhysicsHandler.world.destroyBody(object.body);
                PhysicsHandler.physicsObjectMap.remove(uuid);
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
        return DeltaPacket.S2CRemoveObjectsPacket;
    }
    
}
