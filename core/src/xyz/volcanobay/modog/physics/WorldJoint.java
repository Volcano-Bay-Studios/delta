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
        float myDonatedCharge = objectA.charge/10;
        float objectDonatedCharge = objectB.charge/10;
        objectA.charge += objectDonatedCharge;
        objectA.charge -= myDonatedCharge;
        objectB.charge += myDonatedCharge;
        objectB.charge -= objectDonatedCharge;
        if (objectA.charge> DeltaConstants.maxCharge)
            objectA.charge = DeltaConstants.maxCharge;
        if (objectB.charge> DeltaConstants.maxCharge)
            objectB.charge = DeltaConstants.maxCharge;
        }
    }
}
