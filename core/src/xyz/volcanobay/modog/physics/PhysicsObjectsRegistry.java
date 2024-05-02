package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.game.objects.*;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

import java.util.HashMap;
import java.util.UUID;

public class PhysicsObjectsRegistry {

    public static HashMap<String, PhysicsObject> physicsObjectHashMap = new HashMap<>();

    public static void registerNewObject(String name, PhysicsObject object) {
        if (!physicsObjectHashMap.containsKey(name)) {
            object.type = name;
            physicsObjectHashMap.put(name, object);
            object.pickTexture();
            object.processTexture();
        } else {
            Delta.LOGGER.warning("Duplicate registry object: " + name);
        }
    }

    public static void registerObjects() {
        registerNewObject("wheel",new CircleObject());
        registerNewObject("crate",new CrateObject());
        registerNewObject("ground",new GroundObject());
        registerNewObject("girder",new GirderObject());
        registerNewObject("item",new MaterialObject());
        registerNewObject("coal_generator",new CoalGeneratorObject());
        registerNewObject("lamp",new LampObject());
        registerNewObject("cable",new CableObject());
        registerNewObject("budge",new BudgeObject());
        registerNewObject("battery",new BatteryObject());
        registerNewObject("sustain_tank",new SustainTankObject());
        registerNewObject("mini_thruster",new MiniThrusterObject());
    }

    public static PhysicsObject getFromRegistry(String name) {
        return physicsObjectHashMap.getOrDefault(name, null);
    }

    public static PhysicsObject createInstanceFromRegistry(String name, BodyDef.BodyType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;

        Body body = PhysicsHandler.world.createBody(bodyDef);
        //Note that the UUID is overridable, so the returned objects uuid is not necessarily the one used
        return physicsObjectHashMap.getOrDefault(name, null).create(body).setUuid(NetworkableUUID.randomUUID());
    }

}
