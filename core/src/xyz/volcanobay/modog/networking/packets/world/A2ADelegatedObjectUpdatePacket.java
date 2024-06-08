package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;

@PacketDirection(NetworkingDirection.S2C)
public class A2ADelegatedObjectUpdatePacket extends Packet {
    NetworkableUUID fromUUID;
    boolean deDelegate = false;
    PhysicsObject object;
    
    /**
     * Data ethically sourced from static fields in PhysicsHandler.java
     */
    public A2ADelegatedObjectUpdatePacket(PhysicsObject object, boolean deDelegate) {
        this.object = object;
        this.deDelegate = deDelegate;
    }

    public A2ADelegatedObjectUpdatePacket() {
    }

    @Override
    public DeltaPacket getType() {
        return DeltaPacket.UPDATE_OBJECT_DELEGATION;
    }

    @Override
    public void receive(NetworkReadStream stream) {
        deDelegate = stream.readByteBool();
        fromUUID = stream.readUUID();
        NetworkableUUID uuid = stream.readUUID();
        object = PhysicsHandler.physicsObjectMap.get(uuid);
        if (object != null) {
            if (!deDelegate)
                object.setDelegateOf(fromUUID);
            else
                object.setDelegateOf(NetworkConnectionsManager.hostUUID);
        }
    }

    @Override
    public void write(NetworkWriteStream stream) {
        stream.writeByteBool(deDelegate);
        stream.writeUUID(NetworkConnectionsManager.selfConnectionId);
        stream.writeUUID(object.uuid);
    }
}
