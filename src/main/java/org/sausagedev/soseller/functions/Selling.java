package org.sausagedev.soseller.functions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.utils.ItemBuilder;
import org.sausagedev.soseller.utils.Utils;

import java.util.List;
import java.util.Map;

public class Selling {
    private final SoSeller main = SoSeller.getPlugin();

    public void sellItems(Player p, List<ItemStack> itemList, boolean withMessage) {
        DataManager.PlayerData playerData = DataManager.search(p.getUniqueId()), old = playerData.clone();
        double boost = playerData.getBoost();
        double globalBoost = Config.settings().globalBoost();
        Map<String, Object> priceList = Config.settings().sellItems();

        int profit = 0;
        int items = 0;

        for (ItemStack item : itemList) {
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
        playerData.addItems(finalItems);
        DataManager.replace(old, playerData);
        Utils.playSound(p, "onSellItems");

        if (!withMessage) return;
        String msg = Config.messages().sold();
        msg = msg.replace("{amount}", String.valueOf(items));
        msg = msg.replace("{profit}", String.valueOf(profit));
        p.sendMessage(Utils.convert(msg));
    }

    public void sellItem(Player p, ItemStack item, boolean withMessage) {
        DataManager.PlayerData playerData = DataManager.search(p.getUniqueId());
        double boost = playerData.getBoost();
        double globalBoost = Config.settings().globalBoost();
        Map<String, Object> priceList = Config.settings().sellItems();

        int profit = 0;

        if (item == null || item.getType().equals(Material.AIR) || new ItemBuilder(item).hasFunction()) return;
        String key = item.getType().toString();
        if (!priceList.containsKey(key)) return;
        int price = (int) priceList.get(key);
        int amount = item.getAmount();
        double money = price*amount*boost*globalBoost;
        main.getEconomy().depositPlayer(p.getName(), money);
        profit += (int) money;

        playerData.addItems(item.getAmount());
        item.setAmount(0);


        if (!withMessage) return;
        String msg = Config.messages().sold();
        msg = msg.replace("{amount}", String.valueOf(amount));
        msg = msg.replace("{profit}", String.valueOf(profit));
        p.sendMessage(Utils.convert(msg));
    }
}
