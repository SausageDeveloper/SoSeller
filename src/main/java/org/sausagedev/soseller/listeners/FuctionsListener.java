package org.sausagedev.soseller.listeners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.Functions;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.gui.Menu;
import org.sausagedev.soseller.utils.SellerUtils;

import java.util.UUID;

public class FuctionsListener implements Listener {
    private final SoSeller main;
    private final Functions functions;

    public FuctionsListener(SoSeller main, Functions functions) {
        this.main = main;
        this.functions = functions;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player p = Bukkit.getPlayer(e.getWhoClicked().getName());
        if (p == null) return;
        if (item == null || item.getType() == Material.AIR) return;
        NBTCompound tag = new NBTItem(item);
        if (!tag.hasTag("SoSeller")) return;
        SellerUtils sellerUtils = new SellerUtils(main);

        String f = tag.getString("SoSeller").toLowerCase();
        Menu menu = new Menu(main, sellerUtils);
        if (f.equals("sell_all")) {
            functions.sellItems(p, e.getInventory());
            menu.open(p, "main");
            functions.playSound(p, "onSellItems");
            return;
        }
        if (f.equals("buy_boost")) {
            functions.buyBoost(p);
            menu.open(p, "main");
            return;
        }
        if (f.equals("auto-sell")) {
            UUID uuid = p.getUniqueId();
            boolean bought = sellerUtils.isBoughtAutoSell(uuid);
            if (bought) {
                boolean isEnabled = sellerUtils.isEnabledAutoSell(uuid);
                functions.playSound(p, "onSwapAutoSell");
                if (isEnabled) {
                    sellerUtils.setAutoSellEnabled(uuid, false);
                    menu.open(p, "main");
                    return;
                }
                sellerUtils.setAutoSellEnabled(uuid, true);
                menu.open(p, "main");
                return;
            }
            functions.buyAutoSell(p);
            menu.open(p, "main");
        }
        if (f.contains("move_to-")) {
            f = f.replace("move_to-", "");
            menu.open(p, f);
            functions.playSound(p, "onSwapGui");
        }
    }
}