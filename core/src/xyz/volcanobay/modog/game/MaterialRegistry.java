package xyz.volcanobay.modog.game;

import xyz.volcanobay.modog.game.materials.CoalMaterial;

import java.util.HashMap;
import java.util.List;

public class MaterialRegistry {
    static HashMap<String,Material> materials = new HashMap<>();

    static void registerMaterial(String type, Material material) {
        materials.put(type,material);
    }
    public static Material getFromRegistery(String type) {
        return materials.getOrDefault(type,new CoalMaterial());
    }
    public static void registerMaterials() {
        registerMaterial("coal",new CoalMaterial());
    }
}
