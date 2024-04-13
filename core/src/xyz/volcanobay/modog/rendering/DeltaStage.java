package xyz.volcanobay.modog.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DeltaStage extends Stage {
    public DeltaStage(Viewport viewport) {
        super(viewport);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        RenderSystem.scrolledAmount = amountX+amountY;
        return super.scrolled(amountX, amountY);
    }
}
