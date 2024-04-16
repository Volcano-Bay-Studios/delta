package xyz.volcanobay.modog.physics.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.networking.NetworkableUUID;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class GroundObject extends PhysicsObject {
    public GroundObject() {
        super();
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
        texture = new Texture("ground.png");
    }

    @Override
    public void createFixture() {
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(200.0f, 10.0f);
        body.createFixture(groundBox,1f);
        groundBox.dispose();
    }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        return textButtons;
    }

}
