package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import xyz.volcanobay.modog.networking.NetworkableUUID;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.ArrayList;
import java.util.List;

public class PhysicsObject {
    public Body body;
    public List<TextButtons> textButtons = new ArrayList<>();
    public NetworkableUUID uuid;
    public static Texture texture;

    public PhysicsObject() {
    }
    public PhysicsObject(Body body) {
        this.body = body;
    }
    public PhysicsObject create(Body body) {
        return new PhysicsObject(body);
    }
    public PhysicsObject setUuid(NetworkableUUID uuid) {
        this.uuid = uuid;
        return this;
    }
    public void pickTexture() {
        texture = new Texture("none.png");
    }
    public void tickPhysics() {
//        System.out.println("hu");
    }
    public void render() {
        RenderSystem.batch.draw(RenderSystem.img,body.getPosition().x,body.getPosition().y);
    }
    public void dispose() {
        texture.dispose();
    }
    public void newButton(String string, ChangeListener changeListener) {
        textButtons.add(new TextButtons(string,changeListener));
    }
    public List<TextButtons> getContextOptions() {
        textButtons.clear();
        newButton("Delete", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PhysicsHandler.bodiesForDeletion.add(body); actor.getParent().remove();}
        });
        newButton("Follow", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {RenderSystem.followObject = PhysicsHandler.getPhysicsObjectFromBody(body); actor.getParent().remove();}
        });
        return textButtons;
    }
}
