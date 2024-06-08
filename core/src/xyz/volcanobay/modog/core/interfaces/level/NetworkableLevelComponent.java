package xyz.volcanobay.modog.core.interfaces.level;

import xyz.volcanobay.modog.core.annotations.Nullable;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.networking.enums.ComponentNetworkUpdateType;
import xyz.volcanobay.modog.networking.enums.NetworkingSide;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkWriteStream;

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

    public void writeToNetwork(NetworkWriteStream stream) {
        stream.writeByteInt(getUpdateType().getId());
        switch (getUpdateType()) {
            case FULL_STATE -> {
                writePhysicsStateToNetwork(stream);
                writeStateToNetwork(stream);
            }
            case ONLY_PHYSICS -> {
                writePhysicsStateToNetwork(stream);
            }
            case NEW_COMPONENT -> {
                writeNewToNetwork(stream);
            }
        }
    }

    public void readFromNetwork(NetworkReadStream stream) {
        ComponentNetworkUpdateType.getById(stream.readByteInt());
    }

    public abstract void writePhysicsStateToNetwork(NetworkWriteStream stream);
    public abstract void readPhysicsStateFromNetwork(NetworkReadStream stream);

    public abstract void writeStateToNetwork(NetworkWriteStream stream);
    public abstract void readStateFromNetwork(NetworkReadStream stream);

    public abstract void writeNewToNetwork(NetworkWriteStream stream);
    public NetworkableUUID delegatedTo() {
        if (delegatedTo == null)
            delegatedTo = NetworkConnectionsManager.hostUUID;
        return delegatedTo;
    }

    public boolean isDelegatedTo(NetworkableUUID connection) {
        if (delegatedTo == null) {
            delegatedTo = NetworkConnectionsManager.hostUUID;
        }
        return (delegatedTo() == connection);
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

    /**Optional method to initialise stuff in a clear way*/
    public void initialiseFromNetwork() {
    }
}
