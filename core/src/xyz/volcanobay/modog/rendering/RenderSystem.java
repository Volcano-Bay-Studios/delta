package xyz.volcanobay.modog.rendering;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.game.level.LevelHandeler;
import xyz.volcanobay.modog.game.CursorHandler;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.ObjectPicker;

public class RenderSystem {
    public static OrthographicCamera camera;
    public static SpriteBatch batch;

    public static Vector2 oldMouse;
    public static Vector2 oldCam;
    public static float scrolledAmount;
    public static RayHandler rayHandler;
    public static PhysicsObject followObject;
    public static float zoomSpeed;
    public static boolean hasPicker = false;
    public static DirectionalLight skylight;
    // Images
    public static Texture jointTexture;
    public static Texture img;
    public static Texture remove;

    public static void initialize(){

        img = new Texture("none.png");
        remove = new Texture("remove.png");
        jointTexture = new Texture("joint.png");
        batch = new SpriteBatch();
        SkyRenderer.initialize();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(30, 30 * (h / w));
        camera.position.set(0, 0, 0);
        camera.zoom = 1;
        camera.update();


        rayHandler = new RayHandler(PhysicsHandler.world);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0f);
        skylight = new  DirectionalLight(rayHandler,4000,new Color(1,1,1,.5f),-90);
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(6);
        RayHandler.useDiffuseLight(true);
        RayHandler.setGammaCorrection(true);
    }
    public static void render() {
        handleInput();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setBlurNum(2);
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.viewportHeight = 30 * (h / w);
//        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888,Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),false);

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
//        batch.draw(img, 0, 0);
        batch.enableBlending();
        SkyRenderer.render();
        LevelHandeler.renderLevel();
        PhysicsHandler.renderObjects();
        batch.disableBlending();
        renderJoints();
        renderOOB();
        CursorHandler.renderCursors();
        batch.end();
        rayHandler.setCombinedMatrix(camera);
//        rayHandler.updateAndRender();
        batch.begin();
        batch.enableBlending();
        PhysicsHandler.renderDebug();
        batch.end();
    }
    public static void renderOOB() {
        batch.enableBlending();
        for (int i = (int) ((camera.position.x/2)-30); i < (int) ((camera.position.x/2)+30); i++) {
            batch.draw(remove, (i*2), -90.5f, 2, 1, 0, 0, remove.getWidth(), remove.getHeight(), false, false);
        }
        batch.disableBlending();
    }
    public static void setShaderColor(float red, float green, float blue, float alpha) {
        if (batch.isDrawing()) {
            batch.setColor(red,green,blue,alpha);
        } else
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
    }
    public static Vector2 getOffsetFromAngle(float angle, Vector2 offset) {
        float inRadians = (float) Math.toRadians(angle);
        return new Vector2(((float) (Math.cos(inRadians)*offset.x) - (float) (offset.x  * Math.sin(angle))),  ( (float) (Math.cos(inRadians)*offset.y) + (float) (offset.x * Math.sin(angle))));
    }
    public static void drawJoint(Vector2 pos1, Vector2 pos2, int rand) {
        float distance = (float) Math.sqrt(Math.pow(pos1.x-pos2.x,2)+Math.pow(pos1.y-pos2.y,2));
        float angle = (float) Math.toDegrees(Math.atan2(pos2.y-pos1.y,pos2.x-pos1.x));
//        batch.draw(jointTexture,pos1.x,pos1.y, distance, .1f, 0, 0, jointTexture.getWidth(), jointTexture.getHeight(), false, false);
        Vector2 offset = getOffsetFromAngle(angle,new Vector2(0f,-.05f));
        batch.draw(jointTexture,pos1.x+offset.x,pos1.y+offset.y, (float) 0,-0 ,(distance),.1f,1,1,angle,rand,0,(int) (distance*10),jointTexture.getHeight(), false, false);
    }
    public static void renderJoints() {
        batch.enableBlending();
        if (PhysicsHandler.placementStep == 1) {
            drawJoint(PhysicsHandler.getMouseWorldPosition().sub(0.05f,.05f),PhysicsHandler.getMouseWorldPosition().add(0.05f,-.05f),1);
        }
        if (PhysicsHandler.placementStep == 2) {
            drawJoint(PhysicsHandler.anchorA,PhysicsHandler.getMouseWorldPosition(),1);
        }
        Array<Joint> joints = new Array<>();
        PhysicsHandler.world.getJoints(joints);
        int i = 0;
        for (Joint joint: joints) {
            if (joint instanceof DistanceJoint distanceJoint) {
                Vector2 pos1 = distanceJoint.getAnchorA();
                Vector2 pos2 = distanceJoint.getAnchorB();
                drawJoint(pos1,pos2,i%10);
                i++;
            }
        }
        batch.disableBlending();
    }

    public static void handleInput() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        zoomSpeed = (zoomSpeed+zoomSpeed+zoomSpeed+scrolledAmount)/4;
        camera.zoom += zoomSpeed*(camera.zoom/5);
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) || Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {
            followObject = null;
            oldCam = new Vector2(camera.position.x,camera.position.y);
            oldMouse = mouse;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && !hasPicker) {
            Delta.stage.addActor(new ObjectPicker());
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            float zoom = camera.zoom;
            camera.position.x = oldCam.x+((oldMouse.x- mouse.x)*(zoom/(w/30)));
            camera.position.y = oldCam.y-((oldMouse.y- mouse.y)*(zoom/(h/22.5f)));
        }
        if (followObject != null) {
            camera.position.x = followObject.body.getPosition().x;
            camera.position.y = followObject.body.getPosition().y;
        }
        if (RenderSystem.camera.zoom < .09) {
            RenderSystem.camera.zoom = .09f;
        }
        if (RenderSystem.camera.zoom > 3) {
            RenderSystem.camera.zoom = 3f;
        }
        scrolledAmount = 0;
    }
    public static void dispose() {
        remove.dispose();
        rayHandler.dispose();
        batch.dispose();
        img.dispose();
        jointTexture.dispose();
        CursorHandler.cursor.dispose();
        CursorHandler.cursorRed.dispose();
        CursorHandler.cursorBlue.dispose();
        CursorHandler.cursorLime.dispose();
        CursorHandler.cursorYellow.dispose();
    }

}
