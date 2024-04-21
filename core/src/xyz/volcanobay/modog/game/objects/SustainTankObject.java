package xyz.volcanobay.modog.game.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class SustainTankObject extends LogicObject {
    Texture fluid = new Texture("sustain.png");
    PointLight objectLight;
    float targetHeight = 10;
    public SustainTankObject() {
        super();
    }

    public SustainTankObject(Body body) {
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
    public void control(Vector2 movementVector, float angleGoal) {
        super.control(movementVector, angleGoal);
        if (charge>1) {
            targetHeight += movementVector.y / 30;
        }
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
            float appliedPower = (targetHeight-body.getPosition().y);
            if (body.getPosition().y < targetHeight) {
                body.applyForceToCenter(0,appliedPower*100,true);
                body.setLinearDamping(10f);
                charge -= appliedPower/20;
            }
            int index = (int) Math.min(20,appliedPower*1);
            RenderSystem.batch.draw(fluid,body.getPosition().x-18,body.getPosition().y-10, 18,10, 36, 20,(1)/ PhysicsHandler.scaleDown,(1)/PhysicsHandler.scaleDown,(float) Math.toDegrees(body.getAngle()),0,index,4,19,false,false);
        } else {
            objectLight.setActive(false);
            body.setLinearDamping(0f);
        }
    }


    @Override
    public SustainTankObject create(Body body) {
        return new SustainTankObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "sustain_tank";
        objectLight = new PointLight(RenderSystem.rayHandler,20);
    }

    @Override
    public void pickTexture() {
        texture = new Texture("sustain_tank.png");
    }

    @Override
    public void dispose() {
        super.dispose();
        objectLight.remove(true);
    }
}
