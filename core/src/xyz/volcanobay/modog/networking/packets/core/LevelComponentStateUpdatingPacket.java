package xyz.volcanobay.modog.networking.packets.core;

import xyz.volcanobay.modog.core.interfaces.level.Level;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevel;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevelComponent;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

public abstract class LevelComponentStateUpdatingPacket extends Packet {
    
    @Override
    public void receive(NetworkByteReadStream stream) {
    
    }
    
    @Override
    public void write(NetworkByteWriteStream stream) {
        getLevelForUpdate()

    }
    
    public abstract NetworkableLevel getLevelForUpdate();
    public abstract boolean shouldNetworkComponent(NetworkableLevelComponent component);
    
}
