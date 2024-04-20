package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.physics.box2d.Joint;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

public class WorldJoint {
    public Joint joint;
    public NetworkableUUID uuid;
    public WorldJoint() {}
    public WorldJoint(Joint joint, NetworkableUUID uuid) {
        this.joint = joint;
        this.uuid = uuid;
    }
}
