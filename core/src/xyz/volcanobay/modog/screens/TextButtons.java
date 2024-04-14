package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import javax.swing.event.ChangeEvent;

public class TextButtons {
    String name;
    ChangeListener changeListener;
    public TextButtons(String name, ChangeListener changeListener) {
        this.name = name;
        this.changeListener = changeListener;
    }
}
