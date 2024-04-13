package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import sun.security.krb5.internal.crypto.Des;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.NetworkablePhysicsObject;
import xyz.volcanobay.modog.physics.callbacks.DestroyCallback;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.ArrayList;
import java.util.List;

public class PhysicsHandeler {
    public static World world = new World(new Vector2(0, -30), true);
    public static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    public static boolean isDebug = true;
    public static List<Body> physicsObjects = new ArrayList<>();
    public static List<Body> bodiesForDeletion = new ArrayList<>();
    public static MouseJoint mouseJoint;
    public static Body groundBody;
    public static Body staticMoveBody;

    public static void initialize() {
        addGround();
        addPhysicsObject(5,20);
    }
    public static void addPhysicsObject(int x,int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        physicsObjects.add(body);

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
        groundBox.setAsBox(RenderSystem.camera.viewportWidth*50, 10.0f);
        groundBody.createFixture(groundBox, 0.0f);
    }
    public static void physicsStep() {

        world.step(1/60f, 6, 2);
    }
    public static void renderObjects() {
        if (isDebug)
            debugRenderer.render(world, RenderSystem.camera.combined);
    }
    public static Vector2 getMouseWorldPosition() {
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector3 vc = RenderSystem.camera.unproject(new Vector3(mouse.x,mouse.y,0));
        return new Vector2(new Vector2(vc.x,vc.y));
    }
    public static void handleInput() {
        Vector2 mouse = getMouseWorldPosition();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            world.QueryAABB(fixture -> {
                if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody && mouseJoint == null) {
                    staticMoveBody = fixture.getBody();
                    return true;
                }
                if (mouseJoint == null) {
                    MouseJointDef jointDef = new MouseJointDef();
                    jointDef.bodyA = groundBody;
                    jointDef.bodyB = fixture.getBody();
                    jointDef.maxForce = 1000f * fixture.getBody().getMass();
                    jointDef.target.set(getMouseWorldPosition());
                    jointDef.collideConnected = true;
                    mouseJoint = (MouseJoint) world.createJoint(jointDef);
                    mouseJoint.setTarget(getMouseWorldPosition());
                    fixture.getBody().setAwake(true);
                }
                return true;
            },mouse.x-1,mouse.y-1,mouse.x+1f,mouse.y+1f);
            if (mouseJoint != null) {
                mouseJoint.setTarget(getMouseWorldPosition());
            }
            if (staticMoveBody != null) {
                staticMoveBody.setTransform(getMouseWorldPosition(),staticMoveBody.getAngle());
            }
        } else if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        } else if (staticMoveBody != null) {
            staticMoveBody = null;
        }

        for (Body body: bodiesForDeletion) {
            world.destroyBody(body);
        }
    }
    public static void updatePhysicsObjectFromNetworkedObject(NetworkablePhysicsObject physicsObject){
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        if (bodies.size > physicsObject.index) {
            Body ourBody = bodies.get(physicsObject.index);
            ourBody.setTransform(physicsObject.pos, physicsObject.angle);
            ourBody.setLinearVelocity(physicsObject.vel);
            ourBody.setAngularVelocity(physicsObject.angularVelocity);
        }
    }
}
