package org.sausagedev.soseller.functions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Database;
import org.sausagedev.soseller.utils.ItemBuilder;
import org.sausagedev.soseller.utils.Utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Selling {
    private final SoSeller main = SoSeller.getPlugin();
    private final FileConfiguration config = Config.getSettings();
    private final FileConfiguration messages = Config.getMessages();

    public void sellItems(Player p, Inventory inv) {
        UUID uuid = p.getUniqueId();
        double boost = Database.getBoost(uuid);
        double globalBoost = config.getDouble("global_boost", 1);
        Map<String, Object> priceList = config.getConfigurationSection("sell_items").getValues(false);

        int profit = 0;
        int items = 0;

        for (ItemStack item : inv.getContents()) {
            if (item == null ||
                    item.getType().equals(Material.AIR) ||
                    new ItemBuilder(item).hasFunction()) continue;
            String key = item.getType().toString();
            if (!priceList.containsKey(key)) continue;
            int price = (int) priceList.get(key);
            int amount = item.getAmount();
            double money = price*amount*boost*globalBoost;
            main.getEconomy().depositPlayer(p.getName(), money);
            profit += (int) money;
            items += amount;
            item.setAmount(0);
        }

        if (profit == 0 || items == 0) return;

        int finalItems = items;
        CompletableFuture.runAsync(() -> Database.setItems(uuid, Database.getItems(uuid) + finalItems));

        String def = "&8 ┃&f Вы продали &e{amount} &fпредметов за &a{profit} &fмонет";
        String msg = messages.getString("sold", def);
        msg = msg.replace("{amount}", String.valueOf(items));
        msg = msg.replace("{profit}", String.valueOf(profit));
        p.sendMessage(Utils.convert(msg));
    }

    public void sellItem(Player p, ItemStack item, boolean withMessage) {
        UUID uuid = p.getUniqueId();
        double boost = Database.getBoost(uuid);
        double globalBoost = config.getDouble("global_boost", 1);
        Map<String, Object> priceList = config.getConfigurationSection("sell_items").getValues(false);

        int profit = 0;

        if (item == null || item.getType().equals(Material.AIR) || new ItemBuilder(item).hasFunction()) return;
        String key = item.getType().toString();
        if (!priceList.containsKey(key)) return;
        int price = (int) priceList.get(key);
        int amount = item.getAmount();
        double money = price*amount*boost*globalBoost;
        main.getEconomy().depositPlayer(p.getName(), money);
        profit += (int) money;

        CompletableFuture.runAsync(() -> Database.setItems(uuid, Database.getItems(uuid) + item.getAmount()));
        item.setAmount(0);


        if (!withMessage) return;
        String def = "&8 ┃&f Вы продали &e{amount} &fпредметов за &a{profit} &fмонет";
        String msg = messages.getString("sold", def);
        msg = msg.replace("{amount}", String.valueOf(amount));
        msg = msg.replace("{profit}", String.valueOf(profit));
        p.sendMessage(Utils.convert(msg));
    }
}
