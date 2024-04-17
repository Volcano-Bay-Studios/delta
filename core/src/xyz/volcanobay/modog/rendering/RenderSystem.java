package xyz.volcanobay.modog.rendering;

import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.CursorHandeler;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.ObjectPicker;

public class RenderSystem {
    public static OrthographicCamera camera;
    public static SpriteBatch batch;
    public static Texture img;
    public static Texture remove;
    public static Vector2 oldMouse;
    public static Vector2 oldCam;
    public static float scrolledAmount;
    public static RayHandler rayHandler;
    public static PhysicsObject followObject;
    public static float zoomSpeed;
    public static boolean hasPicker = false;
    public static DirectionalLight skylight;

    public static void initialize(){

        img = new Texture("none.png");
        remove = new Texture("remove.png");
        batch = new SpriteBatch();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(30, 30 * (h / w));
        camera.position.set(0, 0, 0);
        camera.zoom = 1;
        camera.update();


        rayHandler = new RayHandler(PhysicsHandler.world);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0.5f);
        new  DirectionalLight(rayHandler,4000,new Color(1,1,1,.4f),-90);

    }
    public static void render() {
        handleInput();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.viewportHeight = 30 * (h / w);

        ScreenUtils.clear(0.3f, 0.3f, 0.3f, 1);
        batch.begin();

//        batch.draw(img, 0, 0);
        batch.enableBlending();
        PhysicsHandler.renderObjects();
        batch.disableBlending();
        renderOOB();
        CursorHandeler.renderCursors();
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
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
        CursorHandeler.cursor.dispose();
    }

}
