package org.sausagedev.soseller.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.functions.AutoSellModify;
import org.sausagedev.soseller.functions.BoostsModify;
import org.sausagedev.soseller.functions.Selling;
import org.sausagedev.soseller.gui.Menu;
import org.sausagedev.soseller.utils.*;

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
        if (item == null || item.getType() == Material.AIR) return;
        ItemBuilder itemBuilder = new ItemBuilder(item);
        if (!itemBuilder.hasFunction()) return;

        String f = itemBuilder.function().toLowerCase();
        Menu menu = new Menu();
        String currentMenu = MenuDetect.getMenu();

        if (f.contains("move_to-")) {
            f = f.replace("move_to-", "");
            menu.open(p, f);
            Utils.playSound(p, "onSwapGui");
            return;
        }

        switch (f) {
            case "offon_autosell_items":
                autoSellModify.offOnAutoSellItem(p, item.getType());;
                menu.open(p, currentMenu);
                return;
            case "sell_all":
                selling.sellItems(p, e.getInventory());
                menu.open(p, currentMenu);
                Utils.playSound(p, "onSellItems");
                return;
            case "buy_boost":
                boostsModify.buyBoost(p);
                menu.open(p, currentMenu);
                return;
            case "auto-sell":
                UUID uuid = p.getUniqueId();
                boolean bought = Database.isBoughtAutoSell(uuid);
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