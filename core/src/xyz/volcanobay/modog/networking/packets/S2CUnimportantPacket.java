package xyz.volcanobay.modog.networking.packets;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;

import java.util.ConcurrentModificationException;

@PacketDirection(NetworkingDirection.S2C)
public class S2CUnimportantPacket extends Packet {
    
    public S2CUnimportantPacket() {
    }
    
    @Override
    public void receive(NetworkReadStream stream) {
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
    public void write(NetworkWriteStream stream) {
    
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.NOT_IMPORTANT_PACKET_DONT_LOOK;
    }
    
}
