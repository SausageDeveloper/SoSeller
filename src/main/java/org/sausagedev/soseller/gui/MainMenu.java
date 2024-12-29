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
import java.util.*;

public class MainMenu {
    private final SoSeller main;
    private final Utils utils;
    private final SellerUtils sellerUtils;
    private Inventory inv;

    public MainMenu(SoSeller main, Utils utils, SellerUtils sellerUtils) {
        this.main = main;
        this.utils = utils;
        this.sellerUtils = sellerUtils;
    }

    public void open(Player p) {
        String title = config().getString("title", "&aСкупщик");
        title = PlaceholderAPI.setPlaceholders(p, title);
        int size = config().getInt("size", 54);
        inv = Bukkit.createInventory(null, size, utils.convert(title));

        Map<String, Object> icons = config().getConfigurationSection("icons").getValues(false);
        for (String icon : icons.keySet()) {
            StringBuilder path = new StringBuilder("icons." + icon + ".");
            String function = config().getString(path + "function", "none");
            List<Integer> slots = config().getIntegerList(path + "slots");
            UUID uuid = p.getUniqueId();
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
            if (function.equalsIgnoreCase("autosell")) {
                price = main.getConfig().getInt("auto-sell.cost");
                boolean isBought = sellerUtils.isBoughtAutoSell(uuid);
                if (isBought) {
                    path.append("bought.");
                    boolean isEnabled = sellerUtils.isEnabledAutoSell(uuid);
                    path = new StringBuilder(isEnabled ? path + "enabled." : path + "disabled.");
                } else {
                    path.append("have_not.");
                }
            }

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

            ItemStack item = basehead ? SkullCreator.itemFromBase64(mat) : new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
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
            info(p);
        }
        p.openInventory(inv);
    }

    public void info(Player p) {
        int custom_model_data = config().getInt("info.custom_model_data", 0);

        String mat = config().getString("info.material", "BEDROCK");
        boolean basehead = mat.contains("basehead-");
        Material material = null;
        if (!basehead) {
            material = Material.valueOf(mat);
        } else {
            mat = mat.replace("basehead-", "");
        }

        int slot = config().getInt("info.slot");
        String name = config().getString("info.name", "&6Скупщик");
        String boost = String.valueOf(sellerUtils.getBoost(p.getUniqueId()));
        name = name.replace("!boost", boost);
        name = PlaceholderAPI.setPlaceholders(p, name);
        List<String> lore = config().getStringList("info.lore");

        ItemStack item = basehead ? SkullCreator.itemFromBase64(mat) : new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.setCustomModelData(custom_model_data);
        meta.setDisplayName(utils.convert(name));
        List<String> lines = new ArrayList<>();
        for (String line : lore) {
            line = line.replace("!boost", boost);
            line = PlaceholderAPI.setPlaceholders(p, line);
            lines.add(utils.convert(line));
        }
        meta.setLore(lines);
        item.setItemMeta(meta);
        NBTItem nbt = new NBTItem(item);
        nbt.setString("SoSeller", "seller");

        inv.setItem(slot, nbt.getItem());
    }

    public YamlConfiguration config() {
        File file = new File(main.getDataFolder(), "gui/main.yml");
        if (!file.exists()) {
            main.saveResource("gui/main.yml", false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
