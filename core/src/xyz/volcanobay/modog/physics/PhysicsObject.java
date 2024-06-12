package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevelComponent;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.NetworkingCalls;
import xyz.volcanobay.modog.game.objects.MachineObject;
import xyz.volcanobay.modog.game.sounds.SoundHandeler;
import xyz.volcanobay.modog.game.sounds.SoundRegistry;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.packets.world.A2AObjectUpdateStatePacket;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.ArrayList;
import java.util.List;

public class PhysicsObject extends NetworkableLevelComponent {

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
    public boolean held = false;
    public float conductivity;
    public float charge;
    public String workingSound;
    boolean playingSound;
    long soundAddr;
    public int hitTicks = 0;
    public Vector2 lastVelocity;
    public Vector2 acceleration;
    public SoundRegistry.HitType hitType = SoundRegistry.HitType.METAL;
    public int dedelegateTimer = 0;


    public boolean syncNextTick = false;

    /**
     * Constructor for Registry Do NOT use this, instead get a physics object from the registry
     * {@link PhysicsObjectsRegistry }
     */
    public PhysicsObject() {
        this.scale = new Vector2(1, 1);
        this.textureScale = new Vector2(0, 0);
    }

    /**
     * Constructor for making new physics objects
     */
    public PhysicsObject(Body body) {
        this.body = body;
        this.scale = new Vector2(1, 1);
        this.textureScale = new Vector2(0, 0);
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
        textureOffset = new Vector2((float) texture.getWidth() / 2 * scale.x, (float) texture.getHeight() / 2 * scale.y);
    }

    /**
     * Returns a new {@link FixtureDef} from the {@link PhysicsObject}
     *
     * @return new {@link FixtureDef} from the {@link PhysicsObject}
     * @author ModogTheDev
     */
    @Deprecated
    public FixtureDef getFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape box = new PolygonShape();
        box.setAsBox(fixtureScale.x / PhysicsHandler.scaleDown, fixtureScale.y / PhysicsHandler.scaleDown);
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        box.dispose();
        return fixtureDef;
    }

    public void createFixture() {
        PolygonShape groundBox = new PolygonShape();
        fixtureScale = new Vector2(7.8f, 7.8f);
        groundBox.setAsBox(fixtureScale.x / PhysicsHandler.scaleDown, fixtureScale.y / PhysicsHandler.scaleDown);
        body.createFixture(groundBox, 1f);
        groundBox.dispose();
    }

    public void resize(Vector2 newSize) {
        scale = newSize;
        processTexture();
    }

    public void tickPhysics() {
        if (!required && body.getPosition().y < -90 && !markedForDeletion && DeltaNetwork.isNetworkOwner()) {
            PhysicsHandler.bodiesForDeletion.add(body);
            markedForDeletion = true;
        }
        if (required && body.getPosition().y < -90) {
            body.setTransform(body.getPosition().x, -89.99f, body.getAngle());
            body.setLinearVelocity(0, 0);
        }
        if (this instanceof MachineObject machineObject) {
            if (workingSound != null && machineObject.working && !playingSound) {
                soundAddr = SoundHandeler.playSound(getSelf(), workingSound, true);
                playingSound = true;
            }
            if (workingSound != null && !machineObject.working && playingSound) {
                SoundHandeler.stopSound(soundAddr);
                soundAddr = 0;
                playingSound = false;
            }
        }
        if (hitTicks > 0) {
            if (acceleration != null) {
                float strength = acceleration.dst(new Vector2(body.getLinearVelocity().x - lastVelocity.x, body.getLinearVelocity().y - lastVelocity.y));
                if (strength > 3) {
                    System.out.println(strength);
                    SoundHandeler.playHitSound(body.getPosition(), hitType, strength / 30);
                }
            }
            hitTicks--;
        }
        if (body != null) {
            acceleration = new Vector2(body.getLinearVelocity().x - lastVelocity.x, body.getLinearVelocity().y - lastVelocity.y);
        }
//        System.out.println("hu");
        if (dedelegateTimer > 0) {
            dedelegateTimer--;
            if (dedelegateTimer == 0) {
                selfDelegate();
            }
        }
    }

    public Vector2 getBodyAcceleration() {
        return acceleration;
    }


    public void render() {
        RenderSystem.batch.draw(texture, body.getPosition().x - textureOffset.x, body.getPosition().y - textureOffset.y, textureOffset.x, textureOffset.y, texture.getWidth(), texture.getHeight(), (scale.x + textureScale.x) / PhysicsHandler.scaleDown, (scale.y + textureScale.y) / PhysicsHandler.scaleDown, (float) Math.toDegrees(body.getAngle()), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public void dispose() {
        texture.dispose();
    }

    public float getGravity() {
        return Math.min(30, Math.max(0, (body.getPosition().y * -0.3f) + 30));
    }


    public void newButton(String string, ChangeListener changeListener) {
        textButtons.add(new TextButtons(string, changeListener));
    }

    public void scheduleDeDelegate() {
        dedelegateTimer = 400;
    }

    public List<TextButtons> getContextOptions() {
        textButtons.clear();
        if (DeltaNetwork.isNetworkOwner() || !restricted) {
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
                        NetworkingCalls.updateObjectState(body);
                        scheduleDeDelegate();
                        actor.getParent().remove();
                    }
                });
            } else {
                newButton("Freeze", new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        body.setType(BodyDef.BodyType.StaticBody);
                        NetworkingCalls.updateObjectState(body);
                        scheduleDeDelegate();
                        actor.getParent().remove();
                    }
                });
            }
            if (DeltaNetwork.isNetworkOwner()) {
                if (restricted) {
                    newButton("Unrestrict", new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            restricted = false;
                            NetworkingCalls.updateObjectState(body);
                            scheduleDeDelegate();
                            actor.getParent().remove();
                        }
                    });
                } else {
                    newButton("Restrict", new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            restricted = true;
                            NetworkingCalls.updateObjectState(body);
                            scheduleDeDelegate();
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
                    body.setTransform(body.getPosition(), 0);
                    body.setAngularVelocity(0);
                    NetworkingCalls.updateObjectState(body);
                    scheduleDeDelegate();
                    actor.getParent().remove();

                }
            });
        } else {
            newButton("Restricted!", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    actor.getParent().remove();
                }
            });
        }
        return textButtons;
    }

    public void tick() {
        if (syncNextTick) {
            DeltaNetwork.sendPacketToAllClients(new A2AObjectUpdateStatePacket(this));
            syncNextTick = false;
        }

    }

    public float getMaxCharge() {
        return 10f;
    }

    //>Networking
    public void selfDelegate() {
        setDelegateOf(NetworkConnectionsManager.selfConnectionId);
    }


    /**
     * Sync properties NOT position and velocity
     */
    public void writeStateToNetwork(NetworkWriteStream writeStream) {

    }

    /**
     * Sync position and velocity, called every tick by the StageUpdatePacket
     */
    public void writePhysicsStateToNetwork(NetworkWriteStream writeStream) {
        writeStream.writeInt(body.getType().ordinal());
        writeStream.writeVector2(body.getPosition());
        writeStream.writeFloat(body.getAngle());

        writeStream.writeVector2(body.getLinearVelocity());
        writeStream.writeFloat(body.getAngularVelocity());

        writeStream.writeByteBool(required);
        writeStream.writeByteBool(restricted);

        //Server will not read
        writeStream.writeFloat(charge);
    }

    public void writeNewToNetwork(NetworkWriteStream stream) {
        stream.writeString(type);
        writeAllStateToNetwork(stream);
    }


    public void writeAllStateToNetwork(NetworkWriteStream stream) {
        writePhysicsStateToNetwork(stream);
        writeStateToNetwork(stream);
        if (DeltaNetwork.isNetworkOwner())
            writeServerStateToNetwork(stream);
    }

    /**
     * Call super last. This prevents server data from overflowing.
     *
     * @param stream
     */
    public void readStateFromNetwork(NetworkReadStream stream) {
        if (!DeltaNetwork.isNetworkOwner())
            readServerStateFromNetwork(stream);
    }

    /**
     * This will not be called on the server. This is for data like inventories to not be set by clients.
     *
     * @param stream
     */
    public void readServerStateFromNetwork(NetworkReadStream stream) {

    }

    /**
     * This will only be called on the server. This is for data like inventories to not be sent by clients to avoid sending unessesary data.
     *
     * @param stream
     */
    public void writeServerStateToNetwork(NetworkWriteStream stream) {

    }

    public void readPhysicsStateFromNetwork(NetworkReadStream stream) {
        body.setType(BodyDef.BodyType.values()[stream.readInt()]);

        body.setTransform(stream.readVector2(), stream.readFloat());

        body.setLinearVelocity(stream.readVector2());
        body.setAngularVelocity(stream.readFloat());

        required = stream.readByteBool();
        restricted = stream.readByteBool();

        //Server will not read
        if (!DeltaNetwork.isNetworkOwner()) {
            charge = stream.readFloat();
        }
    }

    public static PhysicsObject readNewFromNetwork(NetworkReadStream stream) {
        String objectId = stream.readString();
        BodyDef.BodyType type = BodyDef.BodyType.values()[stream.readInt()];
        stream.seek(-4);
        PhysicsObject newObject = PhysicsObjectsRegistry.createInstanceFromRegistry(objectId, type);
        newObject.readAllStateFromNetwork(stream);
        return newObject;
    }

    public void readAllStateFromNetwork(NetworkReadStream stream) {
        if (restricted && DeltaNetwork.isNetworkOwner())
            return;
        readPhysicsStateFromNetwork(stream);
        readStateFromNetwork(stream);
    }

    public PhysicsObject getSelf() {
        return this;
    }

    public void killMyself() {
        PhysicsHandler.bodiesForDeletion.add(body);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Body thisBody) {
            return thisBody.equals(this.body);
        }
        return super.equals(obj);
    }

    @Override
    public NetworkableUUID getNetworkUUID() {
        return uuid;
    }

    @Override
    public boolean shouldNetwork() {
        return body.isAwake();
    }

    @Override
    public void initialiseFromNetwork() {
        super.initialiseFromNetwork();
        createFixture();
    }

    //
//    @Override
//    public void writeToNetwork(NetworkByteWriteStream stream) {
//        stream.writeByteBool(syncNextTick);
//        if (syncNextTick) {
//            writeStateToNetwork(stream);
//            syncNextTick = false;
//        }
//        writePhysicsStateToNetwork(stream);
//    }
//
//    @Override
//    public void readFromNetwork(NetworkByteReadStream stream) {
//        boolean isSyncingState = stream.readByteBool();
//        if (isSyncingState) {
//            readStateFromNetwork(stream);
//        }
//        readPhysicsStateFromNetwork(stream);
//    }
}
