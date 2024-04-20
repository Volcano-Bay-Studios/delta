package xyz.volcanobay.modog.networking.packets.connection;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnection;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PacketDirection(NetworkingDirection.S2C)
public class S2CRespondConnectionAssignmentsPacket extends Packet {
    
    public S2CRespondConnectionAssignmentsPacket() {
    }
    
    public S2CRespondConnectionAssignmentsPacket(NetworkByteReadStream readStream) {
        super(readStream);
    }
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        NetworkConnectionsManager.cancelAssignmentsRequestListener();
        
        NetworkConnectionsManager.connections = new HashMap<>();
        int length = stream.readInt();
        
        for (int i = 0; i < length; i++) {
            NetworkingSide side = NetworkingSide.values()[stream.readInt()];
            int id = stream.readInt();
            NetworkConnectionsManager.connections.put(id, new NetworkConnection(side, id));
        }
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        stream.writeInt(NetworkConnectionsManager.connections.size());
        
        for (NetworkConnection connection : NetworkConnectionsManager.connections.values()) {
            stream.writeInt(connection.getConnectionSide().ordinal());
            stream.writeInt(connection.getConnectionId());
        }
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.S2CRespondConnectionAssignmentsPacket;
    }
    
}
