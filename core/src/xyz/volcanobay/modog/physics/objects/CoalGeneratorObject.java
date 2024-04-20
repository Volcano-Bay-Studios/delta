package xyz.volcanobay.modog.physics.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.Objects;

public class CoalGeneratorObject extends MachineObject {
    float activeTime = 0;
    Texture on = new Texture("coal_generator_on.png");
    Texture off = new Texture("coal_generator_off.png");
    PointLight objectLight;
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
    public void tick() {
        super.tick();
        activeTime--;
        for (PhysicsObject object : objectsImTouching) {
            if (object instanceof MaterialObject item) {
                if (Objects.equals(item.item, "coal") && activeTime < 800) {
                    activeTime = activeTime + 200;
                    if (activeTime > 1000)
                        activeTime = 1000;
                    PhysicsHandler.removeObject(object);
                }
            }
        }
    }

    @Override
    public void render() {
        if (activeTime> 0) {
            texture = on;
            objectLight.setActive(true);
            objectLight.setPosition(body.getPosition().add(0,0));
            objectLight.setDistance((float) (2+(Math.random()/50f)));
            objectLight.setColor(0.99f,0.49f,0.23f,.5f);
        } else {
            objectLight.setActive(false);
            texture = off;
            activeTime = 0;
        }
        super.render();
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "coal_generator";
        objectLight = new PointLight(RenderSystem.rayHandler,50);
        objectLight.setDistance((float) (2+(Math.random()/50f)));
        objectLight.setColor(0.99f,0.49f,0.23f,0.5f);
    }

    @Override
    public CoalGeneratorObject create(Body body) {
        return new CoalGeneratorObject(body);
    }
    @Override
    public void pickTexture() {
        texture = new Texture("coal_generator_off.png");
    }

    @Override
    public void dispose() {
        super.dispose();
        if (objectLight != null) {
            objectLight.remove(true);
            objectLight = null;
        }
    }
}
