package xyz.volcanobay.modog.core.interfaces.level;

import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import java.util.HashMap;

public interface NetworkableLevel extends Level {

    HashMap<NetworkableUUID, NetworkableLevelComponent> getLevelComponents();

    void write(NetworkByteWriteStream stream);
    void read(NetworkByteReadStream stream);

    /**
     * Stream expecting to read, needs to be cooberated with write functions in {@link NetworkableLevelComponent#writeNewToNetwork(NetworkByteWriteStream)}:
     * [componentType : String]
     * */
    NetworkableLevelComponent resolveNewLevelComponent(NetworkByteReadStream stream);

}
