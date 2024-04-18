package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.networking.NetworkablePhysicsObject;
import xyz.volcanobay.modog.networking.NetworkableUUID;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.ObjectContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PhysicsHandler {
    public static World world = new World(new Vector2(0, -30), true);
    public static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    public static boolean isDebug = false;
    public static ConcurrentHashMap<NetworkableUUID, PhysicsObject> physicsObjectHashMap = new ConcurrentHashMap<>();
    public static List<Body> bodiesForDeletion = new ArrayList<>();
    public static List<NetworkablePhysicsObject> newObjectsForAddition = new ArrayList<>();
    public static List<NetworkablePhysicsObject> objectsForUpdates = new ArrayList<>();
    public static MouseJoint mouseJoint;
    public static Body groundBody;
    public static Body staticMoveBody;
    public static Body velocityMoveBody;
    public static Body mouseBody;
    public static String selectedPlaceableObject = "wheel";
    public static float simSpeed = 1f;
    public static boolean lockChanges = false;
    public static Vector2 grabPoint;
    public static boolean context = false;
    public static boolean lockRot;
    public static float scaleDown = 32;
    // Joint placement variables
    public static boolean placingJoint;
    public static int placementStep;
    public static Body bodyA;
    public static Body bodyB;
    public static Vector2 anchorA;
    public static Vector2 anchorB;
    public static void initialize() {
        addGround();
    }


    public static void addBasicPhysicsObject(float x, float y) {
//        System.out.println("addBasicPhysicsObject");

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        NetworkableUUID uuid = NetworkableUUID.randomUUID();
        while (physicsObjectHashMap.containsKey(uuid))
            uuid = NetworkableUUID.randomUUID();
        PhysicsObject newPhysicsObject = PhysicsObjectsRegistry.getFromRegistry("wheel").create(body).setUuid(uuid);
        physicsObjectHashMap.put(uuid, newPhysicsObject);
        newPhysicsObject.createFixture();
    }
    public static void addObjectFromRegString(float x, float y, String registryObject) {
//        System.out.println("addBasicPhysicsObject");

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        NetworkableUUID uuid = NetworkableUUID.randomUUID();
        while (physicsObjectHashMap.containsKey(uuid))
            uuid = NetworkableUUID.randomUUID();
        PhysicsObject newPhysicsObject = PhysicsObjectsRegistry.getFromRegistry(registryObject).create(body).setUuid(uuid);
        physicsObjectHashMap.put(uuid, newPhysicsObject);
        newPhysicsObject.createFixture();
        if (!NetworkHandler.isHost) {
            NetworkHandler.clientAddObject(newPhysicsObject);
        }
    }

    public static void addNetworkedObject(NetworkablePhysicsObject physicsObject) {
//        System.out.println("addNetworkedObject");
        if (physicsObject.type == null || physicsObject.type.equals("ground") )
            return;
        BodyDef bodyDef = new BodyDef();
        if (physicsObject.bodyType == 0) {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        } else if (physicsObject.bodyType == 2) {
            bodyDef.type = BodyDef.BodyType.KinematicBody;
        } else {
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        }
        bodyDef.position.set(physicsObject.pos);
        bodyDef.linearVelocity.set(physicsObject.vel);
        bodyDef.angle = physicsObject.angle;
        bodyDef.angularVelocity = physicsObject.angularVelocity;

        Body body = world.createBody(bodyDef);
        PhysicsObject newPhysicsObject = PhysicsObjectsRegistry.getFromRegistry(physicsObject.type).create(body).setUuid(physicsObject.uuid);
        physicsObjectHashMap.put(physicsObject.uuid, newPhysicsObject);
        newPhysicsObject.createFixture();
    }

    public static void addGround() {


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        Body body = world.createBody(bodyDef);
        groundBody = body;
        NetworkableUUID uuid = new NetworkableUUID(-4866359188412218577L,-6878574037472667055L);
        PhysicsObject newPhysicsObject = PhysicsObjectsRegistry.getFromRegistry("ground").create(body).setUuid(uuid);
        physicsObjectHashMap.put(uuid, newPhysicsObject);
        newPhysicsObject.createFixture();
    }

    public static void physicsStep() {
//        System.out.println("physicsStep");

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (simSpeed == 0)
                simSpeed = 1;
            else
                simSpeed = 0;
        }

        if (simSpeed > 0 && !lockChanges) {
            world.step(1 / 120f, 6, 2);
            for (PhysicsObject physicsObject : physicsObjectHashMap.values()) {
                physicsObject.tickPhysics();
            }
        }
        List<NetworkableUUID> uuidsForRemovalFromClients = new ArrayList<>();
        for (Body body : bodiesForDeletion) {
            PhysicsObject object = getPhysicsObjectFromBody(body);
            if (object != null) {
                if (mouseJoint != null && mouseJoint.getBodyB() == object.body) {
                    mouseJoint = null;
                }
                uuidsForRemovalFromClients.add(object.uuid);
                physicsObjectHashMap.remove(object.uuid);
                world.destroyBody(body);
            }
        }
        if (!uuidsForRemovalFromClients.isEmpty())
            NetworkHandler.removeFromClients(uuidsForRemovalFromClients);
        bodiesForDeletion.clear();
    }
    public static void updateObjects() {
//        System.out.println("updateObjects");

        List<NetworkablePhysicsObject> networkablePhysicsObjects = new ArrayList<>(objectsForUpdates);
        for (NetworkablePhysicsObject physicsObject: networkablePhysicsObjects) {
            PhysicsObject ourPhysicsObject = physicsObjectHashMap.get(physicsObject.uuid);
            if (ourPhysicsObject != null && ourPhysicsObject.body != staticMoveBody && (mouseJoint == null || ourPhysicsObject.body != mouseJoint.getBodyB()) && (!NetworkHandler.isHost || !ourPhysicsObject.restricted)) {
                Body ourBody = ourPhysicsObject.body;
                ourBody.setTransform(physicsObject.pos, physicsObject.angle);
                ourBody.setLinearVelocity(physicsObject.vel);
                ourBody.setAngularVelocity(physicsObject.angularVelocity);
                ourPhysicsObject.restricted = physicsObject.restricted;
                if (physicsObject.bodyType == 0) {
                    ourPhysicsObject.body.setType(BodyDef.BodyType.StaticBody);
                } else if (physicsObject.bodyType == 2) {
                    ourPhysicsObject.body.setType(BodyDef.BodyType.KinematicBody);
                } else {
                    ourPhysicsObject.body.setType(BodyDef.BodyType.DynamicBody);
                }
            }
        }
        for (NetworkablePhysicsObject networkablePhysicsObject: newObjectsForAddition) {
            addNetworkedObject(networkablePhysicsObject);
        }

        objectsForUpdates.removeAll(networkablePhysicsObjects);
        newObjectsForAddition.clear();
    }

    public static void renderDebug() {
//        System.out.println("renderDebug");

        if (isDebug && !world.isLocked())
            debugRenderer.render(world, RenderSystem.camera.combined);
    }

    public static void renderObjects() {
//        System.out.println("renderObjects");

        for (PhysicsObject physicsObject : physicsObjectHashMap.values()) {
            physicsObject.render();
        }
    }

    public static Vector2 getMouseWorldPosition() {
//        System.out.println("getMouseWorldPosition");

        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 vc = RenderSystem.camera.unproject(new Vector3(mouse.x, mouse.y, 0));
        return new Vector2(new Vector2(vc.x, vc.y));
    }

    public static PhysicsObject getPhysicsObjectFromBody(Body body) {
//        System.out.println("getPhysicsObjectFromBody");

        for (PhysicsObject object : physicsObjectHashMap.values()) {
            if (object.body == body) {
                return object;
            }
        }
        return null;
    }

    public static void handleInput() {
//        System.out.println("handleInput");

        getMouseObject();
        Vector2 mouse = getMouseWorldPosition();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !placingJoint) {
            if (mouseBody != null && (mouseBody.getType() == BodyDef.BodyType.StaticBody || simSpeed == 0) && mouseJoint == null && staticMoveBody == null) {
                if ((NetworkHandler.isHost || !getPhysicsObjectFromBody(mouseBody).restricted)) {
                    grabPoint = new Vector2(mouse.x-mouseBody.getPosition().x,mouse.y-mouseBody.getPosition().y);
                    staticMoveBody = mouseBody;
                    if (!NetworkHandler.isHost) {
                        NetworkHandler.clientAddObject(getPhysicsObjectFromBody(mouseBody));
                    }
                }
            } else if (mouseBody != null && mouseJoint == null && simSpeed > 0 && (NetworkHandler.isHost || !getPhysicsObjectFromBody(mouseBody).restricted) && mouseBody != groundBody) {
                MouseJointDef jointDef = new MouseJointDef();
                jointDef.bodyA = groundBody;
                jointDef.bodyB = mouseBody;
                jointDef.maxForce = 1000f * mouseBody.getMass();
                jointDef.target.set(getMouseWorldPosition());
                jointDef.collideConnected = true;
                mouseJoint = (MouseJoint) world.createJoint(jointDef);
                mouseJoint.setTarget(getMouseWorldPosition());
                mouseBody.setAwake(true);

            }
            float spinSpeed = 5;
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                spinSpeed = 30;
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                spinSpeed = 1;
            if (mouseJoint != null) {
                mouseJoint.setTarget(getMouseWorldPosition());
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    mouseJoint.getBodyB().setAngularVelocity(spinSpeed);
                    lockRot = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    mouseJoint.getBodyB().setAngularVelocity(-spinSpeed);
                    lockRot = true;
                } else if (lockRot) {
                    mouseJoint.getBodyB().setAngularVelocity(0);
                    mouseJoint.getBodyB().setFixedRotation(true);
                }
            }
            if (staticMoveBody != null) {
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    staticMoveBody.setTransform(staticMoveBody.getPosition(),staticMoveBody.getAngle()+(spinSpeed/100));
                } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    staticMoveBody.setTransform(staticMoveBody.getPosition(),staticMoveBody.getAngle()-(spinSpeed/100));
                }
            }
            if (!NetworkHandler.isHost && mouseJoint != null) {
                NetworkHandler.clientAddObject(getPhysicsObjectFromBody(mouseJoint.getBodyB()));
            }
            if (staticMoveBody != null) {
                staticMoveBody.setTransform(getMouseWorldPosition().sub(grabPoint), staticMoveBody.getAngle());
                staticMoveBody.setAwake(true);
                if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    staticMoveBody.setLinearVelocity(0, 0);
                    staticMoveBody.setAngularVelocity(0);
                }
            }
        } else if (mouseJoint != null && mouseJoint.getBodyB() != null) {
            mouseJoint.getBodyB().setFixedRotation(false);
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
            lockRot = false;
        } else if (staticMoveBody != null) {
            staticMoveBody.setAwake(false);
            staticMoveBody = null;
            lockRot = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Vector2 mousePos = getMouseWorldPosition();
            addObjectFromRegString(mousePos.x, mouse.y,selectedPlaceableObject);
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (mouseBody != null) {
                PhysicsObject physicsObject = getPhysicsObjectFromBody(mouseBody);
                if (physicsObject != null && !context) {
                    Delta.stage.addActor(new ObjectContext(physicsObject, physicsObject.getContextOptions()));
                    context = true;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && placingJoint) {
            placingJoint = false;
            bodyA = null;
            bodyB = null;
            anchorA = null;
            anchorB = null;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && !placingJoint) {
            placingJoint = true;
            placementStep = 1;
            bodyA = null;
            bodyB = null;
            anchorA = null;
            anchorB = null;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && placingJoint) {
            if (mouseBody != null) {
                if (placementStep == 1) {
                    anchorA = mouse;
                    bodyA = mouseBody;
                    placementStep = 2;
                }
                if (placementStep == 2 && mouseBody != bodyA) {
                    anchorB = mouse;
                    bodyB = mouseBody;
                    DistanceJointDef defJoint = new DistanceJointDef ();
                    defJoint.length = 0;
                    defJoint.initialize(bodyA, bodyB, anchorA, anchorB);
                    defJoint.collideConnected = true;
                    world.createJoint(defJoint);
                    placementStep = 0;
                    placingJoint = false;
                    bodyA = null;
                    bodyB = null;
                    anchorA = null;
                    anchorB = null;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            selectedPlaceableObject = "crate";
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_LEFT)) {
            selectedPlaceableObject = "wheel";
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3))
            isDebug = !isDebug;
    }

    public static void getMouseObject() {
//        System.out.println("getMouseObject");

        Vector2 mouse = getMouseWorldPosition();
        mouseBody = null;
//        world.rayCast(new RayCastCallback() {
//            @Override
//            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
//                mouseBody = fixture.getBody();
//                return 0;
//            }
//        },mouse,mouse.add(0,1f));
        world.QueryAABB(fixture -> {
            mouseBody = fixture.getBody();
            return true;
        }, mouse.x, mouse.y, mouse.x, mouse.y);
    }

    public static void updatePhysicsObjectFromNetworkedObject(NetworkablePhysicsObject physicsObject) {
//        System.out.println("updatePhysicsObjectFromNetworkedObject");

        if (!world.isLocked()) {
            newObjectsForAddition.clear();
            if (physicsObjectHashMap.containsKey(physicsObject.uuid)) {
                objectsForUpdates.add(physicsObject);
            } else {
                addNetworkedObject(physicsObject);
            }
        }
    }
}
