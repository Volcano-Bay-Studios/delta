package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

public class NetworkConnection {

    NetworkingSide connectionSide;

    NetworkableUUID connectionId;
    
    public NetworkConnection(NetworkingSide connectionSide, NetworkableUUID connectionId) {
        this.connectionSide = connectionSide;
        this.connectionId = connectionId;
    }
    
    public NetworkingSide getNetworkingSide() {
        return connectionSide;
    }
    
    public NetworkableUUID getConnectionUUID() {
        return connectionId;
    }
    
}
