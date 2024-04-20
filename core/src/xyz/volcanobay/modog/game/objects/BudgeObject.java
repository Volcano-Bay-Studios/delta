package xyz.volcanobay.modog.game.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import xyz.volcanobay.modog.game.InputHandeler;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class BudgeObject extends MachineObject {
    Texture on = new Texture("budge_ok.png");
    Texture working = new Texture("budge_working.png");
    PointLight objectLight;
    public int workingTime;
    public boolean isWorking = false;
    public float targetAngle = 0;
    public BudgeObject() {
        super();
    }

    public BudgeObject(Body body) {
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
            charge -= 0.2f;
        }
        if (isWorking)
            workingTime++;
        else
            workingTime = 0;
    }

    @Override
    public void render() {
        super.render();
        if (charge> 1) {
            float lampIntensity = charge / (charge +1);
            objectLight.setActive(true);
            objectLight.setPosition(body.getPosition());
            objectLight.setDistance((float) (lampIntensity*2+(Math.random()/50f)));
            objectLight.setColor(0,1,0,.5f);
            isWorking = Math.abs(Math.toDegrees(body.getAngle()) - targetAngle) > 1;
            float goal = ((float) (Math.toDegrees(body.getAngle()) - targetAngle)/-1.2f);
            body.setAngularDamping(10f);
            body.setAngularVelocity(goal);
            if (isWorking) {
                RenderSystem.batch.draw(working,body.getPosition().x-5.025f,body.getPosition().y-6,5,6, on.getWidth(), on.getHeight(),(1)/ PhysicsHandler.scaleDown,(1)/PhysicsHandler.scaleDown, 0,0,0,on.getWidth(),on.getHeight(),false,false);
                charge -= goal/200;
            } else {
                RenderSystem.batch.draw(on,body.getPosition().x-5.025f,body.getPosition().y-6,5,6, on.getWidth(), on.getHeight(),(1)/ PhysicsHandler.scaleDown,(1)/PhysicsHandler.scaleDown, 0,0,0,on.getWidth(),on.getHeight(),false,false);
            }
        } else {
            objectLight.setActive(false);
            body.setAngularDamping(0f);
        }
    }


    @Override
    public BudgeObject create(Body body) {
        return new BudgeObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "budge";
        objectLight = new PointLight(RenderSystem.rayHandler,20);
    }

    @Override
    public void pickTexture() {
        texture = new Texture("budge.png");
    }

    @Override
    public void dispose() {
        super.dispose();
        objectLight.remove(true);
        working.dispose();
        on.dispose();
    }
    public BudgeObject getSelf() { return this; }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        newButton("Control", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InputHandeler.controlledContraption = getSelf();
                actor.getParent().remove();
            }
        });
        return textButtons;
    }
}
