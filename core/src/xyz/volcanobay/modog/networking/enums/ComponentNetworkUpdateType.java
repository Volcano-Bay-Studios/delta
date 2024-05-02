package xyz.volcanobay.modog.networking.enums;

public enum ComponentNetworkUpdateType {
    ONLY_PHYSICS, FULL_STATE, NEW_COMPONENT;

    public static ComponentNetworkUpdateType getById(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }

}
