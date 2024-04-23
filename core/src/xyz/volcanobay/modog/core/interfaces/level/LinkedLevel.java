package xyz.volcanobay.modog.core.interfaces.level;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class LinkedLevel implements NetworkableLevel {

    /**Un lista generalmente de los componentes, pero no esta en red*/
    HashMap<NetworkableUUID, NetworkableLevelComponent> levelComponents;

    HashMap<NetworkableUUID, PhysicsObject> physicsObjectMap;
    HashMap<NetworkableUUID, WorldJoint> worldJointMap;

    public LinkedLevel(
            HashMap<NetworkableUUID, NetworkableLevelComponent> levelComponents,
            HashMap<NetworkableUUID, PhysicsObject> physicsObjectMap,
            HashMap<NetworkableUUID, WorldJoint> worldJointMap
    ) {
        this.levelComponents = levelComponents;
        this.physicsObjectMap = physicsObjectMap;
        this.worldJointMap = worldJointMap;
    }

    @Override
    public void write(NetworkByteWriteStream stream) {
        writeComponentSection(physicsObjectMap, stream);
        writeComponentSection(worldJointMap, stream);
    }

    private <T extends NetworkableLevelComponent> void writeComponentSection(
            HashMap<NetworkableUUID, T> componentMap, NetworkByteWriteStream stream
    ) {
        stream.writeInt(componentMap.size());
        for (Map.Entry<NetworkableUUID, T> physicsObject : componentMap.entrySet()) {
            stream.writeUUID(physicsObject.getKey());
            physicsObject.getValue().writeToNetwork(stream);
        }
    }

    @Override
    public void read(NetworkByteReadStream stream) {

    }

    private <T extends NetworkableLevelComponent> void readComponentSection(
            HashMap<NetworkableUUID, T> componentMap, NetworkByteReadStream stream
    ) {
        int length = stream.readInt();
        for (int i = 0; i < length; i++) {
            NetworkableUUID componentUUID = stream.readUUID();
            if (!componentMap.containsKey(componentUUID)) {

            }
        }
    }

    /**
     * Búsquedas por el uuid, pero si no se encontro, no habré un error
     * @Returns Devuelve el componente que ha estado eliminado
     * */
    public NetworkableLevelComponent removeLevelComponent(NetworkableUUID uuid) {
        NetworkableLevelComponent component = levelComponents.remove(uuid);
        if (component == null) return null;

        if (component instanceof PhysicsObject physicsObject)
            physicsObjectMap.remove(uuid);
        else if (component instanceof WorldJoint worldJoint)
            worldJointMap.remove(uuid);
        return component;
    }

    public void addLevelComponent(NetworkableLevelComponent component) {
        levelComponents.put(component.getNetworkUUID(), component);

        if (component instanceof PhysicsObject physicsObject)
            physicsObjectMap.put(component.getNetworkUUID(), physicsObject);
        else if (component instanceof WorldJoint worldJoint)
            worldJointMap.put(component.getNetworkUUID(), worldJoint);
        else
            Delta.LOGGER.warning("Added a level component, but the type had no delegate, when one is expected (Dev issue, report)");
    }

    public HashMap<NetworkableUUID, NetworkableLevelComponent> getLevelComponents() {
        return levelComponents;
    }

    public HashMap<NetworkableUUID, PhysicsObject> getPhysicsObjectMap() {
        return physicsObjectMap;
    }

    public HashMap<NetworkableUUID, WorldJoint> getWorldJointMap() {
        return worldJointMap;
    }
}
