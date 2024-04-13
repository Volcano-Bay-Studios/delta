package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.*;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.physics.PhysicsHandeler;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandeler {
    public static WebSocket socket;
    public static void initalise(){
    }
    public static void joinServer(String ip,int port){
        System.out.println("Attempting to connect to "+ip+":"+port);
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(ip,port));
        socket.setSendGracefully(true);
        socket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                System.out.println("Connected to websocket server!");
                Dialogs.showOKDialog(Delta.stage, "Connected!","Ok");
                return false;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                Dialogs.showOKDialog(Delta.stage, "Disconnected from server ["+closeCode+"] "+reason,"Ok");
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                parseData(packet);
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                return false;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {

                Dialogs.showErrorDialog(Delta.stage, "A network error occured: "+error.getMessage());
                System.out.println("A network error occured!");
                return false;
            }
        });

        socket.connect();
    }
    public static void handleFrame() {
        parseData(packagePhysicsData());
    }
    public static void parseData(String packet) {
        Json json = new Json();
        JsonValue root = new JsonReader().parse(packet);
        for (JsonValue value: root) {
           NetworkablePhysicsObject physicsObject = json.fromJson(NetworkablePhysicsObject.class,value.toJson(JsonWriter.OutputType.json));
           PhysicsHandeler.updatePhysicsObjectFromNetworkedObject(physicsObject);
        }
    }
    public static String packagePhysicsData() {
        Array<Body> bodies = new Array<>();
        PhysicsHandeler.world.getBodies(bodies);
        List<NetworkablePhysicsObject> physicsObjects = new ArrayList<>();
        int i=0;
        for (Body body: bodies) {
            physicsObjects.add(new NetworkablePhysicsObject(body.getPosition(),body.getLinearVelocity(),body.getAngle(),body.getAngularVelocity(),"1",i));
            i++;
        }
        Json json = new Json();
        return json.prettyPrint(physicsObjects );
    }
}
