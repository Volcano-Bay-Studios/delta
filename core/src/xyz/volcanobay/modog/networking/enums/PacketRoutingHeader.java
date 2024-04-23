package xyz.volcanobay.modog.networking.enums;

import xyz.volcanobay.modog.core.annotations.Nullable;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

public enum PacketRoutingHeader {
    TO_ALL_CLIENTS, TO_CLIENT, TO_SERVER, TO_ALL_OTHERS;

    public int getId() {
        return ordinal();
    }
    
    public boolean shouldReadOnCurrentConnection(DeltaPacket packetType, @Nullable NetworkableUUID directedUUID) {
        if (packetType == DeltaPacket.S2CRespondConnectionAssignmentsPacket && NetworkConnectionsManager.isAwaiting)
            return true;
        if (NetworkConnectionsManager.isAwaiting)
            return false;
    
        if (this.equals(TO_CLIENT)) {
            return directedUUID == NetworkConnectionsManager.selfConnectionId;
        }
        if (this.equals(TO_SERVER))
            return DeltaNetwork.isNetworkOwner();
        if (this.equals(TO_ALL_CLIENTS))
            return !DeltaNetwork.isNetworkOwner();
        return true;
    }
    
}
