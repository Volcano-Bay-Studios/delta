package xyz.volcanobay.modog.game.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class LampObject extends MachineObject {
    Texture on = new Texture("lamp_on.png");
    Texture off = new Texture("lamp_off.png");
    PointLight objectLight;
    public LampObject() {
        super();
    }

    public LampObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }


    @Override
    public void tick() {
        super.tick();
        if (charge > 1) {
            charge -= 0.05f;
        }
    }

    @Override
    public void render() {
        super.render();
        if (charge> 1) {
            float lampIntensity = charge / charge +1;
            texture = on;
            objectLight.setActive(true);
            objectLight.setPosition(body.getPosition());
            objectLight.setDistance((float) (lampIntensity+(Math.random()/50f)));
            objectLight.setColor(0.99f,0.65f,0.23f,.5f);
        } else {
            objectLight.setActive(false);
            texture = off;
        }
    }

    @Override
    public LampObject create(Body body) {
        return new LampObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "lamp";
        objectLight = new PointLight(RenderSystem.rayHandler,20);
    }

    @Override
    public void pickTexture() {
        texture = new Texture("lamp_on.png");
    }

    @Override
    public void dispose() {
        super.dispose();
        objectLight.remove(true);
        off.dispose();
        on.dispose();
    }
}
