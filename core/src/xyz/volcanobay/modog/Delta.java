package xyz.volcanobay.modog;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.PopupMenu;
import xyz.volcanobay.modog.networking.NetworkHandeler;
import xyz.volcanobay.modog.physics.PhysicsHandeler;
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
		RenderSystem.initialize();
		PhysicsHandeler.initialize();
		VisUI.load(VisUI.SkinScale.X1);

		stage =  new DeltaStage(new ScreenViewport());
		final Table root = new Table();
		Gdx.input.setInputProcessor(stage);
		stage.addActor(root);
		stage.addActor(new AddressPicker("Ip"));
	}

	@Override
	public void render () {
		PhysicsHandeler.handleInput();
		PhysicsHandeler.physicsStep();
		RenderSystem.render();
		PhysicsHandeler.renderObjects();
		stage.act();
		stage.draw();
		NetworkHandeler.handleFrame();
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
		VisUI.dispose();
	}
}
