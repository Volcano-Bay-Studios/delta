package xyz.volcanobay.modog.game.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class SoundHandeler {
    static List<SoundEvent> soundEventList = new ArrayList<>();

    public static void addSoundEvent(Vector2 pos, Sound sound) {
        SoundEvent event = new SoundEvent();
        event.sound = sound;
        event.pos = pos;
        sound.play();
        soundEventList.add(event);
    }
    public static void handleSoundEvents(){
        for (SoundEvent event : soundEventList) {
            //TODO make sound handling system
        }
    }
}
