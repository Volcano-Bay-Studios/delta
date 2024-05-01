package xyz.volcanobay.modog.networking.packets.world;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.core.interfaces.level.DeltaLevel;
import xyz.volcanobay.modog.core.interfaces.level.Level;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevel;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevelComponent;
import xyz.volcanobay.modog.networking.DeltaPacket;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.annotations.PacketDirection;
import xyz.volcanobay.modog.networking.enums.NetworkingDirection;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.packets.core.LevelComponentStateUpdatingPacket;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.concurrent.ConcurrentHashMap;

@PacketDirection(NetworkingDirection.S2C)
public class A2ADelegatedLevelObjectUpdatePacket extends LevelComponentStateUpdatingPacket {
    
    /**
     * Data ethically sourced from static fields in PhysicsHandler.java
     */
    public A2ADelegatedLevelObjectUpdatePacket() {
    }

    @Override
    public NetworkableLevel getLevelForUpdate() {
        return Delta.LEVEL;
    }

    @Override
    public boolean shouldNetworkComponent(NetworkableLevelComponent component) {
        return component.isDelegatedTo(NetworkConnectionsManager.selfConnectionId);
    }

    @Override
    public DeltaPacket getType() {
        return DeltaPacket.UPDATE_STAGE;
    }
    
}
