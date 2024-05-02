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

/**Creates or updates the object*/
@PacketDirection(NetworkingDirection.A2A)
public class A2AObjectUpdateStatePacket extends Packet {
    
    PhysicsObject object = null;
    
    public A2AObjectUpdateStatePacket(PhysicsObject object) {
        this.object = object;
    }
    
    public A2AObjectUpdateStatePacket() {
    }
    
    @Override
    public void receive(NetworkReadStream stream) {
        NetworkableUUID uuid = stream.readUUID();
        System.out.println(uuid);
        if (PhysicsHandler.physicsObjectMap.containsKey(uuid)) {
            stream.readString();//Skip over the specified type
            PhysicsHandler.physicsObjectMap.get(uuid)
                .readAllStateFromNetwork(stream);
        } else {
            object = PhysicsObject.readNewFromNetwork(stream);
            object.uuid = uuid;
            PhysicsHandler.physicsObjectMap.put(object.uuid, object);
        }
//        if (DeltaNetwork.isExternalServer())
//            NetworkingCalls.updateObjectState(object);
    }
    
    @Override
    public void write(NetworkWriteStream stream) {
        stream.writeUUID(object.uuid);
        object.writeNewToNetwork(stream);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.A2AObjectUpdateStatePacket;
    }
}
