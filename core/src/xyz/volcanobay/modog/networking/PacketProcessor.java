package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.LogUtils;
import xyz.volcanobay.modog.core.annotations.Nullable;
import xyz.volcanobay.modog.networking.enums.PacketRoutingHeader;
import xyz.volcanobay.modog.networking.enums.RelativeNetworkSide;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

public class PacketProcessor {
    
    public static void processPacketData(DeltaNetwork.ReceivedPacketData receivedPacketData) {
        byte[] bytes = receivedPacketData.data();
        
        NetworkByteReadStream readStream = new NetworkByteReadStream(bytes);
        
        int hostRoutingDirection = readStream.readInt();
        @Nullable NetworkableUUID directedUUID = null;
        PacketRoutingHeader routingHeader = PacketRoutingHeader.values()[hostRoutingDirection];
        
        if (routingHeader.equals(PacketRoutingHeader.TO_CLIENT))
            directedUUID = readStream.readUUID();
        
        int packetId = readStream.readInt();
        DeltaPacket packetSource = DeltaPacket.getPacketById(packetId);
        
        if (!routingHeader.shouldReadOnCurrentConnection(packetSource, directedUUID))
            return;
    
        Packet packet = packetSource.packetFactory.get();
        packet.assertSide(RelativeNetworkSide.TO);
        packet.receive(readStream);
    }
    
    public static void send(byte[] array) {
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
