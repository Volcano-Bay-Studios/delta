package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.enums.RelativeNetworkSide;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;

public class PacketProcessor {
    
    public static void processPacketData(DeltaNetwork.ReceivedPacketData receivedPacketData) {
        byte[] bytes = receivedPacketData.data();
        NetworkByteReadStream readStream = new NetworkByteReadStream(bytes);
        
        Packet packet = readPacketHeaders(readStream);
        packet.assertSide(RelativeNetworkSide.FROM);
        packet.receive(readStream);
    }
    
    public static Packet readPacketHeaders(NetworkByteReadStream readStream) {
        int packetId = readStream.readInt();
        DeltaPacket packetSource = DeltaPacket.getPacketById(packetId);
        return packetSource.packetFactory.apply(readStream);
    }
    
}
