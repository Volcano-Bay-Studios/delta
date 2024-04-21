package xyz.volcanobay.modog.game.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.game.Material;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class CoalGeneratorObject extends MachineObject {
    float activeTime = 0;
    Texture on1 = new Texture("coal_generator_on_1.png");
    Texture on2 = new Texture("coal_generator_on_2.png");
    Texture on3 = new Texture("coal_generator_on_3.png");
    Texture off = new Texture("coal_generator_off.png");
    int animationStep = 1;
    int animationTicks;
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
        if (activeTime > 0) {
            animationTicks++;
            if (animationTicks > 9) {
                progressAnimation();
                animationTicks = 0;
            }
        } else
            animationTicks = 0;
        activeTime--;
        Material coal = getMaterial("coal");
        if (coal != null) {
            if (activeTime < 800) {
                if (!(activeTime > 0))
                    progressAnimation();
                activeTime = activeTime + 500;
                if (activeTime > 3000)
                    activeTime = 3000;
                removeMaterial("coal",1);
            }
        }
    }

    public void progressAnimation() {
        animationStep++;
        if (animationStep > 3)
            animationStep = 1;

        if (animationStep == 1)
            texture = on1;
        if (animationStep == 2)
            texture = on2;
        if (animationStep == 3)
            texture = on3;
    }

    @Override
    public void render() {
        if (activeTime > 0) {
            charge += 1f;
            objectLight.setActive(true);
            objectLight.setPosition(body.getPosition().add(0, 0));
            objectLight.setDistance((float) (2 + (Math.random() / 50f)));
            objectLight.setColor(0.99f, 0.49f, 0.23f, .5f);
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
        objectLight = new PointLight(RenderSystem.rayHandler, 50);
        objectLight.setDistance((float) (2 + (Math.random() / 50f)));
        objectLight.setColor(0.99f, 0.49f, 0.23f, 0.5f);
        inventorySize = 5;
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
        off.dispose();
        on1.dispose();
        on2.dispose();
        on3.dispose();
    }
}
