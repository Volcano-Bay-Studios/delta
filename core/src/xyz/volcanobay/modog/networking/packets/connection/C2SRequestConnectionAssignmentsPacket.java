package xyz.volcanobay.modog.networking.packets.connection;


import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import java.util.Arrays;

@PacketDirection(NetworkingDirection.C2S)
public class C2SRequestConnectionAssignmentsPacket extends Packet {
    
    public C2SRequestConnectionAssignmentsPacket() {
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        DeltaNetwork.sendPacketToAllClients(new S2CRespondConnectionAssignmentsPacket());
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.C2SRequestConnectionAssignmentsPacket;
    }
    
}
