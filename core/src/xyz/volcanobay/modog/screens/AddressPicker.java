package xyz.volcanobay.modog.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.networking.NetworkHandeler;

public class AddressPicker extends VisWindow {
    VisTextField textField;
    VisValidatableTextField validatableTextField;
    VisTextButton textButton;
    public AddressPicker(String title) {
        super("Enter IP:                         Port:");
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        textField = new VisTextField();
        validatableTextField = new VisValidatableTextField();
        textButton = new VisTextButton("Join");


        add(textField);
        add(validatableTextField);
        add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int port = Integer.parseInt(validatableTextField.getText());
                NetworkHandeler.joinServer(textField.getText(),port);
            }
        });

        pack();
        setPosition(Gdx.graphics.getWidth(), 18);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(Gdx.graphics.getWidth(), 18);
        super.draw(batch, parentAlpha);
    }

}
