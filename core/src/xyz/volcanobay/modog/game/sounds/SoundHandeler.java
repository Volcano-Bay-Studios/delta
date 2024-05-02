package xyz.volcanobay.modog.game.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoundHandeler {
    static HashMap<Long,SoundEvent> soundEventList = new HashMap<>();
    static List<SoundEvent> finishedSounds = new ArrayList<>();
    public static float masterVolume = .25f;
    public static float getSoundVolume(Vector2 pos, float objectVolume){
        Vector2 camPos = new Vector2(RenderSystem.camera.position.x,RenderSystem.camera.position.y);
        float distance = camPos.dst2(pos)*Math.min(1,(.5f/RenderSystem.camera.zoom));
        return ((20/distance)*objectVolume)*masterVolume;
    }
    public static long playSound(Vector2 pos, String sound, boolean loop) {
        SoundEvent event = SoundRegistry.getSound(sound).clone();
        event.pos = pos;
        if (loop) {
            event.addr = event.sound.loop(getSoundVolume(event.pos,getSoundVolume(event.pos,event.volume)));
        } else {
            event.addr = event.sound.play(getSoundVolume(event.pos,getSoundVolume(event.pos,event.volume)));
        }
        event.loop = loop;
        soundEventList.put(event.addr,event);
        return event.addr;
    }
    public static long playSound(Vector2 pos, String sound, boolean loop, float volume) {
        SoundEvent event = SoundRegistry.getSound(sound).clone();
        event.pos = pos;
        if (loop) {
            event.addr = event.sound.loop(getSoundVolume(event.pos,getSoundVolume(event.pos,event.volume)));
        } else {
            event.addr = event.sound.play(getSoundVolume(event.pos,getSoundVolume(event.pos,event.volume)));
        }
        event.volume = volume;
        event.loop = loop;
        soundEventList.put(event.addr,event);
        return event.addr;
    }
    public static long playSound(PhysicsObject object, String sound, boolean loop) {
        SoundEvent event = SoundRegistry.getSound(sound).clone();
        event.pos = object.body.getPosition();
        event.trackingBody = object;
        if (loop) {
            event.addr = event.sound.loop(getSoundVolume(event.pos,event.volume));
        } else {
            event.addr = event.sound.play(getSoundVolume(event.pos,event.volume));
        }
        event.loop = loop;
        soundEventList.put(event.addr, event);
        return event.addr;
    }
    public static void stopSound(long addr){
        if (soundEventList.containsKey(addr)) {
            SoundEvent event = soundEventList.get(addr);
            event.sound.stop(event.addr);
            soundEventList.remove(event.addr);
        }
    }
    public static void playHitSound(Vector2 pos, SoundRegistry.HitType hitType, float strength){
        switch (hitType) {
            case METAL -> {
                if (strength < .3f)
                    playSound(pos,"metal_2",false,strength/4);
                else
                    playSound(pos,"metal_1",false,strength/4);
            }
            case STONE -> {
                playSound(pos,"metal_1",false,strength/4);
            }
            case GLASS -> {
                playSound(pos,"metal_1",false,strength/4);
            }
        }
    }
    public static void handleSoundEvents(){
        for (SoundEvent event : soundEventList.values()) {
            if (event.trackingBody != null) {
                event.pos = event.trackingBody.body.getPosition();
            }
            event.sound.setVolume(event.addr,getSoundVolume(event.pos,event.volume));
            event.timePlaying++;
            if (event.ticksToPlay > 0 && event.timePlaying > event.ticksToPlay) {
                finishedSounds.add(event);
            }
        }
        for (SoundEvent event : finishedSounds) {
            event.sound.stop(event.addr);
            soundEventList.remove(event.addr);
        }
        finishedSounds.clear();
    }
}
