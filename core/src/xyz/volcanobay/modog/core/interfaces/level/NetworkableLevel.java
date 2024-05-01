package xyz.volcanobay.modog.core.interfaces.level;

import xyz.volcanobay.modog.game.NetworkLevelComponentConstructor;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface NetworkableLevel extends Level {

    ConcurrentHashMap<NetworkableUUID, NetworkableLevelComponent> getLevelComponents();

    void write(NetworkWriteStream stream);
    void read(NetworkReadStream stream);

    /**
     * Stream expecting to read, needs to be cooberated with write functions in {@link NetworkableLevelComponent#writeNewToNetwork(NetworkWriteStream)}:
     * [componentType : int -> LevelComponentNetworkConstructor]
     * */
    NetworkLevelComponentConstructor resolveNewLevelComponentConstructor(NetworkReadStream stream);

    void reloadSourcedMaps();
}
