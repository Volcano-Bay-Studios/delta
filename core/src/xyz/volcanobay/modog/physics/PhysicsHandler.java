package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.NetworkablePhysicsObject;
import xyz.volcanobay.modog.networking.NetworkableUUID;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.ObjectContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhysicsHandler {
    public static World world = new World(new Vector2(0, -30), true);
    public static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    public static boolean isDebug = true;
    public static HashMap<NetworkableUUID, PhysicsObject> physicsObjectHashMap = new HashMap<>();
    public static List<Body> bodiesForDeletion = new ArrayList<>();
    public static List<NetworkablePhysicsObject> newObjectsForAddition = new ArrayList<>();
    public static List<NetworkablePhysicsObject> objectsForUpdates = new ArrayList<>();
    public static MouseJoint mouseJoint;
    public static Body groundBody;
    public static Body staticMoveBody;
    public static Body mouseBody;
    public static float simSpeed = 1f;
    public static boolean lockChanges = false;

    public static void initialize() {
        addGround();
//        world.isLocked();
//        addBasicPhysicsObject(5,20);
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
        physicsObjectHashMap.put(uuid, PhysicsObjectsRegistry.getFromRegistry("circle").create(body).setUuid(uuid));

        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        Fixture fixture = body.createFixture(fixtureDef);
        circle.dispose();
    }

    public static void addNetworkedObject(NetworkablePhysicsObject physicsObject) {
//        System.out.println("addNetworkedObject");

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(physicsObject.pos);
        bodyDef.linearVelocity.set(physicsObject.vel);
        bodyDef.angle = physicsObject.angle;
        bodyDef.angularVelocity = physicsObject.angularVelocity;

        Body body = world.createBody(bodyDef);
        physicsObjectHashMap.put(physicsObject.uuid, PhysicsObjectsRegistry.getFromRegistry("circle").create(body).setUuid(physicsObject.uuid));

        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        Fixture fixture = body.createFixture(fixtureDef);
        circle.dispose();
    }

    public static void addGround() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 10));
        groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(RenderSystem.camera.viewportWidth * 50, 10.0f);
        groundBody.createFixture(groundBox, 0.0f);
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
            world.step(1 / 30f, 6, 2);
            for (PhysicsObject physicsObject : physicsObjectHashMap.values()) {
                physicsObject.tickPhysics();
            }
        }
        for (Body body : bodiesForDeletion) {
            PhysicsObject object = getPhysicsObjectFromBody(body);
            if (object != null) {
                physicsObjectHashMap.remove(object.uuid);
                world.destroyBody(body);
            }
        }
        bodiesForDeletion.clear();
    }
    public static void updateObjects() {
//        System.out.println("updateObjects");

        lockChanges = true;
        for (NetworkablePhysicsObject physicsObject: objectsForUpdates) {
            PhysicsObject ourPhysicsObject = physicsObjectHashMap.get(physicsObject.uuid);
            if (ourPhysicsObject != null) {
                Body ourBody = ourPhysicsObject.body;
                ourBody.setTransform(physicsObject.pos, physicsObject.angle);
                ourBody.setLinearVelocity(physicsObject.vel);
                ourBody.setAngularVelocity(physicsObject.angularVelocity);
            }
        }
        for (NetworkablePhysicsObject networkablePhysicsObject: newObjectsForAddition) {
            addNetworkedObject(networkablePhysicsObject);
        }

        objectsForUpdates.clear();
        newObjectsForAddition.clear();
        lockChanges = false;
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
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (mouseBody != null && (mouseBody.getType() == BodyDef.BodyType.StaticBody || simSpeed == 0) && mouseJoint == null) {
                staticMoveBody = mouseBody;
            } else if (mouseBody != null && mouseJoint == null && simSpeed > 0) {
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
            if (mouseJoint != null) {
                mouseJoint.setTarget(getMouseWorldPosition());
            }
            if (staticMoveBody != null) {
                staticMoveBody.setTransform(getMouseWorldPosition(), staticMoveBody.getAngle());
                if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    staticMoveBody.setLinearVelocity(0, 0);
                    staticMoveBody.setAngularVelocity(0);
                }
            }
        } else if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        } else if (staticMoveBody != null) {
            staticMoveBody = null;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Vector2 mousePos = getMouseWorldPosition();
            addBasicPhysicsObject(mousePos.x, mouse.y);
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (mouseBody != null) {
                PhysicsObject physicsObject = getPhysicsObjectFromBody(mouseBody);
                if (physicsObject != null) {
                    Delta.stage.addActor(new ObjectContext(physicsObject, physicsObject.getContextOptions()));
                }
            }
        }
    }

    public static void getMouseObject() {
//        System.out.println("getMouseObject");

        Vector2 mouse = getMouseWorldPosition();
        mouseBody = null;
        world.QueryAABB(fixture -> {
            mouseBody = fixture.getBody();
            return true;
        }, mouse.x - .1f, mouse.y - .1f, mouse.x + .1f, mouse.y + .1f);
    }

    public static void updatePhysicsObjectFromNetworkedObject(NetworkablePhysicsObject physicsObject) {
//        System.out.println("updatePhysicsObjectFromNetworkedObject");

        if (!world.isLocked() && !lockChanges) {
            objectsForUpdates.clear();
            newObjectsForAddition.clear();
            if (physicsObjectHashMap.containsKey(physicsObject.uuid)) {
                objectsForUpdates.add(physicsObject);
            } else {
                addNetworkedObject(physicsObject);
            }
        }
    }
}
