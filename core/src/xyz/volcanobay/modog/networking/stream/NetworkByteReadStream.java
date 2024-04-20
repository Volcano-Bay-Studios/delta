package xyz.volcanobay.modog.networking.stream;

import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NetworkByteReadStream {
    
    final byte[] data;
    final int length;
    int position = 0;
    
    public NetworkByteReadStream(byte[] bytes) {
        data = bytes;
        length = bytes.length;
    }
    
    public byte[] readBytes(int readLength) {
        if (position + readLength >= length)
            throw new IndexOutOfBoundsException("Tried to read past the end of a network byte stream");
        byte[] bytes = Arrays.copyOfRange(data, position, readLength);
        position += readLength;
        return bytes;
    }
    
    public byte readByte() {
        byte b = data[position];
        position +=1;
        return b;
    }
    
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }
    
    public int readInt() {
        return ByteBuffer.wrap(readBytes(4)).getInt();
    }
    
    public String readString() {
        int length = readInt();
        return new String(readBytes(length));
    }
    
    public Vector2 readVector2() {
        return new Vector2(readFloat(), readFloat());
    }
    
    public NetworkableUUID readUUID() {
        return new NetworkableUUID(readBytes(16));
    }
    
    public boolean readByteBool() {
        return readInt() != 0;
    }
    
}
