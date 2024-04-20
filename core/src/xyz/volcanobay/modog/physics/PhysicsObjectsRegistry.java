package xyz.volcanobay.modog.physics;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.game.objects.*;

import java.util.HashMap;

public class PhysicsObjectsRegistry {
    public static HashMap<String,PhysicsObject> physicsObjectHashMap = new HashMap<>();
    public static void registerNewObject(String name, PhysicsObject object){
        if (!physicsObjectHashMap.containsKey(name)) {
            object.type = name;
            physicsObjectHashMap.put(name,object);
            object.pickTexture();
            object.processTexture();
        } else {
            Delta.LOGGER.warning("Duplicate registry object: "+name);
        }
    }

    public static void registerObjects() {
        registerNewObject("wheel",new CircleObject());
        registerNewObject("crate",new SquareObject());
        registerNewObject("ground",new GroundObject());
        registerNewObject("girder",new GirderObject());
        registerNewObject("item",new MaterialObject());
        registerNewObject("coal_generator",new CoalGeneratorObject());
        registerNewObject("lamp",new LampObject());
        registerNewObject("cable",new CableObject());
        registerNewObject("budge",new BudgeObject());
    }
    public static PhysicsObject getFromRegistry(String name) {
        return physicsObjectHashMap.getOrDefault(name, null);
    }
}
