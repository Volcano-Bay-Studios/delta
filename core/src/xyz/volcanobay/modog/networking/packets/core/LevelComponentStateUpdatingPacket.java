package xyz.volcanobay.modog.networking.packets.core;

import xyz.volcanobay.modog.core.interfaces.level.Level;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevel;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevelComponent;
import xyz.volcanobay.modog.networking.Packet;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;

public abstract class LevelComponentStateUpdatingPacket extends Packet {
    
    @Override
    public void receive(NetworkReadStream stream) {
        getLevelForUpdate().read(stream);
    }
    
    @Override
    public void write(NetworkWriteStream stream) {
        getLevelForUpdate().write(stream);
    }
    
    public abstract NetworkableLevel getLevelForUpdate();
    public abstract boolean shouldNetworkComponent(NetworkableLevelComponent component);
    
}
