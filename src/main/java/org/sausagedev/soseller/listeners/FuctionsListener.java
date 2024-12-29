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
import org.sausagedev.soseller.gui.ItemsMenu;
import org.sausagedev.soseller.gui.MainMenu;
import org.sausagedev.soseller.utils.SellerUtils;
import org.sausagedev.soseller.utils.Utils;

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
        Utils utils = new Utils(main);
        SellerUtils sellerUtils = new SellerUtils(main);

        String f = tag.getString("SoSeller").toLowerCase();
        MainMenu mainMenu = new MainMenu(main, utils, sellerUtils);
        switch (f) {
            case "sell_all":
                functions.sellItems(p, e.getInventory());
                mainMenu.open(p);
                functions.playSound(p, "onSellItems");
                break;
            case "buy_boost":
                functions.buyBoost(p);
                mainMenu.open(p);
                break;
            case "autosell":
                UUID uuid = p.getUniqueId();
                boolean bought = sellerUtils.isBoughtAutoSell(uuid);
                if (bought) {
                    boolean isEnabled = sellerUtils.isEnabledAutoSell(uuid);
                    functions.playSound(p, "onSwapAutoSell");
                    if (isEnabled) {
                        sellerUtils.setAutoSellEnabled(uuid, false);
                        mainMenu.open(p);
                        return;
                    }
                    sellerUtils.setAutoSellEnabled(uuid, true);
                    mainMenu.open(p);
                    return;
                }
                functions.buyAutoSell(p);
                mainMenu.open(p);
                break;
            case "items":
                ItemsMenu itemsMenu = new ItemsMenu(main, utils, sellerUtils);
                itemsMenu.open(p);
                functions.playSound(p, "onSwapGui");
                break;
            case "seller": {
                mainMenu.open(p);
                functions.playSound(p, "onSwapGui");
                break;
            }
        }
    }
}
