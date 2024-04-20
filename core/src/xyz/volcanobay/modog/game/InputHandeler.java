package xyz.volcanobay.modog.game;

import xyz.volcanobay.modog.game.objects.BudgeObject;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;

import java.util.ArrayList;
import java.util.List;

public class InputHandeler {
    public static BudgeObject controlledContraption;
    public static List<PhysicsObject> playerControlledObjects = new ArrayList<>();
    public static void render() {
        if (!NetworkHandler.isHost && controlledContraption != null) {
            NetworkHandler.sendPhysicsObjects(PhysicsHandler.getContraption(controlledContraption),false);
        }
    }
}
