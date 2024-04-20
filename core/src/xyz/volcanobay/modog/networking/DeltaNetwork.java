package xyz.volcanobay.modog.networking;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.ServerHostType;
import xyz.volcanobay.modog.networking.enums.PacketRoutingHeader;
import xyz.volcanobay.modog.networking.packets.connection.C2SRequestConnectionAssignmentsPacket;
import xyz.volcanobay.modog.networking.packets.world.C2SRequestFillLevelContents;
import xyz.volcanobay.modog.networking.packets.world.S2CStageUpdatePacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DeltaNetwork {
    
    protected static WebSocket socket;
    
    protected static boolean connected = false;
    
    protected static boolean enabled = false;
    
    protected static String hostIp = null;
    
    protected static int hostPort;
    
    /**
     * Used to control if the world should be simulated, as well as validating packet sides
     */
    protected static NetworkingSide networkingSide = NetworkingSide.SERVER;
    
    /**
     * When hostingType is local, packets are not sent since there is no server to send to
     */
    protected static ServerHostType hostingType = ServerHostType.LOCAL;
    
    protected static List<ReceivedPacketData> packetProcessQueue = new ArrayList<>();
    
    protected static int LAST_PACKET_ID = 0;
    
    //>Connection
    public static void connectAsLocal() {
        
        System.out.println("Setting DeltaNetwork to work in local");
        
        networkingSide = NetworkingSide.SERVER;
        hostingType = ServerHostType.LOCAL;
        
    }
    
    
    public static void initialiseConnectedGame() {
        
        System.out.println("Successfully connected to game, syncing world state");
        DeltaNetwork.sendPacketToServer(new C2SRequestFillLevelContents());
        
    }
    
    public static void connect(String ip, int port) {
        
        System.out.println("Attempting to connect to " + ip + ":" + port);
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(ip, port));
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
        for (ReceivedPacketData receivedPacketData : new ArrayList<>(packetProcessQueue)) {
            PacketProcessor.processPacketData(receivedPacketData);
        }
        packetProcessQueue = new ArrayList<>();
    }
    
    public static void sendPacketToAllClients(Packet packet) {
        if (!DeltaNetwork.isConnected()) return;
        byte[] packetData = PacketProcessor.getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length + 4)
                .putInt(PacketRoutingHeader.TO_ALL_CLIENTS.getId())
                .put(packetData)
                .array()
        );
    }
    
    public static void sendPacketToAllOthers(Packet packet) {
        if (!DeltaNetwork.isConnected()) return;
        byte[] packetData = PacketProcessor.getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length + 4)
                .putInt(PacketRoutingHeader.TO_ALL_OTHERS.getId())
                .put(packetData)
                .array()
        );
    }
    
    public static void sendPacketToClient(Packet packet, int clientConnectionIndex) {
        if (!DeltaNetwork.isConnected() || DeltaNetwork.isActive()) return;
        byte[] packetData = PacketProcessor.getRawPacketData(packet);
        socket.send(
            ByteBuffer.allocate(packetData.length + 8)
                .putInt(PacketRoutingHeader.TO_CLIENT.getId())
                .putInt(clientConnectionIndex)
                .put(packetData)
                .array()
        );
    }
    
    private static boolean isActive() {
        return !NetworkConnectionsManager.isAwaiting;
    }
    
    public static void sendPacketToServer(Packet packet) {
        if (!DeltaNetwork.isConnected()) return;
        byte[] packetData = PacketProcessor.getRawPacketData(packet);
        PacketProcessor.send(
            ByteBuffer.allocate(packetData.length + 4)
                .putInt(PacketRoutingHeader.TO_SERVER.getId())
                .put(packetData)
                .array()
        );
    }
    
    public static boolean isExternalServer() {
        return hostingType.equals(ServerHostType.EXTERNAL) && networkingSide.equals(NetworkingSide.SERVER);
    }
    
    /**
     * Clients can only be on external servers so no need to specify
     */
    public static boolean isClientSide() {
        return hostingType.equals(ServerHostType.EXTERNAL) && networkingSide.equals(NetworkingSide.CLIENT);
    }
    
    public static boolean isNetworkOwner() {
        return networkingSide.equals(NetworkingSide.SERVER);
    }
    
    public static void setNetworkingSide(NetworkingSide networkingSide) {
        DeltaNetwork.networkingSide = networkingSide;
    }
    
    public static void setHostingType(ServerHostType hostingType) {
        DeltaNetwork.hostingType = hostingType;
    }
    
    public static void setEnabled(boolean enabled) {
        DeltaNetwork.enabled = enabled;
    }
    
    //>Getters
    public static boolean isConnected() {
        return connected;
    }
    
    public static boolean isEnabled() {
        return enabled;
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
    
    public record ReceivedPacketData(byte[] data) {
    
    }
    
}
