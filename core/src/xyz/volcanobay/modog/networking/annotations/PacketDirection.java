package xyz.volcanobay.modog.networking.annotations;

import xyz.volcanobay.modog.networking.enums.NetworkingDirection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketDirection {
    NetworkingDirection value();
}
