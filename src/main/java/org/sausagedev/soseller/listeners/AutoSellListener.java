package org.sausagedev.soseller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.Functions;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.AutoSell;
import org.sausagedev.soseller.utils.Database;

import java.util.Map;
import java.util.UUID;

public class AutoSellListener implements Listener {
    private final Functions functions;
    private final SoSeller main;

    public AutoSellListener(Functions functions, SoSeller main) {
        this.functions = functions;
        this.main = main;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (!AutoSell.isEnabled(uuid)) return;
        ItemStack item = e.getItem().getItemStack();
        boolean itemEnabled = AutoSell.isEnabled(uuid, item.getType());
        if (isDefault(item) || !itemEnabled) return;
        boolean withMsg = main.getConfig().getBoolean("auto-sell.message", false);
        functions.sellItem(p, item, withMsg);
        e.setCancelled(true);
        e.getItem().remove();
        functions.playSound(p, "onAutoSellItems");
    }

    public boolean isDefault(ItemStack item) {
        Map<String, Object> priceList = main.getConfig().getConfigurationSection("sell_items").getValues(false);
        return !priceList.containsKey(item.getType().toString());
    }
}
