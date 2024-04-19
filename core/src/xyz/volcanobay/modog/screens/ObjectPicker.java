package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.PhysicsObjectsRegistry;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.ArrayList;
import java.util.List;

public class ObjectPicker extends VisWindow {
    boolean started;
    List<VisImage> imageList = new ArrayList<>();

    ScrollPane scrollPane;
    public ObjectPicker() {
        super("Object Picker");
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        started = true;
        RenderSystem.hasPicker = true;
        fillList();


        pack();
        setResizable(true);
        setPosition(0,Gdx.graphics.getHeight());
    }
    public void fillList() {
        if (scrollPane != null) {
            removeActor(scrollPane);
        }
        VisTable table = new VisTable();
        int i = 0;
        for (PhysicsObject physicsObject : PhysicsObjectsRegistry.physicsObjectHashMap.values()) {
            if (physicsObject.visible) {
                ObjectButton image = new ObjectButton(new SpriteDrawable(new Sprite(physicsObject.texture)),physicsObject.tooltip);
                image.addCaptureListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        PhysicsHandler.selectedPlaceableObject = physicsObject.type;
                    }
                });
                table.add(image).size(70f);
                if ((i+2)%6 == 0) {
                    table.row();
                }
            }
            i++;
        }
        scrollPane = new ScrollPane(table);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).spaceTop(8).fillX().expandX().row();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && !started) {
            remove();
        }
        started = false;
    }

    @Override
    public boolean remove() {
        RenderSystem.hasPicker = false;
        return super.remove();
    }
}
