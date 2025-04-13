package org.sausagedev.soseller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.functions.Selling;
import org.sausagedev.soseller.utils.AutoSell;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.utils.Utils;

import java.util.Map;
import java.util.UUID;

public class AutoSellListener implements Listener {
    private final Selling selling = new Selling();

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (!AutoSell.isEnabled(uuid)) return;
        ItemStack item = e.getItem().getItemStack();
        boolean itemEnabled = AutoSell.isEnabled(uuid, item.getType());
        if (isDefault(item) || !itemEnabled) return;
        boolean withMsg = (boolean) Config.settings().autoSell().get("message");
        selling.sellItem(p, item, withMsg);
        e.setCancelled(true);
        e.getItem().remove();
        Utils.playSound(p, "onAutoSellItems");
    }

    public boolean isDefault(ItemStack item) {
        Map<String, Object> priceList = Config.settings().sellItems();
        return !priceList.containsKey(item.getType().toString());
    }
}
