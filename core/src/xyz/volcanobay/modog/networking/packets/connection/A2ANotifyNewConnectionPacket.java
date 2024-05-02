package xyz.volcanobay.modog.networking.packets.connection;


import xyz.volcanobay.modog.networking.*;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;

/**Notify of a new connection to others, and if this is a server receiving, respond with the connections it has*/
@PacketDirection(NetworkingDirection.A2A)
public class A2ANotifyNewConnectionPacket extends Packet {

    NetworkableUUID newConnectionUUID;

    public A2ANotifyNewConnectionPacket(NetworkableUUID newConnectionUUID) {
        this.newConnectionUUID = newConnectionUUID;
    }

    public A2ANotifyNewConnectionPacket() {}

    @Override
    public void receive(NetworkReadStream stream) {
        newConnectionUUID = stream.readUUID();
        NetworkConnectionsManager.connections.put(newConnectionUUID, new NetworkConnection(NetworkingSide.CLIENT, newConnectionUUID));
        if (DeltaNetwork.isNetworkOwner())
            DeltaNetwork.sendPacketToClient(new S2CRespondConnectionAssignmentsPacket(), newConnectionUUID);
    }
    
    @Override
    public void write(NetworkWriteStream stream) {
        stream.writeUUID(newConnectionUUID);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.C2SRequestConnectionAssignmentsPacket;
    }
    
}
