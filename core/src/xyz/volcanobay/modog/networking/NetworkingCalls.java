package xyz.volcanobay.modog.networking;

import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.game.Cursor;
import xyz.volcanobay.modog.networking.packets.world.A2AObjectUpdateStatePacket;
import xyz.volcanobay.modog.networking.packets.world.A2ACursorUpdatePacket;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;

public class NetworkingCalls {
    
    public static void updateObjectState(Body body) {
        updateObjectState(PhysicsHandler.getPhysicsObjectFromBody(body));
    }
    
    public static void updateObjectState(PhysicsObject object) {
        if (DeltaNetwork.isNetworkOwner())
            DeltaNetwork.sendPacketToAllClients(new A2AObjectUpdateStatePacket(object));
        else if (DeltaNetwork.isClientSide())
            DeltaNetwork.sendPacketToServer(new A2AObjectUpdateStatePacket(object));
    }
    
}
