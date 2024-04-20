package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.ServerHostType;
import xyz.volcanobay.modog.networking.packets.connection.C2SRequestConnectionAssignmentsPacket;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkConnectionsManager {
    
    public static HashMap<Integer, NetworkConnection> connections = new HashMap<>();
    
    public static Timer searchTimer;
    public static boolean isAwaiting = true;
    public static int selfConnectionId;
    
    public static void getConnectionsOnNetwork() {
        if (!DeltaNetwork.isConnected())
            throw new RuntimeException("Called to get connections on a network but is not connected to any");
        
        DeltaNetwork.sendPacketToAllOthers(new C2SRequestConnectionAssignmentsPacket());
        searchTimer.schedule(new TimerTask() {
            @Override public void run() { onGetConnectionsOnNetworkFail(); }
        }, 10);
    }
    
    public static void onGetConnectionsOnNetworkFail() {
        isAwaiting = false;
        DeltaNetwork.setEnabled(true);
        DeltaNetwork.setNetworkingSide(NetworkingSide.SERVER);
        DeltaNetwork.setHostingType(ServerHostType.EXTERNAL);
    }
    
    public static void cancelAssignmentsRequestListener() {
        searchTimer.cancel();
        isAwaiting = false;
    }
    
}
