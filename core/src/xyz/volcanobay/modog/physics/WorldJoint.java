package xyz.volcanobay.modog.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import xyz.volcanobay.modog.core.interfaces.level.NetworkableLevelComponent;
import xyz.volcanobay.modog.networking.networkable.NetworkableUUID;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;
import xyz.volcanobay.modog.networking.stream.NetworkByteWriteStream;

import static xyz.volcanobay.modog.physics.PhysicsHandler.getPhysicsObjectFromBody;
import static xyz.volcanobay.modog.physics.PhysicsHandler.physicsObjectMap;

public class WorldJoint extends NetworkableLevelComponent {
    public Joint joint;
    public NetworkableUUID uuid;
    
    private WorldJoint() {}
    public WorldJoint(Joint joint, NetworkableUUID uuid) {
        this.joint = joint;
        this.uuid = uuid;
    }
    
    public void writeNewToNetwork(NetworkByteWriteStream writeStream) {
        writeStream.writeUUID(uuid);
        
        PhysicsObject objectA = getPhysicsObjectFromBody(joint.getBodyA());
        PhysicsObject objectB = getPhysicsObjectFromBody(joint.getBodyB());
        assert objectA != null && objectB != null;
        
        writeStream.writeUUID(objectA.uuid);
        writeStream.writeUUID(objectB.uuid);
        
        writeStream.writeVector2(joint.getAnchorA());
        writeStream.writeVector2(joint.getAnchorB());
        
        writeStream.writeFloat(((DistanceJoint) joint).getLength());
    }

    public static WorldJoint readNewFromNetwork(NetworkByteReadStream readStream) {
        WorldJoint worldJoint = new WorldJoint();
    
        worldJoint.uuid = readStream.readUUID();
        
        DistanceJointDef jointDef = new DistanceJointDef();
    
        Body bodyA = physicsObjectMap.get(readStream.readUUID()).body;
        Body bodyB = physicsObjectMap.get(readStream.readUUID()).body;
        
        Vector2 localAnchorA = readStream.readVector2();
        Vector2 localAnchorB = readStream.readVector2();
        
        jointDef.initialize(bodyA, bodyB, localAnchorA, localAnchorB);
    
        worldJoint.joint = PhysicsHandler.world.createJoint(jointDef);
        
        return worldJoint;
    }

    @Override
    public NetworkableUUID getNetworkUUID() {
        return uuid;
    }

    @Override
    public boolean shouldNetwork() {
        return true;
    }

    @Override
    public void writeToNetwork(NetworkByteWriteStream stream) {
        stream.writeFloat(((DistanceJoint) joint).getLength());
    }

    @Override
    public void readFromNetwork(NetworkByteReadStream stream) {
        ((DistanceJoint) joint).setLength(stream.readFloat());
    }
}
