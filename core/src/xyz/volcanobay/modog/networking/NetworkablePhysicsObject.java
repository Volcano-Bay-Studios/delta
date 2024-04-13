package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class NetworkablePhysicsObject{
    public Vector2 pos;
    public Vector2 vel;
    public float angle;
    public String type;
    public float angularVelocity;
    public int index;
    public NetworkablePhysicsObject(Vector2 pos,Vector2 vel,float angle,float angularVelocity,String type,int index) {
        this.pos = pos;
        this.vel = vel;
        this.angle = angle;
        this.type = type;
        this.angularVelocity = angularVelocity;
        this.index = index;
    }
    public NetworkablePhysicsObject() {

    }
}
