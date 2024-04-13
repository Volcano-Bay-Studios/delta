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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.volcanobay.modog.physics.PhysicsHandeler;

import java.awt.*;

public class RenderSystem {
    public static OrthographicCamera camera;
    public static SpriteBatch batch;
    public static Texture img;
    public static Vector2 oldMouse;
    public static Vector2 oldCam;
    public static float scrolledAmount;
    public static RayHandler rayHandler;

    public static void initialize(){

        img = new Texture("badlogic.jpg");
        batch = new SpriteBatch();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(30, 30 * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom = 20;
        camera.update();


        rayHandler = new RayHandler(PhysicsHandeler.world);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(0.5f);

    }
    public static void render() {
        handleInput();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.3f, 0.3f, 0.3f, 1);
        batch.begin();

//        batch.draw(img, 0, 0);


        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
        batch.end();

    }

    public static void handleInput() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.zoom += scrolledAmount;
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            oldCam = new Vector2(camera.position.x,camera.position.y);
            oldMouse = mouse;
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float zoom = camera.zoom;
            camera.position.x = oldCam.x+((oldMouse.x- mouse.x)*(zoom/(w/30)));
            camera.position.y = oldCam.y-((oldMouse.y- mouse.y)*(zoom/(h/22.5f)));
        }
        if (RenderSystem.camera.zoom < 0.5) {
            RenderSystem.camera.zoom = 0.5f;
        }
        scrolledAmount = 0;
    }
}
