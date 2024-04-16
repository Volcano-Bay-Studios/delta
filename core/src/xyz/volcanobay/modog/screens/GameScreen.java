package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.physics.PhysicsHandler;

public class GameScreen extends VisWindow {
    public VisCheckBox isHost;
    public VisTextArea objectSelected;
    public GameScreen() {
        super("Game Menu");

        isHost = new VisCheckBox("Is Hosting");
        objectSelected = new VisTextArea();
        isHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NetworkHandler.isHost = isHost.isChecked();
            }
        });


        add(isHost);
//        add(objectSelected);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!NetworkHandler.isConnected)
            remove();
        objectSelected.setText(PhysicsHandler.selectedPlaceableObject);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
        super.draw(batch, parentAlpha);
    }

}
