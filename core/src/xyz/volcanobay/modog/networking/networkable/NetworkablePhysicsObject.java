package xyz.volcanobay.modog.networking.networkable;

import com.badlogic.gdx.math.Vector2;

public class NetworkablePhysicsObject {
    public Vector2 pos;
    public Vector2 vel;
    public float angle;
    public String type;
    public NetworkableUUID uuid;
    public float angularVelocity;
    public boolean restricted;
    public int index;
    public int bodyType;
    public float charge;
    public NetworkablePhysicsObject(Vector2 pos,Vector2 vel,float angle,float angularVelocity,String type,int index, NetworkableUUID uuid, int bodyType, boolean restricted,float charge) {
        this.pos = pos;
        this.vel = vel;
        this.angle = angle;
        this.type = type;
        this.angularVelocity = angularVelocity;
        this.index = index;
        this.uuid = uuid;
        this.bodyType = bodyType;
        this.restricted = restricted;
        this.charge = charge;
    }
    public NetworkablePhysicsObject() {

    }
}
