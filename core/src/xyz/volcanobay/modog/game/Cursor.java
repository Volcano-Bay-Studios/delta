package xyz.volcanobay.modog.game;

import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class Cursor {
    public NetworkableUUID uuid;
    public Vector2 pos;
    public int color = (int) (Math.random()*4);
    public Cursor() {}
    public Cursor(NetworkableUUID uuid) {
        this.uuid = uuid;
    }
    public void render() {
        if (color == 0) {
            RenderSystem.batch.draw(CursorHandeler.cursor, pos.x, pos.y - (RenderSystem.camera.zoom / 2), RenderSystem.camera.zoom / 2, RenderSystem.camera.zoom / 2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
        }
        if (color == 1) {
            RenderSystem.batch.draw(CursorHandeler.cursorRed, pos.x, pos.y - (RenderSystem.camera.zoom / 2), RenderSystem.camera.zoom / 2, RenderSystem.camera.zoom / 2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
        }
        if (color == 2) {
            RenderSystem.batch.draw(CursorHandeler.cursorBlue, pos.x, pos.y - (RenderSystem.camera.zoom / 2), RenderSystem.camera.zoom / 2, RenderSystem.camera.zoom / 2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
        }
        if (color == 3) {
            RenderSystem.batch.draw(CursorHandeler.cursorLime, pos.x, pos.y - (RenderSystem.camera.zoom / 2), RenderSystem.camera.zoom / 2, RenderSystem.camera.zoom / 2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
        }
        if (color == 4) {
            RenderSystem.batch.draw(CursorHandeler.cursorYellow, pos.x, pos.y - (RenderSystem.camera.zoom / 2), RenderSystem.camera.zoom / 2, RenderSystem.camera.zoom / 2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
        }
        if (color == 5) {
            RenderSystem.batch.draw(CursorHandeler.cursor, pos.x, pos.y - (RenderSystem.camera.zoom / 2), RenderSystem.camera.zoom / 2, RenderSystem.camera.zoom / 2, 0, 0, CursorHandeler.cursor.getWidth(), CursorHandeler.cursor.getHeight(), false, false);
        }
    }
}
