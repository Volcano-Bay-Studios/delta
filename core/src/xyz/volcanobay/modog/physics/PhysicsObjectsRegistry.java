package xyz.volcanobay.modog.physics;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.physics.objects.CircleObject;
import xyz.volcanobay.modog.physics.objects.GroundObject;

import java.rmi.*;
import java.rmi.registry.Registry;
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
        registerNewObject("ground",new GroundObject());
    }
    public static PhysicsObject getFromRegistry(String name) {
        return physicsObjectHashMap.getOrDefault(name, null);
    }
}
