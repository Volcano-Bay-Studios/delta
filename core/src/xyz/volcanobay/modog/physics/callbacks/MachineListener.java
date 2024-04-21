package xyz.volcanobay.modog.physics.callbacks;

import com.badlogic.gdx.physics.box2d.*;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.game.objects.MachineObject;

public class MachineListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        PhysicsObject physicsObjectA = PhysicsHandler.getPhysicsObjectFromBody(bodyA);
        PhysicsObject physicsObjectB = PhysicsHandler.getPhysicsObjectFromBody(bodyB);
        if (physicsObjectA != null && physicsObjectB != null) {
            if (physicsObjectA instanceof MachineObject machine) {
                machine.contact(physicsObjectB);
            }
            if (physicsObjectB instanceof MachineObject machine) {
                machine.contact(physicsObjectA);
            }
        } else
            return;
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        PhysicsObject physicsObjectA = PhysicsHandler.getPhysicsObjectFromBody(bodyA);
        PhysicsObject physicsObjectB = PhysicsHandler.getPhysicsObjectFromBody(bodyB);
        if (physicsObjectA != null && physicsObjectB != null) {
            if (physicsObjectA instanceof MachineObject machine) {
                machine.removeContact(physicsObjectB);
            }
            if (physicsObjectB instanceof MachineObject machine) {
                machine.removeContact(physicsObjectA);
            }
        } else
            return;
    }
    public static void removeFromAll(PhysicsObject object) {
        for (PhysicsObject object1 : PhysicsHandler.physicsObjectHashMap.values()) {
            if (object1 instanceof MachineObject machineObject) {
                machineObject.removeContact(object);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
