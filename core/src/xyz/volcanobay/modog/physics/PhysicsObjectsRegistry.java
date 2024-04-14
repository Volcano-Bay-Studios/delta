package xyz.volcanobay.modog.physics;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.physics.objects.CircleObject;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class PhysicsObjectsRegistry {
    public static HashMap<String,PhysicsObject> physicsObjectHashMap = new HashMap<>();
    public static void registerNewObject(String name, PhysicsObject object){
        if (!physicsObjectHashMap.containsKey(name)) {
            physicsObjectHashMap.put(name,object);
        } else {
            Delta.LOGGER.warning("Duplicate registry object: "+name);
        }
    }

    public static void registerObjects() {
        registerNewObject("circle",new CircleObject());
    }
    public static PhysicsObject getFromRegistry(String name) {
        return physicsObjectHashMap.getOrDefault(name, null);
    }
}
