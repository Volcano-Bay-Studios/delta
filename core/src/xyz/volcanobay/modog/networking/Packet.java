package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.RelativeNetworkSide;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

public abstract class Packet {
    
    public Packet() {}
    
    /**Fill in the fields from the read stream*/
    public abstract void receive(NetworkByteReadStream stream);
    public abstract void write(NetworkByteWriteStream stream);
    
    public abstract DeltaPacket getType();
    
    public void assertSide(RelativeNetworkSide currentSide) {
        PacketDirection direction = getClass().getAnnotation(PacketDirection.class);
        if (direction == null)
            throw new RuntimeException("Couldn't find direction assertion for packet class  "
                + getClass() + ", did you define the packet direction annotation?");
        NetworkingSide side = currentSide.of(direction.value());
        assert side == null || DeltaNetwork.networkingSide.equals(side);
    }
    
}
