package xyz.volcanobay.modog.game.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.PhysicsObjectsRegistry;
import xyz.volcanobay.modog.util.SimplexNoise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static xyz.volcanobay.modog.physics.PhysicsHandler.world;

public class Level {
    static int scale = 40;
    int seed;
    HashMap<Float,Float> heights = new HashMap<>();
    List<Float> worldPhysics = new ArrayList<>();
    List<Body> bodies = new ArrayList<>();
    public void clearPhysData(){
        for (Body body: bodies) {
            world.destroyBody(body);
        }
        worldPhysics.clear();
        bodies.clear();
    }
    public void worldCheckPhysics(float x) {
        if (!worldPhysics.contains(x)) {
            worldPhysics.add(x);
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(x, 0);
            Body body = world.createBody(bodyDef);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.friction = 1f;
            fixtureDef.restitution = .2f;
            PolygonShape shape = new PolygonShape();
            Vector2[] verticies = new Vector2[5];
            verticies[0] = new Vector2(-.1f,getPoint(x-.1f));
            verticies[1] = new Vector2(0,getPoint(x));
            verticies[2] = new Vector2(.1f,getPoint(x+.1f));
            verticies[3] = new Vector2(-.1f,getPoint(x-.1f)-.5f);
            verticies[4] = new Vector2(.1f,getPoint(x+.1f)-.5f);
            shape.set(verticies);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
            shape.dispose();
            bodies.add(body);
        }
    }
    public float getPoint(float x) {
        if (heights.containsKey((float) x)) {
            return heights.get((float) x);
        } else {
            float lowNoise = (float) SimplexNoise.noise((double) x / (scale), seed);
            float highNoise = (float) SimplexNoise.noise((double) x / (scale*20), seed+2);
            float finalVal = lowNoise + (highNoise*10)+2;
            heights.put(x,finalVal);
            return finalVal;
        }
    }
}
