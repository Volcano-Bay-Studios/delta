package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.volcanobay.modog.networking.NetworkHandler;

public class HostScreen extends VisWindow {
    public VisCheckBox isHost;
    public HostScreen() {
        super("Host Menu");

        isHost = new VisCheckBox("Is Hosting");
        isHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NetworkHandler.isHost = isHost.isChecked();
            }
        });

        add(isHost);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!NetworkHandler.isConnected)
            remove();
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
        super.draw(batch, parentAlpha);
    }
}
