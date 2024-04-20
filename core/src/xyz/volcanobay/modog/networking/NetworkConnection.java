package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.enums.NetworkingSide;

public class NetworkConnection {

    NetworkingSide connectionSide;
    
    int connectionId;
    
    public NetworkConnection(NetworkingSide connectionSide, int connectionId) {
        this.connectionSide = connectionSide;
        this.connectionId = connectionId;
    }
    
    public NetworkingSide getConnectionSide() {
        return connectionSide;
    }
    
    public int getConnectionId() {
        return connectionId;
    }
    
}
