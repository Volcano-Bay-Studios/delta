package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.NetworkConnectionsManager;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.rendering.RenderSystem;

public class GameScreen extends VisWindow {
    public VisCheckBox isHost;
    public VisTextArea objectSelected;
    public VisSlider gravitySlider;
    public VisSlider ambientLightSlider;
    public VisTextButton resync;
    public VisTextArea coordinates;
    public GameScreen() {
        super("Game Menu");
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        isHost = new VisCheckBox("Is Hosting");
        gravitySlider = new VisSlider(-10,10,1,false);
        ambientLightSlider = new VisSlider(0,1,.1f,false);
        ambientLightSlider.setValue(0.5f);
        objectSelected = new VisTextArea();
        resync = new VisTextButton("Resync");
        coordinates = new VisTextArea("?");
        coordinates.setReadOnly(true);
        ambientLightSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RenderSystem.rayHandler.setAmbientLight(ambientLightSlider.getValue());
                RenderSystem.skylight.setColor(1,1,1,ambientLightSlider.getValue());
            }
        });
        resync.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                    NetworkConnectionsManager.searchForConnectionsOnNetwork();
            }
        });
        isHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                if (DeltaNetwork.isConnected())
//                    NetworkConnectionsManager.searchForConnectionsOnNetwork();
            }
        });

        add(isHost).row();
        add(gravitySlider).row();
        add(ambientLightSlider).row();
        add(resync).row();
        add(coordinates);
        pack();
//        add(objectSelected);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!DeltaNetwork.isConnected())
            remove();
        isHost.setChecked(DeltaNetwork.isNetworkOwner());
        objectSelected.setText(PhysicsHandler.selectedPlaceableObject);
        setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-20);
        Vector2 mouse = PhysicsHandler.getMouseWorldPosition();
        coordinates.setText("X: "+(int)mouse.x+" Y: "+(int)mouse.y);
        super.draw(batch, parentAlpha);
    }
}
