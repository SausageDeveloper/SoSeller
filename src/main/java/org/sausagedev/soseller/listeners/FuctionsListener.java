package org.sausagedev.soseller.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.сonfiguration.Config;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.functions.AutoSellModify;
import org.sausagedev.soseller.functions.BoostsModify;
import org.sausagedev.soseller.functions.Selling;
import org.sausagedev.soseller.gui.Menu;
import org.sausagedev.soseller.utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FuctionsListener implements Listener {
    private final AutoSellModify autoSellModify = new AutoSellModify();
    private final BoostsModify boostsModify = new BoostsModify();
    private final Selling selling = new Selling();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player p = Bukkit.getPlayer(e.getWhoClicked().getName());
        if (p == null) return;
        DataManager.PlayerData playerData = DataManager.search(p.getUniqueId());
        if (item == null || item.getType() == Material.AIR) return;
        ItemBuilder itemBuilder = new ItemBuilder(item);
        if (!itemBuilder.hasFunction()) return;

        String f = itemBuilder.function().toLowerCase();
        Menu menu = new Menu();
        UUID uuid = p.getUniqueId();
        String currentMenu = MenuDetect.getMenu(uuid) != null ? MenuDetect.getMenu(uuid) : "main";

        if (f.contains("move_to-")) {
            f = f.replace("move_to-", "");
            menu.open(p, f);
            Utils.playSound(p, "onSwapGui");
            return;
        } else if (f.contains("buy-")) {
            f = f.replace("buy-", "");
            List<String> params = Arrays.asList(f.split(";"));
            int items = playerData.getItems();
            int requiredItems = Integer.parseInt(params.get(0));
            int price = Integer.parseInt(params.get(1));
            if (items < requiredItems) return;
            else if (price == 1) return;
        }

        switch (f) {
            case "offon_autosell_items":
                autoSellModify.offOnAutoSellItem(p, item.getType());;
                menu.open(p, currentMenu);
                return;
            case "sell_all":
                boolean withMsg = (boolean) Config.settings().autoSell().get("message");
                selling.sellItems(p, Arrays.asList(e.getInventory().getContents()), withMsg);
                menu.open(p, currentMenu);
                return;
            case "buy_boost":
                boostsModify.buyBoost(p);
                menu.open(p, currentMenu);
                return;
            case "auto-sell":
                boolean bought = playerData.isAutoSellBought();
                if (bought) {
                    boolean isEnabled = AutoSell.isEnabled(uuid);
                    Utils.playSound(p, "onSwapAutoSell");
                    if (isEnabled) {
                        AutoSell.disable(uuid);
                        menu.open(p, currentMenu);
                        return;
                    }
                    AutoSell.enable(uuid);
                    menu.open(p, currentMenu);
                    return;
                }
                autoSellModify.buyAutoSell(p);
                menu.open(p, currentMenu);
        }
    }
}