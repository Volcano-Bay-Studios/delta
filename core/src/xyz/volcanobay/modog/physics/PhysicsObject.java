package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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
    public Texture texture;
    public String type;
    public Vector2 textureOffset;
    private Vector2 scale;
    public boolean required = false;
    /**
     * Constructor for Registry
     * Do NOT use this, instead get a physics object from the registry {@link PhysicsObjectsRegistry }
     */
    public PhysicsObject() {
        this.scale = new Vector2(1,1);
    }
    /**
     * Constructor for making new physics objects
     */
    public PhysicsObject(Body body) {
        this.body = body;
        this.scale = new Vector2(1,1);
        pickTexture();
        processTexture();
        initialise();
    }
    public PhysicsObject create(Body body) {
        return new PhysicsObject(body);
    }
    public void initialise() {

    }
    public PhysicsObject setUuid(NetworkableUUID uuid) {
        this.uuid = uuid;
        return this;
    }
    public void pickTexture() {
        texture = new Texture("none.png");
    }
    public void processTexture() {
        textureOffset = new Vector2((float) texture.getWidth() /2*scale.x, (float) texture.getWidth() /2*scale.y);
    }

    /**
     * Returns a new {@link FixtureDef} from the {@link PhysicsObject}
     * @return new {@link FixtureDef} from the {@link PhysicsObject}
     *
     * @author ModogTheDev
     */
    public FixtureDef getFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape box = new PolygonShape();
        box.setAsBox(10f, 10.0f);
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        box.dispose();
        return fixtureDef;
    }
    public void resize(Vector2 newSize) {
        scale = newSize;
        processTexture();
    }
    public void tickPhysics() {
//        System.out.println("hu");
    }
    public void render() {
        RenderSystem.batch.draw(texture,body.getPosition().x-textureOffset.x,body.getPosition().y-textureOffset.y,textureOffset.x,textureOffset.y, 16F, 16F,scale.x,scale.y, (float) Math.toDegrees(body.getAngle()),0,0,texture.getWidth(),texture.getHeight(),false,false);
    }
    public void dispose() {
        texture.dispose();
    }
    public void newButton(String string, ChangeListener changeListener) {
        textButtons.add(new TextButtons(string,changeListener));
    }
    public List<TextButtons> getContextOptions() {
        textButtons.clear();
        if (!required) {
            newButton("Delete", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PhysicsHandler.bodiesForDeletion.add(body);
                    actor.getParent().remove();
                }
            });
        }
        if ((body.getType() == BodyDef.BodyType.StaticBody)) {
            newButton("Unfreeze", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    body.setType(BodyDef.BodyType.DynamicBody);
                    actor.getParent().remove();
                }
            });
        } else {
            newButton("Freeze", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    body.setType(BodyDef.BodyType.StaticBody);
                    actor.getParent().remove();
                }
            });
        }
        newButton("Follow", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {RenderSystem.followObject = PhysicsHandler.getPhysicsObjectFromBody(body); actor.getParent().remove();}
        });
        return textButtons;
    }
}
