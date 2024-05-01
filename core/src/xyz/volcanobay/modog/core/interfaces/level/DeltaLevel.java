package xyz.volcanobay.modog.core.interfaces.level;

import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.game.NetworkLevelComponentConstructor;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.WorldJoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DeltaLevel implements NetworkableLevel {

    /**Un lista generalmente de los componentes, pero no esta en red*/
    protected ConcurrentHashMap<NetworkableUUID, NetworkableLevelComponent> levelComponents;

    protected ConcurrentHashMap<NetworkableUUID, PhysicsObject> physicsObjectMap;
    protected ConcurrentHashMap<NetworkableUUID, WorldJoint> worldJointMap;

    public DeltaLevel(
            ConcurrentHashMap<NetworkableUUID, NetworkableLevelComponent> levelComponents,
            ConcurrentHashMap<NetworkableUUID, PhysicsObject> physicsObjectMap,
            ConcurrentHashMap<NetworkableUUID, WorldJoint> worldJointMap
    ) {
        this.levelComponents = levelComponents;
        this.physicsObjectMap = physicsObjectMap;
        this.worldJointMap = worldJointMap;
    }

    @Override
    public NetworkLevelComponentConstructor resolveNewLevelComponentConstructor(NetworkReadStream stream) {
        return NetworkLevelComponentConstructor.getById(stream.readByteInt());
    }

    @Override
    public void write(NetworkWriteStream stream) {
        writeComponentSection(physicsObjectMap, stream);
        writeComponentSection(worldJointMap, stream);
    }

    private <T extends NetworkableLevelComponent> void writeComponentSection(
            ConcurrentHashMap<NetworkableUUID, T> componentMap, NetworkWriteStream stream
    ) {
        stream.writeInt(componentMap.size());
        for (Map.Entry<NetworkableUUID, T> physicsObject : componentMap.entrySet()) {
            stream.writeUUID(physicsObject.getKey());
            physicsObject.getValue().writeToNetwork(stream);
        }
    }

    @Override
    public void read(NetworkReadStream stream) {
        readComponentSection(physicsObjectMap, stream);
        readComponentSection(worldJointMap, stream);
    }

    private <T extends NetworkableLevelComponent> void readComponentSection(
            ConcurrentHashMap<NetworkableUUID, T> componentMap, NetworkReadStream stream
    ) {
        int length = stream.readInt();
        for (int i = 0; i < length; i++) {
            NetworkableUUID componentUUID = stream.readUUID();
            if (!componentMap.containsKey(componentUUID)) {
                NetworkableLevelComponent newComponent = resolveNewLevelComponentConstructor(stream).build(stream);
                newComponent.initialiseFromNetwork();
                addLevelComponent(newComponent);
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

    public ConcurrentHashMap<NetworkableUUID, NetworkableLevelComponent> getLevelComponents() {
        return levelComponents;
    }

    public ConcurrentHashMap<NetworkableUUID, PhysicsObject> getPhysicsObjectMap() {
        return physicsObjectMap;
    }

    public ConcurrentHashMap<NetworkableUUID, WorldJoint> getWorldJointMap() {
        return worldJointMap;
    }

    public abstract void reloadSourcedMaps();
}
