package xyz.volcanobay.modog.game.objects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class CableObject extends MachineObject {
    public CableObject() {
        super();
    }

    public CableObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }



    @Override
    public CableObject create(Body body) {
        return new CableObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        type = "cable";
    }

    @Override
    public void pickTexture() {
        texture = new Texture("cable.png");
    }
}
