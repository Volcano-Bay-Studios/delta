package xyz.volcanobay.modog.physics.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.networking.NetworkableUUID;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class GroundObject extends PhysicsObject {
    public GroundObject() {
        super();
        visible = false;
    }

    public GroundObject(Body body) {
        super(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "ground";
        required = true;
        textureScale = new Vector2(0,0);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    public GroundObject create(Body body) {
        return new GroundObject(body);
    }

    @Override
    public void pickTexture() {
        texture = new Texture("girder.png");
    }

    @Override
    public void createFixture() {
        PolygonShape groundBox = new PolygonShape();
        fixtureScale = new Vector2(150,5);
        groundBox.setAsBox((fixtureScale.x/ PhysicsHandler.scaleDown), (fixtureScale.y/PhysicsHandler.scaleDown));
        body.createFixture(groundBox,1f);
        body.getFixtureList().get(0).setRestitution(.3f);
        groundBox.dispose();
    }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        return textButtons;
    }

}
