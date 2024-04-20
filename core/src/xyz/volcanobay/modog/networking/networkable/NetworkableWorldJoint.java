package xyz.volcanobay.modog.networking.networkable;

import com.badlogic.gdx.math.Vector2;

public class NetworkableWorldJoint {
    public NetworkableUUID uuid;
    public NetworkableUUID bodyAUUID;
    public NetworkableUUID bodyBUUID;
    public Vector2 localPointA;
    public Vector2 localPointB;
    public String type;
    public float length;
    public NetworkableWorldJoint() {}
}
