package xyz.volcanobay.modog.rendering;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;

public class RenderSystem {
    public static OrthographicCamera camera;
    public static SpriteBatch batch;
    public static Texture img;
    public static Vector2 oldMouse;
    public static Vector2 oldCam;
    public static float scrolledAmount;
    public static RayHandler rayHandler;
    public static PhysicsObject followObject;
    public static float zoomSpeed;

    public static void initialize(){

        img = new Texture("none.png");
        batch = new SpriteBatch();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(30, 30 * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom = 20;
        camera.update();


        rayHandler = new RayHandler(PhysicsHandler.world);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0.5f);

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
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
        PhysicsHandler.renderDebug();
        batch.end();
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
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            float zoom = camera.zoom;
            camera.position.x = oldCam.x+((oldMouse.x- mouse.x)*(zoom/(w/30)));
            camera.position.y = oldCam.y-((oldMouse.y- mouse.y)*(zoom/(h/22.5f)));
        }
        if (followObject != null) {
            camera.position.x = followObject.body.getPosition().x;
            camera.position.y = followObject.body.getPosition().y;
        }
        if (RenderSystem.camera.zoom < 0.5) {
            RenderSystem.camera.zoom = 0.5f;
        }
        scrolledAmount = 0;
    }

}
