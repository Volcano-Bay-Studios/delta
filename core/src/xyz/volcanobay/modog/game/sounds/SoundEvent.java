package xyz.volcanobay.modog.game.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import xyz.volcanobay.modog.physics.PhysicsObject;

public class SoundEvent implements Cloneable{
    Vector2 pos;
    Sound sound;
    long addr;
    int ticksToPlay;
    int timePlaying;
    boolean loop;
    float volume = 1;
    PhysicsObject trackingBody;

    @Override
    public SoundEvent clone() {
        try {
            return (SoundEvent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
