package xyz.volcanobay.modog.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundRegistry {
    public static Sound empty = Gdx.audio.newSound(Gdx.files.internal("audio/empty.mp3"));
}
