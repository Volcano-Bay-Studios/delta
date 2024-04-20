package xyz.volcanobay.modog.networking;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.RelativeNetworkSide;
import xyz.volcanobay.modog.networking.enums.ServerHostType;
import xyz.volcanobay.modog.networking.enums.HostRoutingHeader;
import xyz.volcanobay.modog.networking.packets.world.S2CStageUpdatePacket;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DeltaNetwork {
    
    protected static WebSocket socket;
    
    protected static boolean connected = false;
    
    protected static String hostIp = null;
    protected static int hostPort;
    
    /**Used to control if the world should be simulated, as well as validating packet sides*/
    protected static NetworkingSide networkingSide = null;
    /**When hostingType is local, packets are not sent since there is no server to send to*/
    protected static ServerHostType hostingType = null;
    
    protected static List<ReceivedPacketData> packetProcessQueue = new ArrayList<>();
    
    protected static int LAST_PACKET_ID = 0;
    
    //>Connection
    public static void connectAsLocal() {
        
        System.out.println("Setting DeltaNetwork to work in local");
    
        networkingSide = NetworkingSide.SERVER;
        hostingType = ServerHostType.LOCAL;
        
    }
    
    public static void connect(String ip, int port) {
    
        System.out.println("Attempting to connect to "+ip+":"+port);
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(ip,port));
        socket.setSendGracefully(true);
        socket.addListener(new NetworkListener(ip, port));
    
        socket.connect();
        
    }
    
    //>Per tick handling
    public static void sendDataTick() {
        if (!isConnected() || !isExternalServer())
            return;
        
        sendPacketToAllClients(new S2CStageUpdatePacket());
    }
    public static void readDataTick() {
        for (ReceivedPacketData receivedPacketData : packetProcessQueue) {
            PacketProcessor.processPacketData(receivedPacketData);
        }
    }
    
    //>Sending to server
    protected static byte[] getRawPacketData(Packet packet) {
        packet.assertSide(RelativeNetworkSide.FROM);
        NetworkByteWriteStream writeStream = new NetworkByteWriteStream();
        packet.write(writeStream);
        return writeStream.getBytes();
    }
    
    public static void sendPacketToAllClients(Packet packet) {
        byte[] packetData = getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length +1)
                .putInt(HostRoutingHeader.TO_ALL_CLIENTS.getId())
                .put(packetData)
                .array()
        );
    }
    
    public static void sendPacketToAllOthers(Packet packet) {
        byte[] packetData = getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length +1)
                .putInt(HostRoutingHeader.TO_ALL_OTHERS.getId())
                .put(packetData)
                .array()
        );
    }
    
    public static void sendPacketToClient(Packet packet, int clientConnectionIndex) {
        byte[] packetData = getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length +2)
                .putInt(HostRoutingHeader.TO_CLIENT.getId())
                .putInt(clientConnectionIndex)
                .put(packetData)
                .array()
        );
    }
    
    public static void sendPacketToServer(Packet packet) {
        byte[] packetData = getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length +1)
                .putInt(HostRoutingHeader.TO_SERVER.getId())
                .put(packetData)
                .array()
        );
    }
    
    public static boolean isExternalServer() {
        return hostingType.equals(ServerHostType.EXTERNAL) && networkingSide.equals(NetworkingSide.SERVER);
    }
    
    /**Clients can only be on external servers so no need to specify*/
    public static boolean isClientSide() {
        return hostingType.equals(ServerHostType.EXTERNAL) && networkingSide.equals(NetworkingSide.CLIENT);
    }
    
    public static boolean isNetworkOwner() {
        return networkingSide.equals(NetworkingSide.SERVER);
    }
    
    //>Getters
    public static boolean isConnected() {
        return connected;
    }
    
    public static String getHostIp() {
        return hostIp;
    }
    
    public static int getHostPort() {
        return hostPort;
    }
    
    public static NetworkingSide getNetworkingSide() {
        return networkingSide;
    }
    
    public static ServerHostType getHostingType() {
        return hostingType;
    }
    
    public record ReceivedPacketData(byte[] data) { }
    
}
