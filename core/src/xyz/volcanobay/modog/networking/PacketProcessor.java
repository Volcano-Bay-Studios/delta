package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.enums.PacketRoutingHeader;
import xyz.volcanobay.modog.networking.enums.RelativeNetworkSide;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

public class PacketProcessor {
    
    public static void processPacketData(DeltaNetwork.ReceivedPacketData receivedPacketData) {
        byte[] bytes = receivedPacketData.data();
        NetworkByteReadStream readStream = new NetworkByteReadStream(bytes);
        
        int hostRoutingDirection = readStream.readInt();
        PacketRoutingHeader routingHeader = PacketRoutingHeader.values()[hostRoutingDirection];
        int packetId = readStream.readInt();
        DeltaPacket packetSource = DeltaPacket.getPacketById(packetId);
        
        if (!routingHeader.shouldReadOnCurrentConnection(packetSource, readStream))
            return;
    
        Packet packet = packetSource.packetFactory.apply(readStream);
        packet.assertSide(RelativeNetworkSide.FROM);
        packet.receive(readStream);
    }
    
    public static void packAndSend(byte[] array) {
        DeltaNetwork.socket.send(array);
    }
    
    //>Sending to server
    protected static byte[] getRawPacketData(Packet packet) {
        packet.assertSide(RelativeNetworkSide.FROM);
        NetworkByteWriteStream writeStream = new NetworkByteWriteStream();
        writeStream.writeInt(packet.getType().ordinal());
        packet.write(writeStream);
        return writeStream.getBytes();
    }
    
}
