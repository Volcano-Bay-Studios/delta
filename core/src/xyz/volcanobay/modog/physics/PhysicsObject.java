package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
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
    public Vector2 textureScale;
    public Vector2 fixtureScale;
    public boolean visible = true;
    public boolean required = false;
    public String tooltip = "Nothing to see here!";
    public boolean markedForDeletion = false;
    public boolean restricted = false;
    public float conductivity;
    public float charge;
    /**
     * Constructor for Registry
     * Do NOT use this, instead get a physics object from the registry {@link PhysicsObjectsRegistry }
     */
    public PhysicsObject() {
        this.scale = new Vector2(1,1);
        this.textureScale = new Vector2(0,0);
    }
    /**
     * Constructor for making new physics objects
     */
    public PhysicsObject(Body body) {
        this.body = body;
        this.scale = new Vector2(1,1);
        this.textureScale = new Vector2(0,0);
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
        textureOffset = new Vector2((float) texture.getWidth() /2*scale.x, (float) texture.getHeight() /2*scale.y);
    }

    /**
     * Returns a new {@link FixtureDef} from the {@link PhysicsObject}
     * @return new {@link FixtureDef} from the {@link PhysicsObject}
     *
     * @author ModogTheDev
     */
    @Deprecated
    public FixtureDef getFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape box = new PolygonShape();
        box.setAsBox(fixtureScale.x/PhysicsHandler.scaleDown, fixtureScale.y/PhysicsHandler.scaleDown);
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        box.dispose();
        return fixtureDef;
    }
    public void createFixture() {

    }
    public void resize(Vector2 newSize) {
        scale = newSize;
        processTexture();
    }
    public void tickPhysics() {
        if (!required && body.getPosition().y < -90 && !markedForDeletion && (NetworkHandler.isHost || !NetworkHandler.isConnected)) {
            PhysicsHandler.bodiesForDeletion.add(body);
            markedForDeletion = true;
        }
        if (required && body.getPosition().y < -90) {
            body.setTransform(body.getPosition().x,-89.99f,body.getAngle());
            body.setLinearVelocity(0,0);
        }
//        System.out.println("hu");
    }
    public void render() {
        RenderSystem.batch.draw(texture,body.getPosition().x-textureOffset.x,body.getPosition().y-textureOffset.y,textureOffset.x,textureOffset.y, texture.getWidth(), texture.getHeight(),(scale.x+textureScale.x)/PhysicsHandler.scaleDown,(scale.y+textureScale.y)/PhysicsHandler.scaleDown, (float) Math.toDegrees(body.getAngle()),0,0,texture.getWidth(),texture.getHeight(),false,false);
    }
    public void dispose() {
        texture.dispose();
    }
    public void newButton(String string, ChangeListener changeListener) {
        textButtons.add(new TextButtons(string,changeListener));
    }
    public List<TextButtons> getContextOptions() {
        textButtons.clear();
        if (NetworkHandler.hasAuthority || !restricted) {
            if (!required) {
                newButton("Delete", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        PhysicsHandler.bodiesForDeletion.add(body);
                        actor.getParent().remove();
                    }
                });
            }
            newButton("Destroy Joints", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PhysicsHandler.bodiesForJointRemoval.add(body);
                    actor.getParent().remove();

                }
            });
            if ((body.getType() == BodyDef.BodyType.StaticBody)) {
                newButton("Unfreeze", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        body.setType(BodyDef.BodyType.DynamicBody);
                        NetworkHandler.clientAddObject(body);
                        actor.getParent().remove();
                    }
                });
            } else {
                newButton("Freeze", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        body.setType(BodyDef.BodyType.StaticBody);
                        NetworkHandler.clientAddObject(body);
                        actor.getParent().remove();
                    }
                });
            }
            if (NetworkHandler.isHost) {
                if (restricted) {
                    newButton("Unrestrict", new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            restricted = false;
                            actor.getParent().remove();
                        }
                    });
                } else {
                    newButton("Restrict", new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            restricted = true;
                            actor.getParent().remove();
                        }
                    });
                }
            }
            newButton("Follow", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RenderSystem.followObject = PhysicsHandler.getPhysicsObjectFromBody(body);
                    actor.getParent().remove();

                }
            });
            newButton("Zero Angle", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    body.setTransform(body.getPosition(),0);
                    body.setAngularVelocity(0);
                    actor.getParent().remove();

                }
            });
        }
        return textButtons;
    }
    public void tick() {}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Body thisBody) {
            return thisBody.equals(this.body);
        }
        return super.equals(obj);
    }
}
