package xyz.volcanobay.modog.game.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundRegistry {
    public static Sound empty = Gdx.audio.newSound(Gdx.files.internal("audio/empty.mp3"));
    static HashMap<String,SoundEvent> soundEventHashMap = new HashMap<>();
    public static void registerSoundEvent(String name, String fileName){
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("audio/"+fileName+".mp3"));
        SoundEvent event = new SoundEvent();
        event.sound = sound;
        soundEventHashMap.put(name,event);
    }
    public static void reigsterSoundEvents(){
        registerSoundEvent("empty","empty");
    }
    public static void dispose(){
        for (SoundEvent event : soundEventHashMap.values()) {
            event.sound.dispose();
        }
        soundEventHashMap.clear();
    }
}
