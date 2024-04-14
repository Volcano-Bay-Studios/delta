package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.ArrayList;
import java.util.List;

public class PhysicsObject {
    public Body body;
    public List<TextButtons> textButtons = new ArrayList<>();
    public PhysicsObject() {
    }
    public PhysicsObject(Body body) {
        this.body = body;
    }
    public PhysicsObject create(Body body) {
        return new PhysicsObject(body);
    }
    public void tickPhysics() {
//        System.out.println("hu");
    }
    public void render() {

    }
    public void newButton(String string, ChangeListener changeListener) {
        textButtons.add(new TextButtons(string,changeListener));
    }
    public List<TextButtons> getContextOptions() {
        textButtons.clear();
        newButton("Delete", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PhysicsHandeler.bodiesForDeletion.add(body);
            }
        });
        return textButtons;
    }
}
