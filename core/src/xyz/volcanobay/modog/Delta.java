package xyz.volcanobay.modog;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.PopupMenu;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.PhysicsObjectsRegistry;
import xyz.volcanobay.modog.rendering.DeltaStage;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.screens.AddressPicker;

import java.util.logging.Logger;

public class Delta extends ApplicationAdapter {
	public static Logger LOGGER = Logger.getLogger("Delta");
	public static DeltaStage stage;
	private MenuBar menuBar;

	@Override
	public void create () {
		PhysicsObjectsRegistry.registerObjects();
		RenderSystem.initialize();
		PhysicsHandler.initialize();
		VisUI.load(VisUI.SkinScale.X1);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		stage =  new DeltaStage(new ScreenViewport());
		final Table root = new Table();
		Gdx.input.setInputProcessor(stage);
		stage.addActor(root);
		stage.addActor(new AddressPicker());
	}

	@Override
	public void render () {
		RenderSystem.render();
		PhysicsHandler.handleInput();
		PhysicsHandler.physicsStep();
		NetworkHandler.handleFrame();
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		if (width == 0 && height == 0) return; //see https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
		stage.getViewport().update(width, height, true);
		PopupMenu.removeEveryMenu(stage);
	}

	@Override
	public void dispose () {
		RenderSystem.rayHandler.dispose();
		RenderSystem.batch.dispose();
		RenderSystem.img.dispose();
		stage.dispose();
		for (PhysicsObject object: PhysicsHandler.physicsObjectHashMap.values()) {
			object.dispose();
		}
		VisUI.dispose();
	}
}
