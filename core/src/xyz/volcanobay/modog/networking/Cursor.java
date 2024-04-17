package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class Cursor {
    public NetworkableUUID uuid;
    public Vector2 pos;
    public Cursor() {}
    public Cursor(NetworkableUUID uuid) {
        this.uuid = uuid;
    }
    public void render() {
        RenderSystem.batch.draw(CursorHandeler.cursor, pos.x,pos.y-(RenderSystem.camera.zoom/2), RenderSystem.camera.zoom/2, RenderSystem.camera.zoom/2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
    }
}
