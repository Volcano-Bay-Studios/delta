package xyz.volcanobay.modog.physics.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class CircleObject extends PhysicsObject {
    public CircleObject() {
        super();
    }

    public CircleObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    public CircleObject create(Body body) {
        return new CircleObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "wheel";
    }

    @Override
    public void pickTexture() {
        texture = new Texture("wheel.png");
    }


    @Override
    public void createFixture() {
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circle = new CircleShape();
        fixtureScale = new Vector2(7.8f,0f);

        circle.setRadius(fixtureScale.x/ PhysicsHandler.scaleDown);
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        body.createFixture(fixtureDef);
        circle.dispose();
    }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        return textButtons;
    }
}
