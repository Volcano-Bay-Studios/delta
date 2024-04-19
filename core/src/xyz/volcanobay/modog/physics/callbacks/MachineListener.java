package xyz.volcanobay.modog.physics.callbacks;

import com.badlogic.gdx.physics.box2d.*;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.physics.objects.MachineObject;

public class MachineListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        PhysicsObject physicsObjectA = PhysicsHandler.getPhysicsObjectFromBody(bodyA);
        PhysicsObject physicsObjectB = PhysicsHandler.getPhysicsObjectFromBody(bodyB);
        if (physicsObjectA != null && physicsObjectB != null) {
            if (physicsObjectA instanceof MachineObject machine) {
                machine.contact(bodyB, physicsObjectB);
            } else if (physicsObjectB instanceof MachineObject machine) {
                machine.contact(bodyA,physicsObjectA);
            }
        } else
            return;
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
