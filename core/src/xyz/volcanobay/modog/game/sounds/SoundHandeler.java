package xyz.volcanobay.modog.game.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.ArrayList;
import java.util.List;

public class SoundHandeler {
    static List<SoundEvent> soundEventList = new ArrayList<>();
    static List<SoundEvent> finishedSounds = new ArrayList<>();
    public static float masterVolume = .25f;
    public static float getSoundVolume(Vector2 pos){
        Vector2 camPos = new Vector2(RenderSystem.camera.position.x,RenderSystem.camera.position.y);
        float distance = camPos.dst2(pos);
        return (20/distance)*masterVolume;
    }
    public static void playSound(Vector2 pos, String sound, boolean loop) {
        SoundEvent event = SoundRegistry.getSound(sound).clone();
        event.pos = pos;
        if (loop) {
            event.addr = event.sound.loop(getSoundVolume(event.pos));
        } else {
            event.addr = event.sound.play(getSoundVolume(event.pos));
        }
        event.loop = loop;
        soundEventList.add(event);
    }
    public static void handleSoundEvents(){
        for (SoundEvent event : soundEventList) {
            if (event.trackingBody != null) {
                event.pos = event.trackingBody.body.getPosition();
            }
            event.sound.setVolume(event.addr,getSoundVolume(event.pos));
            event.timePlaying++;
            if (event.timePlaying > event.ticksToPlay) {
                finishedSounds.add(event);
            }
            //TODO make sound handling system
        }
        for (SoundEvent event : finishedSounds) {
            event.sound.stop(event.addr);
        }
        soundEventList.removeAll(finishedSounds);
        finishedSounds.clear();
    }
}
