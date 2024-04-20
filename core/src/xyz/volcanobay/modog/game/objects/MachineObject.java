package xyz.volcanobay.modog.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.game.DeltaConstants;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.ArrayList;
import java.util.List;

public class MachineObject  extends PhysicsObject {
    List<PhysicsObject> objectsImTouching = new ArrayList<>();
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
    public void tick() {
        super.tick();
        for (PhysicsObject object : objectsImTouching) {
            float myDonatedCharge = charge/10;
            float objectDonatedCharge = object.charge/10;
            charge += objectDonatedCharge;
            charge -= myDonatedCharge;
            object.charge += myDonatedCharge;
            object.charge -= objectDonatedCharge;
            if (charge> DeltaConstants.maxCharge)
                charge = DeltaConstants.maxCharge;
            if (object.charge> DeltaConstants.maxCharge)
                object.charge = DeltaConstants.maxCharge;
        }
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
    public void contact(PhysicsObject object) {
        objectsImTouching.add(object);
    }
    public void removeContact(PhysicsObject object) {
        objectsImTouching.remove(object);
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
        body.getFixtureList().get(0).setDensity(4f);
        groundBox.dispose();
    }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        return textButtons;
    }
}
