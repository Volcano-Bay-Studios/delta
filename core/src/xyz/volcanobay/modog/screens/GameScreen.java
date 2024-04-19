package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.physics.PhysicsHandler;

public class GameScreen extends VisWindow {
    public VisCheckBox isHost;
    public VisTextArea objectSelected;
    public VisSlider visSlider;
    public VisTextButton resync;
    public VisTextArea coordinates;
    public GameScreen() {
        super("Game Menu");
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        isHost = new VisCheckBox("Is Hosting");
        visSlider = new VisSlider(-10,10,1,false);
        objectSelected = new VisTextArea();
        resync = new VisTextButton("Resync");
        coordinates = new VisTextArea("?");
        isHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NetworkHandler.isHost = isHost.isChecked();
            }
        });
        resync.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NetworkHandler.fullResync();
            }
        });
        visSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PhysicsHandler.world.setGravity(new Vector2(0,visSlider.getValue()));
            }
        });


        add(isHost).row();
        add(visSlider).row();
        add(resync).row();
        add(coordinates);
        pack();
//        add(objectSelected);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!NetworkHandler.isConnected)
            remove();
        objectSelected.setText(PhysicsHandler.selectedPlaceableObject);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
        Vector2 mouse = PhysicsHandler.getMouseWorldPosition();
        coordinates.setText("X: "+(int)mouse.x+" Y: "+(int)mouse.y);
        super.draw(batch, parentAlpha);
    }
}
