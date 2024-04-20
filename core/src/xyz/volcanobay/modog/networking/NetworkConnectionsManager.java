package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.utils.Timer;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.ServerHostType;
import xyz.volcanobay.modog.networking.packets.connection.C2SRequestConnectionAssignmentsPacket;

import java.util.HashMap;

public class NetworkConnectionsManager {
    
    public static HashMap<Integer, NetworkConnection> connections = new HashMap<>();
    
    public static Timer searchTimer;
    public static boolean isAwaiting = true;
    public static int selfConnectionId;
    
    public static void searchForConnectionsOnNetwork() {
        if (!DeltaNetwork.isConnected())
            throw new RuntimeException("Called to get connections on a network but is not connected to any");
    
        System.out.println("Searching for server on network...");
        DeltaNetwork.sendPacketToAllOthers(new C2SRequestConnectionAssignmentsPacket());
        Timer.schedule(new Timer.Task() {
            @Override public void run() { onGetConnectionsOnNetworkFail(); }
        }, 3);
    }
    
    public static void onGetConnectionsOnNetworkFail() {
        isAwaiting = false;
        System.out.println("Search for server on network failed, becoming the alpha...");
        DeltaNetwork.setEnabled(true);
        DeltaNetwork.setNetworkingSide(NetworkingSide.SERVER);
        DeltaNetwork.setHostingType(ServerHostType.EXTERNAL);
    }
    
    public static void cancelAssignmentsRequestListener() {
        System.out.println("Search for server successful, becoming the beta...");
        searchTimer.clear();
        isAwaiting = false;
        DeltaNetwork.setEnabled(true);
        DeltaNetwork.setNetworkingSide(NetworkingSide.CLIENT);
        DeltaNetwork.setHostingType(ServerHostType.EXTERNAL);
        DeltaNetwork.initialiseConnectedGame();
    }
    
}
