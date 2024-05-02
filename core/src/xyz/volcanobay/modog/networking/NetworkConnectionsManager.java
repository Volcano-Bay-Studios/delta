package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.utils.Timer;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.enums.ServerHostType;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.packets.connection.A2ANotifyNewConnectionPacket;

import java.util.HashMap;

public class NetworkConnectionsManager {
    
    public static int NEXT_ASSIGNED_CONNECTION_ID = 1;
    public static HashMap<NetworkableUUID, NetworkConnection> connections = new HashMap<>();
    
    public static Timer.Task searchTimer;
    public static boolean isAwaiting = true;
    public static NetworkableUUID selfConnectionId = NetworkableUUID.randomUUID();
    
    public static void searchForConnectionsOnNetwork() {
        if (!DeltaNetwork.isConnected())
            throw new RuntimeException("Called to get connections on a network but is not connected to any");
    
        System.out.println("Searching for server on network...");
        DeltaNetwork.sendPacketToAllOthers(new A2ANotifyNewConnectionPacket(selfConnectionId));
        Timer.schedule(searchTimer = new Timer.Task() {
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
        System.out.println("Search for server successful, found " + connections.size() + " connections becoming the beta...");
        searchTimer.cancel();
        isAwaiting = false;
        DeltaNetwork.setEnabled(true);
        DeltaNetwork.setNetworkingSide(NetworkingSide.CLIENT);
        DeltaNetwork.setHostingType(ServerHostType.EXTERNAL);
        DeltaNetwork.initialiseConnectedGame();
    }
    
}
