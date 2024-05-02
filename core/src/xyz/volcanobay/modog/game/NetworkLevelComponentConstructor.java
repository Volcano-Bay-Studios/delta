package xyz.volcanobay.modog.game;

import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevelComponent;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.function.Function;

public enum NetworkLevelComponentConstructor {
    PHYSICS_OBJECT(PhysicsObject::readNewFromNetwork),
    WORLD_JOINT(WorldJoint::readNewFromNetwork);
    Function<NetworkReadStream, NetworkableLevelComponent> constructor;

    NetworkLevelComponentConstructor(Function<NetworkReadStream, NetworkableLevelComponent> constructor) {
        this.constructor = constructor;
    }

    public NetworkableLevelComponent build(NetworkReadStream stream) {
        return constructor.apply(stream);
    }

    public static NetworkLevelComponentConstructor getById(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }

}
