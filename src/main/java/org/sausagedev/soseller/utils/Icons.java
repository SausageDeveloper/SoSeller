package org.sausagedev.soseller.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Icons {
    public String findIcon(String function, FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("icons");
        if (section == null) return null;
        Map<String, Object> icons = section.getValues(false);
        for (String icon : icons.keySet()) {
            String tag = (String) icons.get(icon + ".function");
            if (tag != null && tag.equalsIgnoreCase(function)) return icon;
        }
        return null;
    }

    public static ItemStack prepareDefaultItem(String path, String menu) {
        ConfigurationSection section = Config.getMenu(menu).getConfigurationSection(path);
        if (section == null) return null;
        String materialID = section.getString("material", "BEDROCK");
        Material material = Material.matchMaterial(materialID);
        boolean baseHead = materialID.contains("basehead-");
        if (baseHead) {
            materialID = materialID.replace("basehead-", "");

        }
        int amount = section.getInt("amount", 1);
        int modelData = section.getInt("model_data", 0);
        String name = section.getString("name", "none");
        List<String> lore = section.getStringList("lore");

        List<String> configFlags = section.getStringList("flags");
        List<ItemFlag> flags = new ArrayList<>();
        Arrays.asList(ItemFlag.values()).forEach(flag -> {if (configFlags.contains(flag.name())) flags.add(flag);});

        ConfigurationSection enchantsSection = section.getConfigurationSection("enchants");
        boolean enchantsExists = enchantsSection != null;

        ItemStack reserveItem = new ItemStack(material != null ? material : Material.AIR);
        ItemStack item = baseHead ? SkullCreator.itemFromBase64(materialID) : reserveItem;
        ItemBuilder itemBuilder = new ItemBuilder(item)
                .modelData(modelData)
                .amount(amount)
                .name(name)
                .lore(lore)
                .flags(flags);

        if (enchantsExists) {
            Map<String, Object> configEnchants = enchantsSection.getValues(false);
            Map<Enchantment, Integer> enchants = new HashMap<>();
            Arrays.asList(Enchantment.values()).forEach(enchant -> {
                String enchantName = enchant.getName();
                if (!configEnchants.containsKey(enchantName)) return;
                int lvl = (int) configEnchants.get(enchantName);
                enchants.put(enchant, lvl);
            });
            itemBuilder.enchants(enchants);
        }

        return itemBuilder.item();
    }
}
