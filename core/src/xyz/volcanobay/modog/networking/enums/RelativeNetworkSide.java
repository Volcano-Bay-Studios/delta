package xyz.volcanobay.modog.networking.enums;

import java.util.function.Function;

public enum RelativeNetworkSide {
    FROM(NetworkingDirection::getFrom), TO(NetworkingDirection::getTo);
    
    final Function<NetworkingDirection, NetworkingSide> sideGetter;
    
    RelativeNetworkSide(Function<NetworkingDirection, NetworkingSide> sideGetter) {
        this.sideGetter = sideGetter;
    }
    
    public NetworkingSide of(NetworkingDirection value) {
        return sideGetter.apply(value);
    }
}
