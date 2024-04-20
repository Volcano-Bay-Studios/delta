package xyz.volcanobay.modog.networking.enums;

import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnection;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.packets.connection.S2CRespondConnectionAssignmentsPacket;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;

public enum PacketRoutingHeader {
    TO_ALL_CLIENTS(0),
    TO_CLIENT(1),
    TO_SERVER(2),
    TO_ALL_OTHERS(3);
    final int id;
    
    PacketRoutingHeader(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean shouldReadOnCurrentConnection(DeltaPacket packetType, int additional) {
        if (packetType == DeltaPacket.S2CRespondConnectionAssignmentsPacket && NetworkConnectionsManager.isAwaiting)
            return true;
        if (NetworkConnectionsManager.isAwaiting)
            return false;
    
        if (this.equals(TO_CLIENT)) {
            return additional == NetworkConnectionsManager.selfConnectionId;
        }
        if (this.equals(TO_SERVER))
            return DeltaNetwork.isNetworkOwner();
        if (this.equals(TO_ALL_CLIENTS))
            return !DeltaNetwork.isNetworkOwner();
        return true;
    }
    
}
