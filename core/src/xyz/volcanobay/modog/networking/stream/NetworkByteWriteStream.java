package xyz.volcanobay.modog.networking.stream;

import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class NetworkByteWriteStream {
    
    List<Byte> stream;
    
    public NetworkByteWriteStream() {
        stream = new ArrayList<>();
    }
    
    public void writeBytes(Byte[] bytes) {
        stream.addAll(List.of(bytes));
    }
    public void writeBytes(byte[] bytes) {
        for (Byte b : bytes)
            stream.add(b);
    }
    
    public void writeByte(byte b) {
        stream.add(b);
    }
    
    public void writeFloat(float f) {
        writeInt(Float.floatToIntBits(f));
    }
    
    public void writeInt(Integer i) {
        writeBytes(intToByteArray(i));
    }
    
    public void writeString(String string) {
        writeInt(string.length());
        writeBytes(string.getBytes());
    }
    
    public void writeUUID(NetworkableUUID uuid) {
        writeBytes(uuid.getBytes());
    }
    
    public void writeVector2(Vector2 vector2) {
        writeFloat(vector2.x);
        writeFloat(vector2.y);
    }
    
    public void writeByteBool(boolean required) {
        writeByte((byte) (required ? 0 : 1));
    }
    
    protected static byte[] intToByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
    
    /**Returns a byte array of the stream's contents*/
    public byte[] getBytes() {
        byte[] bytes = new byte[stream.size()];
        for(int i = 0; i < stream.size(); i++) bytes[i] = stream.get(i);
        return bytes;
    }
    
}
