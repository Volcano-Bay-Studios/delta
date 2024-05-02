package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.utils.*;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.game.Cursor;
import xyz.volcanobay.modog.game.CursorHandeler;
import xyz.volcanobay.modog.networking.networkable.NetworkablePhysicsObject;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.networkable.NetworkableWorldJoint;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.WorldJoint;
import xyz.volcanobay.modog.screens.AddressPicker;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static xyz.volcanobay.modog.physics.PhysicsHandler.getPhysicsObjectFromBody;

public class NetworkHandler {
    public static WebSocket socket;
    public static boolean isConnected = false;
    public static boolean isHost;
    public static boolean connectWindowOpen = true;
    public static String connectedIp;
    public static int connectedPort;
    public static List<byte[]> packetProcessQueue = new ArrayList<>();
    public static boolean hasAuthority;
    
    public static void initalise(){
    }
    
    public static void joinServer(String ip,int port){
        System.out.println("Attempting to connect to "+ip+":"+port);
        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(ip,port));
        socket.setSendGracefully(true);
        socket.addListener(new NetworkListener(ip, port));

        socket.connect();
    }
    
    public static void periodic() {
        if (isHost) {
            fullResync();
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
        hasAuthority = socket == null || socket.isClosed() || isHost;
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
            parsePhysicsObjectRemovalPacket(str);
        }
        if (type.equals("cD")) {
            parseCursorPacket(str);
        }
        if (type.equals("jS")) {
            parseJointData(str);
        }
        if (type.equals("jR")) {
            parseJointRemovalPacket(str);
        }
    }
    
    private static void parseCursorPacket(String packet) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.minimal);
        CursorHandeler.updateCursor(json.fromJson(Cursor.class, new JsonReader().parse(packet).child.next.asString()) );
    }
    
    private static void parsePhysicsObjectRemovalPacket(String packet) {
        if (!PhysicsHandler.world.isLocked()) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.minimal);
            JsonValue root = new JsonReader().parse(packet).child.next;
            JsonValue rootArray = new JsonReader().parse(root.asString());
            for (JsonValue value : rootArray) {
                NetworkableUUID uuid = new NetworkableUUID(value.child.next.asLong(),value.child.next.next.asLong());
                if (PhysicsHandler.physicsObjectMap.containsKey(uuid)) {
                    PhysicsHandler.bodiesForDeletion.add(PhysicsHandler.physicsObjectMap.get(uuid).body);
                }
            }
            PhysicsHandler.updateObjects();
        }
    }
    
    public static void parseJointRemovalPacket(String packet){
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.minimal);
        JsonValue root = new JsonReader().parse(packet).child.next;
        JsonValue rootArray = new JsonReader().parse(root.asString());
        for (JsonValue value : rootArray) {
            NetworkableWorldJoint networkableWorldJoint = json.fromJson(NetworkableWorldJoint.class, value.toJson(JsonWriter.OutputType.json));
            if (networkableWorldJoint != null) {
                WorldJoint ourJoint = PhysicsHandler.jointMap.get(networkableWorldJoint.uuid);
                PhysicsHandler.jointsForRemoval.add(ourJoint);
            }
        }
        PhysicsHandler.updateObjects();
    }
    
    private static void parsePhysicsData(String packet) {
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
    
    private static void parseJointData(String packet) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.minimal);
            JsonValue root = new JsonReader().parse(packet).child.next;
            JsonValue rootArray = new JsonReader().parse(root.asString());
            for (JsonValue value : rootArray) {
                NetworkableWorldJoint networkableWorldJoint = json.fromJson(NetworkableWorldJoint.class, value.toJson(JsonWriter.OutputType.json));
                if (networkableWorldJoint != null) {
                    PhysicsHandler.updateJoints(networkableWorldJoint);
                }
            }
            PhysicsHandler.updateObjects();
    }
    
    public static void sendJoint(WorldJoint joint) {
        if (socket != null && socket.isOpen()) {
            List<WorldJoint> joints = new ArrayList<>();
            joints.add(joint);
            socket.send(packageJoints(joints));
        }
    }
    
    public static void removeJoints(List<WorldJoint> joints) {
        if (socket != null && socket.isOpen()) {
            List<NetworkableWorldJoint> networkableWorldJoints = new ArrayList<>();
            for (WorldJoint worldJoint : joints) {
                NetworkableWorldJoint networkableWorldJoint = new NetworkableWorldJoint();
                networkableWorldJoint.uuid = worldJoint.uuid;
                networkableWorldJoints.add(networkableWorldJoint);
            }
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.minimal);
            socket.send(json.toJson(new Packet("jR", json.toJson(networkableWorldJoints))));
        }
    }
    
    public static String packageJoints(List<WorldJoint> worldJoints) {
        List<NetworkableWorldJoint> networkableWorldJoints = new ArrayList<>();
        for (WorldJoint worldJoint: worldJoints) {
            NetworkableWorldJoint networkableWorldJoint = new NetworkableWorldJoint();
            networkableWorldJoint.uuid = worldJoint.uuid;
            PhysicsObject uuidA = getPhysicsObjectFromBody(worldJoint.joint.getBodyA());
            PhysicsObject uuidB = getPhysicsObjectFromBody(worldJoint.joint.getBodyB());
            if (uuidA != null && uuidB != null) {
                networkableWorldJoint.bodyAUUID = uuidA.uuid;
                networkableWorldJoint.bodyBUUID = uuidB.uuid;
                networkableWorldJoint.localPointA = worldJoint.joint.getAnchorA();
                networkableWorldJoint.localPointB = worldJoint.joint.getAnchorB();
                networkableWorldJoint.length = ((DistanceJoint) worldJoint.joint).getLength();
                networkableWorldJoints.add(networkableWorldJoint);
            }
        }
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.minimal);
        return json.toJson(new Packet("jS",json.toJson(networkableWorldJoints)));
    }
    
    public static void fullResync() {
        if (isHost && socket != null && socket.isOpen() && !PhysicsHandler.world.isLocked()) {
            String packed = packagePhysicsData(false);
            if (packed != null)
                socket.send(packed);
            String packed1 = packageJoints(PhysicsHandler.jointMap.values().stream().toList());
            if (packed1 != null)
                socket.send(packed1);
        }
    }
    
    public static String packagePhysicsData(boolean onlyAwake) {
        List<NetworkablePhysicsObject> physicsObjects = new ArrayList<>();
        int i=0;
        for (PhysicsObject physicsObject: PhysicsHandler.physicsObjectMap.values()) {
            if (!onlyAwake || physicsObject.body.isAwake()) {
                Body body = physicsObject.body;
                int bodyType = 0;
                if (physicsObject.body.getType() == BodyDef.BodyType.DynamicBody) {
                    bodyType = 1;
                } else if (physicsObject.body.getType() == BodyDef.BodyType.KinematicBody) {
                    bodyType = 2;
                }
                physicsObjects.add(new NetworkablePhysicsObject(body.getPosition(), body.getLinearVelocity(), body.getAngle(), body.getAngularVelocity(), physicsObject.type, i, physicsObject.uuid, bodyType, physicsObject.restricted,physicsObject.charge));
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
    
    public static void clientAddObject(Body body) {
        PhysicsObject object = getPhysicsObjectFromBody(body);
        if (object != null) {
            if (socket == null || !socket.isOpen())
                return;
            List<PhysicsObject> physicsObjectList = new ArrayList<>();
            physicsObjectList.add(object);
            sendPhysicsObjects(physicsObjectList, true);
        }
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
                physicsObjects.add(new NetworkablePhysicsObject(body.getPosition(), body.getLinearVelocity(), body.getAngle(), body.getAngularVelocity(), physicsObject.type, i, physicsObject.uuid, bodyType, physicsObject.required,physicsObject.charge));
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
