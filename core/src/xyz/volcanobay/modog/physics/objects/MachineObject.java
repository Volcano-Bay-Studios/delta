package xyz.volcanobay.modog.physics.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class MachineObject  extends PhysicsObject {
    public MachineObject() {
        super();
    }

    public MachineObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    public MachineObject create(Body body) {
        return new MachineObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        pickTexture();
        type = "machine";
    }
    public void contact(Body body, PhysicsObject object) {
    }

    @Override
    public void pickTexture() {
        texture = new Texture("crate.png");
    }


    @Override
    public void createFixture() {
        PolygonShape groundBox = new PolygonShape();
        fixtureScale = new Vector2((float) texture.getWidth() /2-.3f, (float) texture.getHeight() /2-.3f);
        groundBox.setAsBox(fixtureScale.x/ PhysicsHandler.scaleDown, fixtureScale.y/PhysicsHandler.scaleDown);
        body.createFixture(groundBox,1f);
        groundBox.dispose();
    }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        return textButtons;
    }
}
