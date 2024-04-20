package xyz.volcanobay.modog.networking.annotations;

import xyz.volcanobay.modog.networking.enums.NetworkingDirection;

public @interface PacketDirection {
    NetworkingDirection value();
}
