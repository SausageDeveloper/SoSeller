package org.sausagedev.soseller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.Functions;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.SellerUtils;

import java.util.Map;

public class AutoSellListener implements Listener {
    private final Functions functions;
    private final SellerUtils sellerUtils;
    private final SoSeller main;

    public AutoSellListener(Functions functions, SellerUtils sellerUtils, SoSeller main) {
        this.functions = functions;
        this.sellerUtils = sellerUtils;
        this.main = main;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (!sellerUtils.isEnabledAutoSell(p.getUniqueId())) return;
        ItemStack item = e.getItem().getItemStack();
        if (isDefault(item)) return;
        boolean withMsg = main.getConfig().getBoolean("auto-sell.message", false);
        functions.sellItem(e.getPlayer(), item, withMsg);
        e.setCancelled(true);
        e.getItem().remove();
        functions.playSound(p, "onAutoSellItems");
    }

    public boolean isDefault(ItemStack item) {
        Map<String, Object> priceList = main.getConfig().getConfigurationSection("sell_items").getValues(false);
        return !priceList.containsKey(item.getType().toString());
    }
}
