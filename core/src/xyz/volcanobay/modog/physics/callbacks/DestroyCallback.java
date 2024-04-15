package xyz.volcanobay.modog.physics.callbacks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import xyz.volcanobay.modog.Delta;
import xyz.volcanobay.modog.physics.PhysicsHandler;

public class DestroyCallback implements RayCastCallback {

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        Delta.LOGGER.info("WTF!");
        PhysicsHandler.bodiesForDeletion.add(fixture.getBody());
        return 0;
    }
}
