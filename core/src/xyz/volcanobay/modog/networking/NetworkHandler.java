package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.*;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.AddressPicker;
import xyz.volcanobay.modog.screens.GameScreen;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NetworkHandler {
    public static WebSocket socket;
    public static boolean isConnected = false;
    public static boolean isHost;
    public static boolean connectWindowOpen = true;
    public static String connectedIp;
    public static int connectedPort;
    public static List<byte[]> packetProcessQueue = new ArrayList<>();
    public static void initalise(){
    }
    public static void joinServer(String ip,int port){
        System.out.println("Attempting to connect to "+ip+":"+port);
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(ip,port));
        socket.setSendGracefully(true);
        socket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                connectedIp = ip;
                connectedPort = port;
                System.out.println("Connected to websocket server!");
                Dialogs.showOKDialog(Delta.stage, "Connected!","Connected to "+connectedIp+connectedPort);
                isConnected = true;
                Delta.stage.addActor(new GameScreen());
                return false;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                isConnected = false;
                Dialogs.showOKDialog(Delta.stage, "Disconnected from server","["+closeCode+" ]"+reason);
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                packetProcessQueue.add(packet.getBytes(StandardCharsets.UTF_8));
                return false;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                packetProcessQueue.add(packet);
                return false;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                isConnected = false;
                Dialogs.showErrorDialog(Delta.stage, "A network error occured: ",error.getMessage());
                System.out.println("A network error occured!");
                return false;
            }
        });

        socket.connect();
    }
    public static void periodic() {
        if (isHost) {
            packagePhysicsData(false);
        }
    }
    public static void handleFrame() {
        if (isHost && socket != null && socket.isOpen() && !PhysicsHandler.world.isLocked()) {
            String packed = packagePhysicsData(true);
            if (packed != null)
                socket.send(packed);
        }
        if (!isConnected && !connectWindowOpen)
            Delta.stage.addActor(new AddressPicker());
        List<byte[]> processing = new ArrayList<>(packetProcessQueue);
        for (byte[] bytes: processing) {
            parsePacket(bytes);
        }
        packetProcessQueue.removeAll(processing);
    }
    public static void parsePacket(byte[] bytes) {
        if (bytes == null)
            return;
        String str = new String(bytes, StandardCharsets.UTF_8); // for UTF-8 encoding
        String type = new JsonReader().parse(str).child.asString();
        if (type.equals("pD") || type.equals("resync")) {
            parsePhysicsData(str);
        }
        if (type.equals("rM")) {
            parseRemovalPacket(str);
        }
        if (type.equals("cD")) {
            parseCursorPacket(str);
        }
    }
    public static void parseCursorPacket(String packet) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.minimal);
        CursorHandeler.updateCursor(json.fromJson(Cursor.class, new JsonReader().parse(packet).child.next.asString()) );
    }
    public static void parseRemovalPacket(String packet) {
        if (!PhysicsHandler.world.isLocked()) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.minimal);
            JsonValue root = new JsonReader().parse(packet).child.next;
            JsonValue rootArray = new JsonReader().parse(root.asString());
            for (JsonValue value : rootArray) {
                NetworkableUUID uuid = new NetworkableUUID(value.child.next.asLong(),value.child.next.next.asLong());
                if (PhysicsHandler.physicsObjectHashMap.containsKey(uuid)) {
                    PhysicsHandler.bodiesForDeletion.add(PhysicsHandler.physicsObjectHashMap.get(uuid).body);
                }
            }
            PhysicsHandler.updateObjects();
        }
    }
    public static void parsePhysicsData(String packet) {
        if (!PhysicsHandler.world.isLocked()) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.minimal);
            JsonValue root = new JsonReader().parse(packet).child.next;
            JsonValue rootArray = new JsonReader().parse(root.asString());
            for (JsonValue value : rootArray) {
                NetworkablePhysicsObject physicsObject = json.fromJson(NetworkablePhysicsObject.class, value.toJson(JsonWriter.OutputType.json));
                if (physicsObject != null) {
                    PhysicsHandler.updatePhysicsObjectFromNetworkedObject(physicsObject);
                }
            }
            PhysicsHandler.updateObjects();
        }
    }

    public static String packagePhysicsData(boolean onlyAwake) {
        List<NetworkablePhysicsObject> physicsObjects = new ArrayList<>();
        int i=0;
        for (PhysicsObject physicsObject: PhysicsHandler.physicsObjectHashMap.values()) {
            if (!onlyAwake || physicsObject.body.isAwake()) {
                Body body = physicsObject.body;
                int bodyType = 0;
                if (physicsObject.body.getType() == BodyDef.BodyType.DynamicBody) {
                    bodyType = 1;
                } else if (physicsObject.body.getType() == BodyDef.BodyType.KinematicBody) {
                    bodyType = 2;
                }
                physicsObjects.add(new NetworkablePhysicsObject(body.getPosition(), body.getLinearVelocity(), body.getAngle(), body.getAngularVelocity(), physicsObject.type, i, physicsObject.uuid, bodyType, physicsObject.restricted));
                i++;
            }
        }
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.minimal);
        if (physicsObjects.isEmpty())
            return null;
        return json.toJson(new Packet("pD",json.toJson(physicsObjects)));
    }
    public static void clientAddObject(PhysicsObject object) {
        if (socket == null || !socket.isOpen())
            return;
        List<PhysicsObject> physicsObjectList = new ArrayList<>();
        physicsObjectList.add(object);
        sendPhysicsObjects(physicsObjectList,true);
    }
    public static void sendPhysicsObjects(List<PhysicsObject> physicsObjectList, boolean onlyAwake) {
        if (socket == null || !socket.isOpen())
            return;
        List<NetworkablePhysicsObject> physicsObjects = new ArrayList<>();
        int i=0;
        for (PhysicsObject physicsObject: physicsObjectList) {
            if (!onlyAwake || physicsObject.body.isAwake()) {
                Body body = physicsObject.body;
                int bodyType = 0;
                if (physicsObject.body.getType() == BodyDef.BodyType.DynamicBody) {
                    bodyType = 1;
                } else if (physicsObject.body.getType() == BodyDef.BodyType.KinematicBody) {
                    bodyType = 2;
                }
                physicsObjects.add(new NetworkablePhysicsObject(body.getPosition(), body.getLinearVelocity(), body.getAngle(), body.getAngularVelocity(), physicsObject.type, i, physicsObject.uuid, bodyType, physicsObject.required));
                i++;
            }
        }
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.minimal);
        if (!physicsObjects.isEmpty())
            socket.send(json.toJson(new Packet("pD",json.toJson(physicsObjects))));
    }
    public static void sendCursor(Cursor cursor) {
        Json json = new Json();
        if (socket != null && socket.isOpen()) {
            socket.send(json.toJson(new Packet("cD", json.toJson(cursor))));
        }
    }
    public static void removeFromClients(List<NetworkableUUID> networkableUUID) {
        if (socket != null && socket.isOpen()) {
            Json json = new Json();
            socket.send(json.toJson(new Packet("rM", json.toJson(networkableUUID))));
        }
    }
}
