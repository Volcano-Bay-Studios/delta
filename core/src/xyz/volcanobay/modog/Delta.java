package xyz.volcanobay.modog;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.PopupMenu;
import xyz.volcanobay.modog.networking.CursorHandeler;
import xyz.volcanobay.modog.networking.NetworkHandler;
import xyz.volcanobay.modog.networking.NetworkableUUID;
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
	public static NetworkableUUID uuid = NetworkableUUID.randomUUID();
	public static boolean periodicScheduled;

	@Override
	public void create () {
		PhysicsObjectsRegistry.registerObjects();
		RenderSystem.initialize();
		PhysicsHandler.initialize();
		VisUI.load(VisUI.SkinScale.X1);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		System.out.println("Client UUID is "+uuid);

		stage =  new DeltaStage(new ScreenViewport());
		final Table root = new Table();
		Gdx.input.setInputProcessor(stage);
		stage.addActor(root);
		stage.addActor(new AddressPicker());
//		Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
//		Cursor cursor = Gdx.graphics.newCursor(pixmap, 1 ,1);
//		pixmap.dispose();
//		Gdx.graphics.setCursor(cursor);
	}

	@Override
	public void render () {
		RenderSystem.render();
		PhysicsHandler.handleInput();
		PhysicsHandler.physicsStep();
		stage.act();
		stage.draw();
		NetworkHandler.handleFrame();
		if (!periodicScheduled) {
			Timer.schedule(new Timer.Task() {
				@Override
				public void run() {
					periodic();
					periodicScheduled = false;
				}
			},10);
			periodicScheduled = true;
		}
	}
	public void periodic() {
		NetworkHandler.periodic();
	}

	@Override
	public void resize(int width, int height) {
		if (width == 0 && height == 0) return; //see https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
		stage.getViewport().update(width, height, true);
		PopupMenu.removeEveryMenu(stage);
	}

	@Override
	public void dispose () {

		RenderSystem.dispose();
		stage.dispose();
		for (PhysicsObject object: PhysicsHandler.physicsObjectHashMap.values()) {
			object.dispose();
		}
		VisUI.dispose();
	}
}
