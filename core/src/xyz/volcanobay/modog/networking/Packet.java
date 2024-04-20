package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.RelativeNetworkSide;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

public abstract class Packet {
    
    public Packet() {};
    public Packet(NetworkByteReadStream stream) {
        receive(stream);
    }
    
    /**Fill in the fields from the read stream*/
    public abstract void receive(NetworkByteReadStream stream);
    public abstract void write(NetworkByteWriteStream stream);
    
    public void assertSide(RelativeNetworkSide currentSide) {
        PacketDirection[] packetDirectionAnnotations = getClass().getAnnotationsByType(PacketDirection.class);
        if (packetDirectionAnnotations.length != 1)
            throw new RuntimeException("Wrong number of annotations on packet, did you define the packet direction");
        PacketDirection direction = packetDirectionAnnotations[0];
        NetworkingSide side = currentSide.of(direction.value());
        assert side == null || DeltaNetwork.networkingSide.equals(side);
    }
    
}
