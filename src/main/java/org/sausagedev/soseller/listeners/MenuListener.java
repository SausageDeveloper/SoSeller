package org.sausagedev.soseller.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.sausagedev.soseller.utils.*;

import java.util.Arrays;
import java.util.List;

public class MenuListener implements Listener {

    @EventHandler
    void onClickEvent(InventoryClickEvent e) {
        Player p = Bukkit.getPlayer(e.getWhoClicked().getName());
        if (p == null) return;
        List<Integer> slots = SellingFields.getSlots(MenuDetect.getMenu(e.getWhoClicked().getUniqueId()));
        Inventory topInv = p.getOpenInventory().getTopInventory();
        Inventory clickedInv = e.getClickedInventory();
        if (e.getClick().equals(ClickType.SHIFT_LEFT)
                && !slots.contains(topInv.firstEmpty())
                && !clickedInv.equals(topInv)
                && !Utils.isDefaultInv(clickedInv)) e.setCancelled(true);
        else if (Utils.isDefaultInv(e.getClickedInventory())
                || slots.contains(e.getSlot())) return;
        e.setCancelled(true);
    }

    @EventHandler
    void onDragEvent(InventoryDragEvent e) {
        if (Utils.isDefaultInv(e.getInventory())) return;
        List<Integer> slots = SellingFields.getSlots(MenuDetect.getMenu(e.getWhoClicked().getUniqueId()));
        for (Integer slot : e.getInventorySlots()) {
            if (!slots.contains(slot)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    void onSwapItems(PlayerSwapHandItemsEvent e) {
        if (Utils.isDefaultInv(e.getPlayer().getInventory())) return;
        e.setCancelled(true);
    }

    @EventHandler
    void onDropItem(PlayerDropItemEvent e) {
        if (Utils.isDefaultInv(e.getPlayer().getInventory())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();
        if (Utils.isDefaultInv(inv)) return;
        MenuDetect.remove(p);
        Arrays.asList(inv.getContents()).forEach(item -> {
            if (item == null || item.getType().equals(Material.AIR)) return;
            else if (new ItemBuilder(item).hasFunction()) return;
            Utils.getItem(p, item, item.getAmount());
        });
    }
}