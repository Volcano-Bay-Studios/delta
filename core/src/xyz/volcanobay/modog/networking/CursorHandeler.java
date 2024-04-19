package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.physics.PhysicsHandler;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.volcanobay.modog.rendering.RenderSystem.batch;

public class CursorHandeler {
    public static Texture cursor = new Texture("cursor.png");
    public static Texture cursorRed = new Texture("cursor_red.png");
    public static Texture cursorBlue = new Texture("cursor_blue.png");
    public static Texture cursorLime = new Texture("cursor_lime.png");
    public static Texture cursorYellow = new Texture("cursor_yellow.png");
    public static Cursor myCursor = new Cursor(Delta.uuid);
    public static ConcurrentHashMap<NetworkableUUID,Cursor> cursors = new ConcurrentHashMap<>();

    public static void renderCursors() {
        myCursor.pos = PhysicsHandler.getMouseWorldPosition();
        batch.enableBlending();
        for (Cursor cursor: cursors.values()) {
            cursor.render();
        }
        batch.disableBlending();
        NetworkHandler.sendCursor(myCursor);
    }
    public static void updateCursor(Cursor data) {
        if (cursors.containsKey(data.uuid)) {
            cursors.get(data.uuid).pos = data.pos;
        } else {
            cursors.put(data.uuid, data);
            if (NetworkHandler.isHost) {
                NetworkHandler.packagePhysicsData(false);
            }
        }
    }
}
