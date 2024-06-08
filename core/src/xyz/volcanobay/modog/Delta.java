package xyz.volcanobay.modog;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import xyz.volcanobay.modog.game.MaterialRegistry;
import xyz.volcanobay.modog.game.level.LevelHandeler;
import xyz.volcanobay.modog.game.sounds.SoundHandeler;
import xyz.volcanobay.modog.game.sounds.SoundRegistry;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevel;
import xyz.volcanobay.modog.networking.DeltaNetwork;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.PhysicsObjectsRegistry;
import xyz.volcanobay.modog.rendering.DeltaStage;
import xyz.volcanobay.modog.rendering.RenderSystem;
import xyz.volcanobay.modog.rendering.SkyRenderer;
import xyz.volcanobay.modog.screens.AddressPicker;

import java.util.logging.Logger;

public class Delta extends ApplicationAdapter {

    public static final int NETWORKING_VERSION = 0;

    public static Logger LOGGER = Logger.getLogger("Delta");

    public static DeltaStage stage;

    public static NetworkableLevel LEVEL;

    public static boolean periodicScheduled;

    @Override
    public void create() {
        PhysicsObjectsRegistry.registerObjects();
        RenderSystem.initialize();
        PhysicsHandler.initialize();
        MaterialRegistry.registerMaterials();
        LEVEL = PhysicsHandler.asLevel();
        LevelHandeler.addLevels();
        VisUI.load(VisUI.SkinScale.X1);

        stage = new DeltaStage(new ScreenViewport());
        final Table root = new Table();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(root);
        stage.addActor(new AddressPicker());
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        Cursor cursor = Gdx.graphics.newCursor(pixmap, 1, 1);
        pixmap.dispose();
        Gdx.graphics.setCursor(cursor);
        tickPeriodic();
        networkTick();
        SoundRegistry.registerSoundEvents();
    }

    @Override
    public void render() {
        RenderSystem.render();
        stage.act();
        stage.draw();
        LevelHandeler.addLevels();
        SoundHandeler.handleSoundEvents();
    }

//    public void periodic() {
//        tickPeriodic();
//    }

    public void tickPeriodic() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
//                periodic();
                tickPeriodic();
            }
        }, 0.01f);
        PhysicsHandler.objectTickPeriodic();
        PhysicsHandler.worldJointTickPeriodic();
        //DeltaNetwork.sendPacketToAllOthers(new A2ACursorUpdatePacket(CursorHandler.myCursor));
        DeltaNetwork.readDataTick();

        PhysicsHandler.handleInput();
        PhysicsHandler.physicsStep();

        DeltaNetwork.sendDataTick();
    }
    public void networkTick() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
//                periodic();
                networkTick();
            }
        }, 0.05f);
        PhysicsHandler.networkTick();
    }

	@Override
	public void resize(int width, int height) {
		if (width == 0 && height == 0) return; //see https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
		stage.getViewport().update(width, height, true);
		SkyRenderer.resize(width,height);
		PopupMenu.removeEveryMenu(stage);
	}

    @Override
    public void dispose() {

        RenderSystem.dispose();
        stage.dispose();
        for (PhysicsObject object : PhysicsHandler.physicsObjectMap.values()) {
            object.dispose();
        }
        VisUI.dispose();
    }

}
