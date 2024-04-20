package xyz.volcanobay.modog.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class MaterialObject  extends PhysicsObject {
    public boolean clientUsed = false;
    public String item;
    public MaterialObject() {
        super();
    }

    public MaterialObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    public MaterialObject create(Body body) {
        return new MaterialObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        pickTexture();
        type = "item";
    }

    @Override
    public void pickTexture() {
        item = "coal";
        texture = new Texture(item+".png");
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
