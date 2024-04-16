package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.UUID;

public class NetworkablePhysicsObject {
    public Vector2 pos;
    public Vector2 vel;
    public float angle;
    public String type;
    public NetworkableUUID uuid;
    public float angularVelocity;
    public int index;
    public int bodyType;
    public NetworkablePhysicsObject(Vector2 pos,Vector2 vel,float angle,float angularVelocity,String type,int index, NetworkableUUID uuid, int bodyType) {
        this.pos = pos;
        this.vel = vel;
        this.angle = angle;
        this.type = type;
        this.angularVelocity = angularVelocity;
        this.index = index;
        this.uuid = uuid;
        this.bodyType = bodyType;
    }
    public NetworkablePhysicsObject() {

    }
}
