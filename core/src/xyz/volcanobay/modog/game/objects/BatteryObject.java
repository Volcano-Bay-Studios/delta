package xyz.volcanobay.modog.game.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.rendering.RenderSystem;

import javax.crypto.Mac;

public class BatteryObject extends MachineObject {
    Texture batteryCharge = new Texture("battery_charges.png");
    PointLight objectLight;
    public BatteryObject() {
        super();
    }

    public BatteryObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }


    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render() {
        super.render();
        if (charge> 1) {
            float lampIntensity = charge / (charge +1);
            objectLight.setActive(true);
            objectLight.setPosition(body.getPosition());
            objectLight.setDistance((float) (lampIntensity*2+(Math.random()/50f)));
            objectLight.setColor(0,0.2f,1f,.5f);
            int index = (int) Math.ceil(charge/getMaxCharge()*7.1);
            RenderSystem.batch.draw(batteryCharge,body.getPosition().x-1.99f,body.getPosition().y-9.48f, 1.99f,9.48f, 4, 19,(1)/ PhysicsHandler.scaleDown,(1)/PhysicsHandler.scaleDown,(float) Math.toDegrees(body.getAngle()),index*4,0,4,19,false,false);
        } else {
            objectLight.setActive(false);
        }
    }


    @Override
    public BatteryObject create(Body body) {
        return new BatteryObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "battery";
        objectLight = new PointLight(RenderSystem.rayHandler,20);
    }

    @Override
    public void pickTexture() {
        texture = new Texture("battery.png");
    }

    @Override
    public void dispose() {
        super.dispose();
        objectLight.remove(true);
    }
    @Override
    public float getMaxCharge() {
        return 1000;
    }
}
