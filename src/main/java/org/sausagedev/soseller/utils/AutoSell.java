package org.sausagedev.soseller.utils;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AutoSell {
    private static HashMap<UUID, List<Material>> listOfMaterials;

    public static void setListOfMaterials(HashMap<UUID, List<Material>> listOfMaterials) {
        AutoSell.listOfMaterials = listOfMaterials;
    }

    public static void enable(UUID uuid) {
        listOfMaterials.put(uuid, new ArrayList<>());
    }

    public static void disable(UUID uuid) {
        listOfMaterials.remove(uuid);
    }

    public static boolean isEnabled(UUID uuid) {
        return listOfMaterials.containsKey(uuid);
    }

    public static boolean isEnabled(UUID uuid, Material m) {
        List<Material> materials = listOfMaterials.get(uuid);
        return materials != null && materials.contains(m);
    }

    public static void enableMaterial(UUID uuid, Material m) {
        if (!isEnabled(uuid)) return;
        List<Material> materials = new ArrayList<>();

        materials.add(m);
        if (listOfMaterials.containsKey(uuid)) {
            materials.addAll(listOfMaterials.get(uuid));
            listOfMaterials.replace(uuid, materials);
            return;
        }
        listOfMaterials.put(uuid, materials);
    }

    public static void disableMaterial(UUID uuid, Material m) {
        if (!isEnabled(uuid) || !listOfMaterials.containsKey(uuid)) return;
        List<Material> materials = listOfMaterials.get(uuid);
        materials.remove(m);
        listOfMaterials.replace(uuid, materials);
    }
}
