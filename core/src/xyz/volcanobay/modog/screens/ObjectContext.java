package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.volcanobay.modog.physics.PhysicsHandeler;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.ArrayList;
import java.util.List;

public class ObjectContext extends VisWindow {
    public List<VisTextButton> textButtons = new ArrayList<>();
    public ObjectContext(Vector2 pos,List<TextButtons> buttons) {
        super("Context");
        int i = 0;
        for (TextButtons button: buttons) {
            textButtons.add(new VisTextButton(button.name));
            add(textButtons.get(i));
            textButtons.get(i).addListener(button.changeListener);
            i++;
        }

        Vector2 mousePos = PhysicsHandeler.getMouseWorldPosition();
        pack();
        setPosition(mousePos.x, mousePos.y);
    }
}
