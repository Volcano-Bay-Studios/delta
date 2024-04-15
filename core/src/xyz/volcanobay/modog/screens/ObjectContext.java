package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.rendering.RenderSystem;

import java.util.ArrayList;
import java.util.List;

public class ObjectContext extends VisWindow {
    public List<VisTextButton> textButtons = new ArrayList<>();
    public Vector2 startPos = new Vector2();
    public PhysicsObject physicsObject;
    public ObjectContext(PhysicsObject object, List<TextButtons> buttons) {
        super("Context");
        this.physicsObject = object;
        int i = 0;
        for (TextButtons button: buttons) {
            textButtons.add(new VisTextButton(button.name));
            add(textButtons.get(i));
            textButtons.get(i).addListener(button.changeListener);
            i++;
        }

        Vector2 mousePos = PhysicsHandler.getMouseWorldPosition();
        pack();
        setPosition(mousePos.x, mousePos.y);

        startPos = mousePos;
    }
    public Vector2 worldPos() {
        Vector3 pos = RenderSystem.camera.project(new Vector3(startPos.x,startPos.y,0));
        return new Vector2(pos.x,pos.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.setPosition(worldPos().x, worldPos().y);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && PhysicsHandler.getPhysicsObjectFromBody(PhysicsHandler.mouseBody) != physicsObject) {
            remove();
        }
    }
}
