package xyz.volcanobay.modog.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;

import java.util.Objects;

public class CoalGeneratorObject extends MachineObject {
    public CoalGeneratorObject() {
        super();
    }

    public CoalGeneratorObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    public void contact(Body body, PhysicsObject object) {
        super.contact(body, object);
        if (object instanceof MaterialObject item) {
            if (Objects.equals(item.item, "coal")) {
                PhysicsHandler.removeObject(object);
            }
        }
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "coal_generator";
    }

    @Override
    public CoalGeneratorObject create(Body body) {
        return new CoalGeneratorObject(body);
    }
    @Override
    public void pickTexture() {
        texture = new Texture("coal_generator_off.png");
    }
}
