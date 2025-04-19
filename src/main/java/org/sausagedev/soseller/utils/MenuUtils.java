package org.sausagedev.soseller.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.configuration.data.GuiField;
import org.sausagedev.soseller.configuration.data.MessagesField;
import org.sausagedev.soseller.configuration.data.SettingsField;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.gui.CustomHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MenuUtils {

    public static Inventory generate(Player p, String menu) {
        GuiField menuSettings = Config.guis().get(menu);
        SettingsField settings = Config.settings();
        MessagesField messages = Config.messages();
        String title = menuSettings.title();
        if (SoSeller.usePAPI()) title = PlaceholderAPI.setPlaceholders(p, title);
        int size = menuSettings.size();
        Inventory inv = Bukkit.createInventory(new CustomHolder(), size, Utils.convert(title));

        CompletableFuture.runAsync(() -> {
            UUID uuid = p.getUniqueId();
            DataManager.PlayerData playerData = DataManager.search(uuid);
            double boost = playerData.getBoost();
            double globalBoost = Config.settings().globalBoost();

            ConfigurationSection icons = menuSettings.icons();
            for (String icon : icons.getValues(false).keySet()) {
                StringBuilder path = new StringBuilder(icon + ".");
                String function = icons.getString(path + "function", "none").toLowerCase();
                boolean isLoadItems = function.equals("load_items");
                boolean isLoadAutoSellItems = function.equals("load_autosell");
                List<String> items = icons.getStringList(path + "items");
                List<Integer> slots = icons.getIntegerList(path + "slots");

                int price = 0;
                ConfigurationSection boosts = settings.boosts();
                for (String key : boosts.getValues(false).keySet()) {
                    if (key.equalsIgnoreCase("message")) continue;
                    Map<String, Object> boostParams = boosts.getConfigurationSection(key).getValues(false);
                    if (boost >= Integer.parseInt(key)) continue;
                    price = (int) boostParams.get("price");
                    break;
                }

                if (function.equals("auto-sell")) {
                    price = (int) settings.autoSell().get("cost");
                    boolean isBought = playerData.isAutoSellBought();
                    if (isBought) {
                        path.append("bought.");
                        boolean isEnabled = AutoSell.isEnabled(uuid);
                        path = new StringBuilder(isEnabled ? path + "enabled." : path + "disabled.");
                    } else {
                        path.append("have_not.");
                    }
                }

                String displayName = icons.getString(path + "name", "&e" + icon);
                displayName = displayName.replace("{boost}", String.valueOf(boost));
                displayName = displayName.replace("{globalboost}", String.valueOf(globalBoost));
                displayName = displayName.replace("{price}", String.valueOf(price));

                List<String> lore = icons.getStringList(path + "lore");
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
                    displayName = Utils.convert(displayName);
                    List<String> l2 = new ArrayList<>();
                    for (String line : lines) {
                        if (SoSeller.usePAPI()) line = PlaceholderAPI.setPlaceholders(p, line);
                        l2.add(Utils.convert(line));
                    }

                    itemBuilder.lore(l2).function(function);
                    if (SoSeller.usePAPI()) displayName = PlaceholderAPI.setPlaceholders(p, displayName);
                    itemBuilder.name(displayName);

                    slots.forEach(slot -> inv.setItem(slot, itemBuilder.item()));
                } else {
                    for (String i : new ArrayList<>(items)) {
                        if (slots.isEmpty()) break;
                        Map<String, Object> materials = messages.materials();
                        boolean itemEnabled = AutoSell.isEnabled(uuid, Material.valueOf(i));
                        String translatedItem = materials.containsKey(i) ? materials.get(i).toString() : i;
                        String msg = itemEnabled ? messages.allowAutosell() : messages.denyAutosell();
                        String d2 = displayName.replace("{item_type}", i);
                        d2 = d2.replace("{item_type_display}", translatedItem);
                        d2 = d2.replace("{can_autosell}", msg);
                        if (SoSeller.usePAPI()) d2 = PlaceholderAPI.setPlaceholders(p, d2);
                        d2 = Utils.convert(d2);
                        List<String> l2 = new ArrayList<>();
                        lines.forEach(line -> {
                            line = line.replace("{item_type}", i);
                            line = line.replace("{item_type_display}", translatedItem);
                            line = line.replace("{can_autosell}", msg);
                            if (SoSeller.usePAPI()) line = PlaceholderAPI.setPlaceholders(p, line);
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
        return inv;
    }
}
