package xyz.volcanobay.modog.core.interfaces.level;

import xyz.volcanobay.modog.core.annotations.Nullable;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.enums.ComponentNetworkUpdateType;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

public abstract class NetworkableLevelComponent {

    @Nullable
    NetworkableUUID delegatedTo;
    boolean isNew;
    boolean stateChanged;

    public NetworkableLevelComponent() {
        isNew = true;
    }

    public abstract NetworkableUUID getNetworkUUID();

    public abstract boolean shouldNetwork();

    public ComponentNetworkUpdateType getUpdateType() {
        if (isNew) return ComponentNetworkUpdateType.NEW_COMPONENT;
        else return (stateChanged ? ComponentNetworkUpdateType.FULL_STATE : ComponentNetworkUpdateType.ONLY_PHYSICS);
    }

    public void writeToNetwork(NetworkByteWriteStream stream) {

    }

    /**
     * @param level el level que se da nuevos componentes
     * */
    public void readFromNetwork(NetworkByteReadStream stream, NetworkableLevel level) {

    }

    public abstract void writePhysicsStateToNetwork(NetworkByteWriteStream stream);
    public abstract void readPhysicsStateFromNetwork(NetworkByteReadStream stream);

    public abstract void writeStateToNetwork(NetworkByteWriteStream stream);
    public abstract void readStateFromNetwork(NetworkByteReadStream stream);

    public abstract void writeNewToNetwork(NetworkByteWriteStream stream);

    public boolean isDelegatedTo(NetworkableUUID connection) {
        return (delegatedTo == null && (DeltaNetwork.isNetworkOwner()
                || NetworkConnectionsManager.connections.get(connection).getNetworkingSide().equals(NetworkingSide.SERVER)))
                || delegatedTo == connection;
    }

    public void setDelegateOf(NetworkableUUID connection) {
        delegatedTo = connection;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void notifyStateChanged() {
        this.stateChanged = true;
    }
}
