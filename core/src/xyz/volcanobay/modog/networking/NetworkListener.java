package xyz.volcanobay.modog.networking;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.screens.GameScreen;

import java.nio.charset.StandardCharsets;

class NetworkListener implements WebSocketListener {
    
    private final String ip;
    private final int port;
    public NetworkListener(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    @Override
    public boolean onOpen(WebSocket webSocket) {
        NetworkHandler.connectedIp = ip;
        NetworkHandler.connectedPort = port;
        System.out.println("Connected to websocket server!");
        Dialogs.showOKDialog(Delta.stage, "Connected!", "Connected to " + NetworkHandler.connectedIp + NetworkHandler.connectedPort);
        NetworkHandler.isConnected = true;
        Delta.stage.addActor(new GameScreen());
        return false;
    }
    
    @Override
    public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
        NetworkHandler.isConnected = false;
        Dialogs.showOKDialog(Delta.stage, "Disconnected from server", "[" + closeCode + " ]" + reason);
        return false;
    }
    
    @Override
    public boolean onMessage(WebSocket webSocket, String packet) {
        NetworkHandler.packetProcessQueue.add(packet.getBytes(StandardCharsets.UTF_8));
        return false;
    }
    
    @Override
    public boolean onMessage(WebSocket webSocket, byte[] packet) {
        NetworkHandler.packetProcessQueue.add(packet);
        return false;
    }
    
    @Override
    public boolean onError(WebSocket webSocket, Throwable error) {
        NetworkHandler.isConnected = false;
        Dialogs.showErrorDialog(Delta.stage, "A network error occured: ", error.getMessage());
        System.out.println("A network error occured!");
        return false;
    }
    
}
