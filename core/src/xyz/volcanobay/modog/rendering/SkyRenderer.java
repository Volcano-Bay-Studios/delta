package xyz.volcanobay.modog.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.modog.game.Material;

import java.util.ArrayList;
import java.util.List;

import static xyz.volcanobay.modog.rendering.RenderSystem.batch;

public class SkyRenderer {
    public static Texture atmosphere = new Texture("atmosphere.png");
    public static Texture star = new Texture("star.png");
    public static ShaderProgram atmosphereShader = buildShader("shaders/atmosphere.vert","shaders/atmosphere.frag");
    static FrameBuffer blurTargetA;
    static FrameBuffer blurTargetB;
    static List<Vector2> stars = new ArrayList<>();
    static float deltaBlur = 0f;
    static void buildFBO(int width,int height) {
        if (blurTargetA != null) blurTargetA.dispose();
        if (blurTargetB != null) blurTargetB.dispose();

        blurTargetA = new FrameBuffer(Pixmap.Format.RGBA8888,width,height,false);
        blurTargetB = new FrameBuffer(Pixmap.Format.RGBA8888,width,height,false);
    }

    private static ShaderProgram buildShader(String vertexPath, String fragPath) {
        String vert = Gdx.files.internal(vertexPath).readString();
        String frag = Gdx.files.internal(fragPath).readString();
        return new ShaderProgram(vert,frag);
    }
    public static void buildStars() {
        for (int i = 0; i < 100; i++) {
            stars.add(new Vector2((float) ((Math.random()-.5f)*100), (float) ((Math.random()-.5f)*100)));
        }
    }

    public static void initialize(){
        buildFBO(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        buildStars();
    }
    public static void drawParalax(Texture texture, float ratio) {
        Vector2 scale = new Vector2(.1f,.1f);
        Vector2 pos = new Vector2((RenderSystem.camera.position.x-(texture.getWidth()*(scale.x/2f))),(RenderSystem.camera.position.y-(texture.getHeight()*(scale.y/2f))));
        batch.draw(texture,pos.x,pos.y/10f,0,0,texture.getWidth(),texture.getHeight(),.1f,.1f,0,0,0,texture.getWidth(),texture.getHeight(),false,false);
    }
    public static void drawStars() {
        batch.end();
        if (deltaBlur < 1f) {
            deltaBlur += Gdx.graphics.getDeltaTime() * 0.25f;
        }
//        batch.setShader(atmosphereShader);
        Vector2 cam = new Vector2((RenderSystem.camera.position.x), (RenderSystem.camera.position.y));
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888,Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),false);
        batch.begin();

//        fbo.begin();
        Vector3 start = RenderSystem.camera.unproject(new Vector3(0,0,0));
        Vector3 end = RenderSystem.camera.unproject(new Vector3(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0));
        for (Vector2 pos : stars) {
            Vector2 offset = new Vector2(0,0);
            batch.draw(star, pos.x + (cam.x*.9f) + offset.x, pos.y + (cam.y*.9f) + offset.y, 0, 0, star.getWidth(), star.getHeight(), .1f, .1f, 0, 0, 0, star.getWidth(), star.getHeight(), false, false);
        }
//        fbo.end();
        batch.end();
//
//        for (int i = 0; i < 4; i++) {
//            batch.begin();
//            blurTargetA.begin();
//            atmosphereShader.setUniformf("dir", .5f, 0);
//            atmosphereShader.setUniformf("radius", 1f * 4);
//            atmosphereShader.setUniformf("resolution", Gdx.graphics.getWidth());
//            cam.scl(.9f);
//            if (i == 0) {
//                batch.draw(fbo.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
//            } else {
//                batch.draw(blurTargetB.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
//            }
//            batch.end();
//            blurTargetA.end();
//
//            blurTargetB.begin();
//            batch.begin();
//            // Verticle blur pass
//            atmosphereShader.setUniformf("dir", 0, .5f);
//            atmosphereShader.setUniformf("radius", 1f * 4);
//            atmosphereShader.setUniformf("resolution", Gdx.graphics.getHeight());
//            batch.draw(blurTargetA.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
//            batch.end();
//            blurTargetB.end();
//        }
        fbo.dispose();
        batch.begin();
        batch.setShader(SpriteBatch.createDefaultShader());
        batch.draw(fbo.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
    }

    public static void render() {
        drawStars();
        drawParalax(atmosphere,1);
    }

    public static void dispose() {
        atmosphere.dispose();
    }
    public static void resize(int width, int height){
        buildFBO(width,height);
    }

}
