package xyz.volcanobay.modog.game.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundRegistry {
    public static Sound empty = Gdx.audio.newSound(Gdx.files.internal("audio/empty.mp3"));
    static HashMap<String,SoundEvent> soundEventHashMap = new HashMap<>();
    public static void registerSoundEvent(String name, String fileName, int time){
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("audio/"+fileName+".mp3"));
        SoundEvent event = new SoundEvent();
        event.sound = sound;
        event.ticksToPlay = time;
        soundEventHashMap.put(name,event);
    }
    public static SoundEvent getSound(String sound){
        return soundEventHashMap.getOrDefault(sound,new SoundEvent()).clone();
    }
    public static void registerSoundEvents(){
        registerSoundEvent("empty","empty",12);
        registerSoundEvent("thruster","thruster",-1);

        // Hit Sounds
        registerSoundEvent("metal_1","metal_1",20);
        registerSoundEvent("metal_2","metal_2",20);

        // Sliding Sounds
        registerSoundEvent("metal_slide_1","metal_slide_1",80);
    }
    public static void dispose(){
        for (SoundEvent event : soundEventHashMap.values()) {
            event.sound.dispose();
        }
        soundEventHashMap.clear();
    }
    public enum HitType {
        METAL,
        STONE,
        GLASS
    }
}
