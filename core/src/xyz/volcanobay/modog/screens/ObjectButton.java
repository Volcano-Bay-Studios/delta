package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisImageButton;

public class ObjectButton extends VisImageButton {
    public ObjectButton(Drawable imageUp, String tooltipText) {
        super(imageUp, tooltipText);
        init();
    }
    public void init() {
        getImage().setScaling(Scaling.fit);
        getImage().setSize(70f,70f);
    }

}
