package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.game.Cursor;
import xyz.volcanobay.modog.game.CursorHandler;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

@PacketDirection(NetworkingDirection.A2A)
public class A2ACursorUpdatePacket extends Packet {
    
    Cursor cursor;
    
    public A2ACursorUpdatePacket(Cursor cursor) {
        this.cursor = cursor;
    }
    
    public A2ACursorUpdatePacket() {}
    
    @Override
    public void receive(NetworkByteReadStream stream) {
        NetworkableUUID uuid = stream.readUUID();
        
        if (!CursorHandler.cursors.containsKey(uuid))
            CursorHandler.cursors.put(uuid, new Cursor(uuid));
        
        cursor = CursorHandler.cursors.get(uuid);
        cursor.pos = stream.readVector2();
        cursor.color = stream.readInt();
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        stream.writeUUID(cursor.uuid);
        stream.writeVector2(cursor.pos);
        stream.writeInt(cursor.color);
    }
    
    @Override
    public DeltaPacket getType() {
        return DeltaPacket.CURSOR_UPDATE_PACKET;
    }
    
}
