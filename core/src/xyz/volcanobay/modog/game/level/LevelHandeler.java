package xyz.volcanobay.modog.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.rendering.SkyRenderer;

import java.util.ArrayList;
import java.util.List;

public class LevelHandeler {
    public static List<Level> levels = new ArrayList<>();
    public static float step = .1f;

    public static void addLevels(){
        Level level1 = new Level();
        levels.add(level1);
    }
    public static void renderLevel(){
        levels.get(0).clearPhysData();
        int width = (int) RenderSystem.camera.unproject(new Vector3(Gdx.graphics.getWidth(),0,0)).x+1;
        float start = (int) RenderSystem.camera.unproject(new Vector3(0,0,0)).x-1;
        for (float i = start; i < (float) width; i += step) {
            float pos = levels.get(0).getPoint(i);
            float posToCheck = (float) Math.round(i * 5) / 5;
            levels.get(0).worldCheckPhysics(posToCheck);
            RenderSystem.batch.draw(SkyRenderer.star,i,pos,0,0,1,1,.1f,.1f,0,0,0,1,1,false,false);

        }
        for (PhysicsObject object: PhysicsHandler.physicsObjectHashMap.values()) {
            float posToCheck = (float) Math.round(object.body.getPosition().x * 5) / 5;
            levels.get(0).worldCheckPhysics(posToCheck);
        }
    }
}
