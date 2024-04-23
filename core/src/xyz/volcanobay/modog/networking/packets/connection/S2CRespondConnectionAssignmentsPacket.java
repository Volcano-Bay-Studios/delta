package xyz.volcanobay.modog.networking.packets.connection;

import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnection;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import java.util.HashMap;

@PacketDirection(NetworkingDirection.S2C)
public class S2CRespondConnectionAssignmentsPacket extends Packet {
    
    public S2CRespondConnectionAssignmentsPacket() {
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        if (!NetworkConnectionsManager.isAwaiting) return;

        System.out.println("Received response to request for network assignments");

        NetworkConnectionsManager.connections = new HashMap<>();
        int length = stream.readInt();
        
        for (int i = 0; i < length; i++) {
            NetworkingSide side = NetworkingSide.values()[stream.readInt()];
            NetworkableUUID id = stream.readUUID();
            NetworkConnectionsManager.connections.put(id, new NetworkConnection(side, id));
        }

        NetworkConnectionsManager.cancelAssignmentsRequestListener();
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        stream.writeInt(NetworkConnectionsManager.connections.size());
        
        for (NetworkConnection connection : NetworkConnectionsManager.connections.values()) {
            stream.writeInt(connection.getNetworkingSide().ordinal());
            stream.writeUUID(connection.getConnectionUUID());
        }
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CRespondConnectionAssignmentsPacket;
    }
    
}
