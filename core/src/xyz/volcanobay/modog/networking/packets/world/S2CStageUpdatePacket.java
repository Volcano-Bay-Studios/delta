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

import java.util.concurrent.ConcurrentHashMap;

@PacketDirection(NetworkingDirection.S2C)
public class S2CStageUpdatePacket extends Packet {
    
    public static ConcurrentHashMap<NetworkableUUID, PhysicsObject> physicsObjectHashMap;
    public static ConcurrentHashMap<NetworkableUUID, WorldJoint> jointConcurrentHashMap;
    
    /**
     * Data ethically sourced from static fields in PhysicsHandler.java
     */
    public S2CStageUpdatePacket() {
        physicsObjectHashMap = PhysicsHandler.physicsObjectMap;
        jointConcurrentHashMap = PhysicsHandler.jointMap;
    }
    
    public S2CStageUpdatePacket(NetworkByteReadStream networkByteReadStream) {
        super(networkByteReadStream);
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
    
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CStageUpdatePacket;
    }
    
}
