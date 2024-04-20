package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.WorldJoint;

@PacketDirection(NetworkingDirection.S2C)
public class S2CJointCreatedPacket extends Packet {
    
    /**Object to send the state of, null on client*/
    WorldJoint object = null;
    
    public S2CJointCreatedPacket(WorldJoint object) {
        this.object = object;
    }
    
    public S2CJointCreatedPacket(NetworkByteReadStream writeStream) {
        super(writeStream);
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        object = WorldJoint.readNewFromNetwork(stream);
        PhysicsHandler.jointMap.put(object.uuid, object);
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        object.writeNewToNetwork(stream);
    }
    
}
