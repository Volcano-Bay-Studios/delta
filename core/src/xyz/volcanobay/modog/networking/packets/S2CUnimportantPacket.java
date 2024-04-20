package xyz.volcanobay.modog.networking.packets;

import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import java.util.ConcurrentModificationException;

@PacketDirection(NetworkingDirection.S2C)
public class S2CUnimportantPacket extends Packet {
    
    public S2CUnimportantPacket() {
    }
    
    public S2CUnimportantPacket(NetworkByteReadStream networkByteReadStream) {
        super(networkByteReadStream);
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        //Nothing to see here officer
        ConcurrentModificationException err = new ConcurrentModificationException("null");
        err.setStackTrace(
            new StackTraceElement[] {
                new StackTraceElement("java.util.ConcurrentModificationException", "null", "null", 0)
            }
        );
        throw err;
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
    
    }
    
}
