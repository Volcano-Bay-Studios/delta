package xyz.volcanobay.modog.game.objects;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.List;

public class MiniThrusterObject extends LogicObject {
    public boolean working = false;
    ConeLight objectLight;
    Texture flames = new Texture("mini_thruster_flame.png");
    int animationTicks;
    int animationStep;
    public MiniThrusterObject() {
        super();
    }

    public MiniThrusterObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }

    @Override
    public void tick() {
        super.tick();
        animationTicks++;
        if (animationTicks > 9) {
            progressAnimation();
            animationTicks = 0;
        }
    }

    @Override
    public void control(Vector2 movementVector, float angleGoal) {
        float ourAngle = (float) body.getAngle();
        Vector2 vecAngle = new Vector2((float) -(Math.sin(ourAngle)), (float) (Math.cos(ourAngle)));
        float appliedPower = Math.max(0,movementVector.dot(vecAngle)*200);
        Vector2 force = new Vector2(vecAngle.x*appliedPower,vecAngle.y*appliedPower);
        body.applyForceToCenter(force,true);
        if (appliedPower > .25f)
            working = true;
    }

    @Override
    public MiniThrusterObject create(Body body) {
        return new MiniThrusterObject(body);
    }

    @Override
    public void render() {
        super.render();
        if (charge> 1 && working) {
            float lampIntensity = charge / (charge +1);
            objectLight.setActive(true);
            objectLight.setPosition(body.getPosition());
            objectLight.setDistance((float) (lampIntensity*5+(Math.random()/2f)));
            objectLight.setDirection((float) Math.toDegrees(body.getAngle())-90);
            objectLight.setColor(0,0.2f,1f,.5f);
            int index = (int) Math.ceil(charge/getMaxCharge()*7.1);
            RenderSystem.batch.draw(flames,body.getPosition().x-3,body.getPosition().y-10, 3,10, 6, 11,(1)/ PhysicsHandler.scaleDown,(1)/PhysicsHandler.scaleDown,(float) Math.toDegrees(body.getAngle()),animationStep*6-6,0,6,11,false,false);
        } else {
            objectLight.setActive(false);
        }
        working = false;
    }
    public void progressAnimation() {
        animationStep++;
        if (animationStep>3)
            animationStep = 1;
    }
    @Override
    public void initialise() {
        super.initialise();
        pickTexture();
        type = "mini_thruster";
        objectLight = new ConeLight(RenderSystem.rayHandler,20,new Color(0,0.2f,1f,.5f),100f,body.getPosition().x,body.getPosition().y, (float) Math.toDegrees(body.getAngle())-90,5f);
    }
    @Override
    public void pickTexture() {
        texture = new Texture("mini_thruster.png");
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

    @Override
    public void dispose() {
        super.dispose();
        flames.dispose();
    }
}
