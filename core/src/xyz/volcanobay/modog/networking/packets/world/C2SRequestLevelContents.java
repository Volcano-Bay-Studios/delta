package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

@PacketDirection(NetworkingDirection.C2S)
public class C2SRequestLevelContents extends Packet {
    
    public C2SRequestLevelContents() {
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        System.out.println("Syncing level data to client");
        DeltaNetwork.sendPacketToClient(new S2CFillLevelContentsPacket(), stream.readUUID());
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        stream.writeUUID(NetworkConnectionsManager.selfConnectionId);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.C2SRequestFillLevelContents;
    }
    
}
