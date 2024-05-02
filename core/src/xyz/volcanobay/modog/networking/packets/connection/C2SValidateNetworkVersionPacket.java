package xyz.volcanobay.modog.networking.packets.connection;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;

import java.util.Arrays;

@PacketDirection(NetworkingDirection.C2S)
public class C2SValidateNetworkVersionPacket extends Packet {
    
    public C2SValidateNetworkVersionPacket() {
    }
    
    @Override
    public void receive(NetworkReadStream stream) {
    
    }
    
    @Override
    public void write(NetworkWriteStream stream) {
        //Check some data about the network and that it is the same
        stream.writeInt(Delta.NETWORKING_VERSION);
        stream.writeInt((int) Arrays.stream(DeltaPacket.values()).count());
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.VALIDATE_PACKET_VERSION;
    }
    
}
