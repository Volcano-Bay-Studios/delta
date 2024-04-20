package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.physics.box2d.Joint;
import xyz.volcanobay.modog.game.DeltaConstants;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;

import static xyz.volcanobay.modog.physics.PhysicsHandler.getPhysicsObjectFromBody;

public class WorldJoint {
    public Joint joint;
    public NetworkableUUID uuid;
    public WorldJoint() {}
    public WorldJoint(Joint joint, NetworkableUUID uuid) {
        this.joint = joint;
        this.uuid = uuid;
    }
    public void propagatePower() {
        PhysicsObject objectA = getPhysicsObjectFromBody(joint.getBodyA());
        PhysicsObject objectB = getPhysicsObjectFromBody(joint.getBodyB());
        if (objectA != null && objectB != null) {
        float aDifference = objectA.getMaxCharge();
        float bDifference = objectB.getMaxCharge();
        float myDonatedCharge =     (objectA.charge/(aDifference));
        float objectDonatedCharge = (objectB.charge/(bDifference));
        myDonatedCharge = Math.min(objectA.charge,myDonatedCharge);
        objectDonatedCharge = Math.min(objectB.charge,objectDonatedCharge);
        objectA.charge += objectDonatedCharge;
        objectA.charge -= myDonatedCharge;
        objectB.charge += myDonatedCharge;
        objectB.charge -= objectDonatedCharge;
        if (objectA.charge> objectA.getMaxCharge())
            objectA.charge = objectA.getMaxCharge();
        if (objectB.charge> objectB.getMaxCharge())
            objectB.charge = objectB.getMaxCharge();
        }
    }
}
