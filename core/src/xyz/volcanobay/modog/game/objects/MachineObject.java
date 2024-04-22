package xyz.volcanobay.modog.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import xyz.volcanobay.modog.game.Material;
import xyz.volcanobay.modog.game.MaterialRegistry;
import xyz.volcanobay.modog.game.sounds.SoundHandeler;
import xyz.volcanobay.modog.physics.PhysicsHandler;
import xyz.volcanobay.modog.physics.PhysicsObject;
import xyz.volcanobay.modog.screens.TextButtons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MachineObject  extends PhysicsObject {
    List<PhysicsObject> objectsImTouching = new ArrayList<>();
    public HashMap<String,Integer> inventory = new HashMap<>();
    List<String> filter = new ArrayList<>();
    public int inventorySize;
    public int inventoryUsed;
    public boolean givesUpInventory;
    public boolean contactCharge;
    public MachineObject() {
        super();
    }

    public MachineObject(Body body) {
        super(body);
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
    }
    public int tryPutInInventory(Material material) {
        if (inventoryUsed == inventorySize) {
            return 0;
        }
        int amountToPut = Math.min(inventorySize-inventoryUsed,material.amount);
        if (inventory.containsKey(material.type)) {
            inventory.put(material.type,inventory.get(material.type) + amountToPut);
        } else {
            inventory.put(material.type,amountToPut);
        }
        material.amount -= amountToPut;
        inventoryUsed += amountToPut;
        return amountToPut;
    }
    public void checkInventory() {
        for (String material : inventory.keySet()) {
            if (inventory.get(material) <= 0) {
                inventory.remove(material);
            }
        }
    }
    public void dropMaterial(String material,int amount) {
        if (inventory.containsKey(material) && inventory.get(material) <= amount) {
            Material ourMaterial = getMaterial(material);
            ourMaterial.setAmount(1);
            for (int i = 0; i < amount; i++) {
                PhysicsHandler.addMaterialObject(body.getPosition().add((texture.getWidth() / PhysicsHandler.scaleDown) + .02f, 0), ourMaterial.clone());
            }
            inventoryUsed -= amount;
            inventory.put(material, inventory.get(material)-amount);
        }
        if (inventory.get(material) <= 0) {
            inventory.remove(material);
        }
    }
    public void removeMaterial(String key, int amount) {
        if (inventory.containsKey(key)) {
            inventory.put(key,inventory.get(key)-amount);
            inventoryUsed -= amount;
        }
        checkInventory();
    }
    public void addMaterial(String key, int amount) {
        if (inventory.containsKey(key)) {
            inventory.put(key,inventory.get(key)-amount);
            inventoryUsed += amount;
        }
        checkInventory();
    }

    public Material getMaterial(String key) {
        if (inventory.containsKey(key)) {
            Material ourMaterial = MaterialRegistry.getFromRegistery(key);
            ourMaterial.setAmount(inventory.get(key));
            return ourMaterial;
        }
        return null;
    }

    public void emptyInventory() {
        for (String materialKey : inventory.keySet()) {
            dropMaterial(materialKey,inventory.get(materialKey));
        }
    }
    @Override
    public void tick() {
        super.tick();
        for (PhysicsObject objectB : objectsImTouching) {
            if (objectB instanceof MaterialObject materialObject) {
                tryPutInInventory(materialObject.material);
                if (materialObject.material.amount <= 0) {
                    materialObject.killMyself();
                }
            }
            if (this.givesUpInventory && objectB instanceof MachineObject machineObject) {
                if (!machineObject.givesUpInventory && machineObject.inventorySize > 0) {
                    for (String string : inventory.keySet()) {
                        Material ourMaterial = MaterialRegistry.getFromRegistery(string);
                        ourMaterial.setAmount(inventory.get(string));
                        int remove = machineObject.tryPutInInventory(ourMaterial);
                        if (remove > 0) {
                            removeMaterial(string,remove);
                        }
                    }
                }
            }
            if (objectB != null && contactCharge) {
                float aDifference = this.getMaxCharge();
                float bDifference = objectB.getMaxCharge();
                float myDonatedCharge =     (this.charge/(aDifference));
                float objectDonatedCharge = (objectB.charge/(bDifference));
                myDonatedCharge = Math.min(this.charge,myDonatedCharge);
                objectDonatedCharge = Math.min(objectB.charge,objectDonatedCharge);
                this.charge += objectDonatedCharge;
                this.charge -= myDonatedCharge;
                objectB.charge += myDonatedCharge;
                objectB.charge -= objectDonatedCharge;
                if (this.charge> this.getMaxCharge())
                    this.charge = this.getMaxCharge();
                if (objectB.charge> objectB.getMaxCharge())
                    objectB.charge = objectB.getMaxCharge();
            }
        }
    }

    @Override
    public MachineObject create(Body body) {
        return new MachineObject(body);
    }

    @Override
    public void initialise() {
        super.initialise();
        pickTexture();
        type = "machine";
    }
    public void contact(PhysicsObject object) {
        objectsImTouching.add(object);
    }
    public void removeContact(PhysicsObject object) {
        objectsImTouching.remove(object);
    }

    @Override
    public void pickTexture() {
        texture = new Texture("crate.png");
    }
    public MachineObject getSelf() { return this; }


    @Override
    public void createFixture() {
        PolygonShape groundBox = new PolygonShape();
        fixtureScale = new Vector2((float) texture.getWidth() /2-.3f, (float) texture.getHeight() /2-.3f);
        groundBox.setAsBox(fixtureScale.x/ PhysicsHandler.scaleDown, fixtureScale.y/PhysicsHandler.scaleDown);
        body.createFixture(groundBox,1f);
        body.getFixtureList().get(0).setDensity(4f);
        groundBox.dispose();
    }

    @Override
    public List<TextButtons> getContextOptions() {
        super.getContextOptions();
        if (inventorySize > 0 && inventoryUsed > 0) {
            newButton("Empty", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    getSelf().emptyInventory();
                    SoundHandeler.playSound(body.getPosition(),"empty",false);
                    actor.getParent().remove();
                }
            });

        }
        return textButtons;
    }
}
