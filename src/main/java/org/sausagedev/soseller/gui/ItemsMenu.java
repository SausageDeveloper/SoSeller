package org.sausagedev.soseller.gui;

import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.SellerUtils;
import org.sausagedev.soseller.utils.SkullCreator;
import org.sausagedev.soseller.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsMenu {
    private final SoSeller main;
    private final Utils utils;
    private final SellerUtils sellerUtils;

    public ItemsMenu(SoSeller main, Utils utils, SellerUtils sellerUtils) {
        this.main = main;
        this.utils = utils;
        this.sellerUtils = sellerUtils;
    }

    public void open(Player p) {
        String title = config().getString("title", "&aСкупщик");
        title = PlaceholderAPI.setPlaceholders(p, title);
        int size = config().getInt("size", 54);
        Inventory inv = Bukkit.createInventory(null, size, utils.convert(title));

        Map<String, Object> icons = config().getConfigurationSection("icons").getValues(false);
        for (String icon : icons.keySet()) {
            String path = "icons." + icon + ".";
            String function = config().getString(path + "function", "none");

            String mat = config().getString(path + "material", "BEDROCK");
            boolean basehead = mat.contains("basehead-");
            Material material = null;
            if (!basehead) {
                material = Material.valueOf(mat);
            } else {
                mat = mat.replace("basehead-", "");
            }

            int amount = config().getInt(path + "amount", 1);
            String displayName = config().getString(path + "name", "&e" + icon);
            double boost = sellerUtils.getBoost(p.getUniqueId());
            int price = 0;
            Map<String, Object> boosts = main.getConfig().getConfigurationSection("boosts").getValues(false);
            for (String key : boosts.keySet()) {
                if (key.equalsIgnoreCase("value")) continue;
                if (key.equalsIgnoreCase("message")) continue;
                if (boost < Integer.parseInt(key)) {
                    price = (int) boosts.get(key);
                    break;
                }
            }
            displayName = displayName.replace("!boost", String.valueOf(boost));
            displayName = displayName.replace("!price", String.valueOf(price));
            displayName = PlaceholderAPI.setPlaceholders(p, displayName);

            List<String> lore = config().getStringList(path + "lore");
            int customModelData = config().getInt(path + "custom_model_data");
            ConfigurationSection section = config().getConfigurationSection(path + "enchants");
            Map<String, Object> enchants = new HashMap<>();
            if (section != null) {
                enchants = config().getConfigurationSection(path + "enchants").getValues(false);
            }
            List<String> flags = config().getStringList(path + "flags");
            List<Integer> slots = config().getIntegerList(path + "slots");

            ItemStack item = basehead ? SkullCreator.itemFromBase64(mat) : new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            meta.setDisplayName(utils.convert(displayName));
            List<String> lines = new ArrayList<>();
            for (String line : lore) {
                line = line.replace("!boost", String.valueOf(boost));
                line = line.replace("!price", String.valueOf(price));
                line = PlaceholderAPI.setPlaceholders(p, line);
                lines.add(utils.convert(line));
            }
            meta.setCustomModelData(customModelData);
            for (Enchantment enchant : Enchantment.values()) {
                if (!enchants.containsKey(enchant.getName())) continue;
                int lvl = (int) enchants.get(enchant.getName());
                meta.addEnchant(enchant, lvl, true);
            }
            for (ItemFlag flag : ItemFlag.values()) {
                if (!flags.contains(flag.toString())) continue;
                meta.addItemFlags(flag);
            }
            meta.setLore(lines);
            item.setItemMeta(meta);
            item.setAmount(amount);
            NBTItem itemTag = new NBTItem(item);
            itemTag.setString("SoSeller", function);
            for (Integer slot : slots) {
                inv.setItem(slot, itemTag.getItem());
            }
        }
        p.openInventory(inv);
    }

    public YamlConfiguration config() {
        File file = new File(main.getDataFolder(), "gui/items.yml");
        if (!file.exists()) {
            main.saveResource("gui/items.yml", false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
