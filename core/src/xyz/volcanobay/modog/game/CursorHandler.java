package xyz.volcanobay.modog.game;

import com.badlogic.gdx.graphics.Texture;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.NetworkingCalls;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.packets.world.A2ACursorUpdatePacket;
import xyz.volcanobay.modog.networking.packets.world.A2ADelegatedObjectUpdatePacket;
import xyz.volcanobay.modog.physics.PhysicsHandler;

import java.util.concurrent.ConcurrentHashMap;

import static xyz.volcanobay.modog.rendering.RenderSystem.batch;

public class CursorHandler {
    public static Texture cursor = new Texture("cursor.png");
    public static Texture cursorRed = new Texture("cursor_red.png");
    public static Texture cursorBlue = new Texture("cursor_blue.png");
    public static Texture cursorLime = new Texture("cursor_lime.png");
    public static Texture cursorYellow = new Texture("cursor_yellow.png");
    public static Cursor myCursor = new Cursor(NetworkConnectionsManager.selfConnectionId);
    public static ConcurrentHashMap<NetworkableUUID, Cursor> cursors = new ConcurrentHashMap<>();

    public static void renderCursors() {
        myCursor.pos = PhysicsHandler.getMouseWorldPosition();
        batch.enableBlending();
        for (Cursor cursor : cursors.values()) {
            cursor.render();
        }
        batch.disableBlending();

        DeltaNetwork.sendPacketToAllOthers(new A2ACursorUpdatePacket(myCursor));

    }
//    public static void updateCursor(Cursor data) {
//        if (cursors.containsKey(data.uuid)) {
//            cursors.get(data.uuid).pos = data.pos;
//        } else {
//            cursors.put(data.uuid, data);
//            if (NetworkingCalls.isHost) {
//                NetworkHandler.packagePhysicsData(false);
//            }
//        }
//    }
}
