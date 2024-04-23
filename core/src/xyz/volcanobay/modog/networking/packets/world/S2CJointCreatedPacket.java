package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.WorldJoint;

@PacketDirection(NetworkingDirection.S2C)
public class S2CJointCreatedPacket extends Packet {
    
    /**Object to send the state of, null on client*/
    WorldJoint object = null;
    
    public S2CJointCreatedPacket(WorldJoint object) {
        this.object = object;
    }
    
    public S2CJointCreatedPacket() {
    }
    
    @Override
    public void receive(NetworkReadStream stream) {
        object = WorldJoint.readNewFromNetwork(stream);
        PhysicsHandler.jointMap.put(object.uuid, object);
    }
    
    @Override
    public void write(NetworkWriteStream stream) {
        object.writeNewToNetwork(stream);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CJointCreatedPacket;
    }
    
}
