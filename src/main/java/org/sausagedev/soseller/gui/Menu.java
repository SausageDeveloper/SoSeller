package org.sausagedev.soseller.gui;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Menu {

    public void open(Player p, String menu) {
        FileConfiguration config = Config.getMenu(menu);
        FileConfiguration main = Config.getSettings();
        FileConfiguration messages = Config.getMessages();
        String title = config.getString("title", "&aСкупщик");
        title = PlaceholderAPI.setPlaceholders(p, title);
        int size = config.getInt("size", 54);
        Inventory inv = Bukkit.createInventory(null, size, Utils.convert(title));

        CompletableFuture.runAsync(() -> {
                    UUID uuid = p.getUniqueId();
                    double boost = Database.getBoost(p.getUniqueId());
                    double globalBoost = main.getDouble("global_boost", 1);

                    Map<String, Object> icons = config.getConfigurationSection("icons").getValues(false);
                    for (String icon : icons.keySet()) {
                        StringBuilder path = new StringBuilder("icons." + icon + ".");
                        String function = config.getString(path + "function", "none").toLowerCase();
                        boolean isLoadItems = function.equals("load_items");
                        boolean isLoadAutoSellItems = function.equals("load_autosell");
                        List<String> items = config.getStringList(path + "items");
                        List<Integer> slots = config.getIntegerList(path + "slots");

                        int price = 0;
                        Map<String, Object> boosts = main.getConfigurationSection("boosts").getValues(false);
                        for (String key : boosts.keySet()) {
                            if (key.equalsIgnoreCase("message")) continue;
                            Map<String, Object> boostParams = main.getConfigurationSection("boosts." + key).getValues(false);
                            if (boost >= Integer.parseInt(key)) continue;
                            price = (int) boostParams.get("price");
                            break;
                        }

                        if (function.equals("auto-sell")) {
                            price = main.getInt("auto-sell.cost");
                            boolean isBought = Database.isBoughtAutoSell(uuid);
                            if (isBought) {
                                path.append("bought.");
                                boolean isEnabled = AutoSell.isEnabled(uuid);
                                path = new StringBuilder(isEnabled ? path + "enabled." : path + "disabled.");
                            } else {
                                path.append("have_not.");
                            }
                        }

                        String displayName = config.getString(path + "name", "&e" + icon);
                        displayName = displayName.replace("{boost}", String.valueOf(boost));
                        displayName = displayName.replace("{globalboost}", String.valueOf(globalBoost));
                        displayName = displayName.replace("{price}", String.valueOf(price));
                        displayName = PlaceholderAPI.setPlaceholders(p, displayName);

                        List<String> lore = config.getStringList(path + "lore");
                        List<String> lines = new ArrayList<>();
                        for (String line : lore) {
                            line = line.replace("{boost}", String.valueOf(boost));
                            line = line.replace("{globalboost}", String.valueOf(globalBoost));
                            line = line.replace("{price}", String.valueOf(price));
                            lines.add(line);
                        }

                        ItemStack reserveItem = Icons.prepareDefaultItem(String.valueOf(path), menu);
                        if (reserveItem == null) reserveItem = new ItemStack(Material.AIR);
                        ItemBuilder itemBuilder = new ItemBuilder(reserveItem)
                                .name(displayName)
                                .lore(lines);

                        if (!isLoadItems && !isLoadAutoSellItems) {
                            displayName = PlaceholderAPI.setPlaceholders(p, displayName);
                            displayName = Utils.convert(displayName);
                            List<String> l2 = new ArrayList<>();
                            lines.forEach(line -> l2.add(Utils.convert(PlaceholderAPI.setPlaceholders(p, line))));

                            itemBuilder.name(displayName).lore(l2).function(function);

                            slots.forEach(slot -> inv.setItem(slot, itemBuilder.item()));
                        } else {
                            for (String i : new ArrayList<>(items)) {
                                if (slots.isEmpty()) break;
                                Map<String, Object> materials = messages.getConfigurationSection("materials").getValues(false);
                                String itemEnabled = AutoSell.isEnabled(uuid, Material.valueOf(i)) ? "allow" : "deny";
                                String translatedItem = materials.containsKey(i) ? materials.get(i).toString() : i;
                                String msg = messages.getString(itemEnabled + "-autosell", "null");
                                String d2 = displayName.replace("{item_type}", i);
                                d2 = d2.replace("{item_type_display}", translatedItem);
                                d2 = d2.replace("{can_autosell}", msg);
                                d2 = PlaceholderAPI.setPlaceholders(p, d2);
                                d2 = Utils.convert(d2);
                                List<String> l2 = new ArrayList<>();
                                lines.forEach(line -> {
                                    line = line.replace("{item_type}", i);
                                    line = line.replace("{item_type_display}", translatedItem);
                                    line = line.replace("{can_autosell}", msg);
                                    line = PlaceholderAPI.setPlaceholders(p, line);
                                    l2.add(Utils.convert(line));
                                });

                                itemBuilder
                                        .name(d2)
                                        .lore(l2)
                                        .material(Material.matchMaterial(i))
                                        .function(isLoadAutoSellItems ? "offon_autosell_items" : "none");

                                Object firstSlot = new ArrayList<>(slots).get(0);
                                inv.setItem((int) firstSlot, itemBuilder.item());

                                slots.remove(firstSlot);
                                items.remove(i);
                            }
                        }
                    }
                });
        MenuDetect.setMenu(menu);
        p.openInventory(inv);
    }
}