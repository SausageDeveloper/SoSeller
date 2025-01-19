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
import org.sausagedev.soseller.utils.*;

import java.util.*;

public class Menu {
    private final SoSeller main;
    private final Database database;

    public Menu(SoSeller main, Database database) {
        this.main = main;
        this.database = database;
    }

    public void open(Player p, String menu) {
        YamlConfiguration config = Config.getMenu(menu);
        YamlConfiguration messages = Config.getMessages();
        String title = config.getString("title", "&aСкупщик");
        title = PlaceholderAPI.setPlaceholders(p, title);
        int size = config.getInt("size", 54);
        Inventory inv = Bukkit.createInventory(null, size, Utils.convert(title));

        UUID uuid = p.getUniqueId();
        double boost = database.getBoost(p.getUniqueId());
        double globalBoost = main.getConfig().getDouble("global_boost", 1);

        Map<String, Object> icons = config.getConfigurationSection("icons").getValues(false);
        for (String icon : icons.keySet()) {
            StringBuilder path = new StringBuilder("icons." + icon + ".");
            String function = config.getString(path + "function", "none");
            boolean isLoadItems = function.equalsIgnoreCase("load_items");
            boolean isLoadAutoSellItems = function.equalsIgnoreCase("load_autosell");
            List<String> items = config.getStringList(path + "items");
            List<Integer> slots = config.getIntegerList(path + "slots");
            int price = 0;
            Map<String, Object> boosts = main.getConfig().getConfigurationSection("boosts").getValues(false);
            for (String key : boosts.keySet()) {
                if (key.equalsIgnoreCase("message")) continue;
                Map<String, Object> boostParams = main.getConfig().getConfigurationSection("boosts." + key).getValues(false);
                if (boost >= Integer.parseInt(key)) continue;
                price = (int) boostParams.get("price");
                break;
            }
            if (function.equalsIgnoreCase("auto-sell")) {
                price = main.getConfig().getInt("auto-sell.cost");
                boolean isBought = database.isBoughtAutoSell(uuid);
                if (isBought) {
                    path.append("bought.");
                    boolean isEnabled = database.isEnabledAutoSell(uuid);
                    path = new StringBuilder(isEnabled ? path + "enabled." : path + "disabled.");
                } else {
                    path.append("have_not.");
                }
            }

            String mat = config.getString(path + "material", "BEDROCK");
            boolean basehead = mat.contains("basehead-");
            Material material = null;
            if (!basehead) {
                material = Material.valueOf(mat);
            } else if (!isLoadItems) {
                mat = mat.replace("basehead-", "");
            }

            int amount = config.getInt(path + "amount", 1);
            String displayName = config.getString(path + "name", "&e" + icon);
            displayName = displayName.replace("{boost}", String.valueOf(boost));
            displayName = displayName.replace("{globalboost}", String.valueOf(globalBoost));
            displayName = displayName.replace("{price}", String.valueOf(price));
            displayName = PlaceholderAPI.setPlaceholders(p, displayName);

            List<String> lore = config.getStringList(path + "lore");
            int customModelData = config.getInt(path + "model_data");
            ConfigurationSection section = config.getConfigurationSection(path + "enchants");
            Map<String, Object> enchants = new HashMap<>();
            if (section != null) {
                enchants = config.getConfigurationSection(path + "enchants").getValues(false);
            }
            List<String> flags = config.getStringList(path + "flags");

            ItemStack item = basehead ? SkullCreator.itemFromBase64(mat) : new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.setDisplayName(displayName);
            List<String> lines = new ArrayList<>();
            for (String line : lore) {
                line = line.replace("{boost}", String.valueOf(boost));
                line = line.replace("{globalboost}", String.valueOf(globalBoost));
                line = line.replace("{price}", String.valueOf(price));
                lines.add(line);
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


            if (!isLoadItems && !isLoadAutoSellItems) {
                displayName = PlaceholderAPI.setPlaceholders(p, displayName);
                meta.setDisplayName(Utils.convert(displayName));
                List<String> l2 = new ArrayList<>();
                lines.forEach(line -> l2.add(Utils.convert(PlaceholderAPI.setPlaceholders(p, line))));
                meta.setLore(l2);

                item.setItemMeta(meta);

                NBTItem itemTag = new NBTItem(item);
                itemTag.setString("SoSeller", function);

                slots.forEach(slot -> inv.setItem(slot, itemTag.getItem()));
            } else {
                for (String i : new ArrayList<>(items)) {
                    if (slots.isEmpty()) break;
                    Map<String, Object> materials = messages.getConfigurationSection("materials").getValues(false);
                    String itemEnabled = database.isAutoSellItem(uuid, i) ? "allow" : "deny";
                    String translatedItem = materials.containsKey(i) ? materials.get(i).toString() : i;
                    String msg = messages.getString(itemEnabled + "-autosell", "null");
                    String d2 = displayName.replace("{item_type}", translatedItem);
                    d2 = d2.replace("{can_autosell}", msg);
                    d2 = PlaceholderAPI.setPlaceholders(p, d2);
                    meta.setDisplayName(Utils.convert(d2));
                    List<String> l2 = new ArrayList<>();
                    lines.forEach(line -> {
                        line = line.replace("{item_type}", i);
                        line = line.replace("{can_autosell}", msg);
                        line = PlaceholderAPI.setPlaceholders(p, line);
                        l2.add(Utils.convert(line));
                    });
                    meta.setLore(l2);

                    item.setItemMeta(meta);

                    item.setType(Material.valueOf(i));

                    NBTItem itemTag = new NBTItem(item);
                    itemTag.setString("SoSeller", isLoadAutoSellItems ? "offon_autosell_items" : "none");

                    Object firstSlot = new ArrayList<>(slots).get(0);
                    inv.setItem((int) firstSlot, itemTag.getItem());

                    slots.remove(firstSlot);
                    items.remove(i);
                }
            }
        }
        MenuDetect.setMenu(menu);
        p.openInventory(inv);
    }
}