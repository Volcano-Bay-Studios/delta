package xyz.volcanobay.modog.game;

import xyz.volcanobay.modog.game.objects.BudgeObject;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.physics.PhysicsHandler;

public class InputHandeler {
    public static BudgeObject controlledContraption;
    public static void render() {
        if (!NetworkHandler.isHost) {
            NetworkHandler.clientAddObject(controlledContraption);
        }
    }
}
