package org.sausagedev.soseller.listeners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        NBTCompound tag = new NBTItem(item);
        if (!tag.hasTag("SoSeller")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        ItemStack item = e.getOldCursor();
        NBTCompound tag = new NBTItem(item);
        if (!tag.hasTag("SoSeller")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSwapItemsEvent(PlayerSwapHandItemsEvent e) {
        ItemStack item = e.getMainHandItem();
        if (isDefault(item)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();
        if (!isSeller(inv)) return;
        Arrays.asList(inv.getContents()).forEach(item -> {
            if (item == null || item.getType().equals(Material.AIR)) return;
            NBTItem nbt = new NBTItem(item);
            if (nbt.hasTag("SoSeller")) return;
            getItem(p, item, item.getAmount());
        });
    }

    public boolean isSeller(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (isDefault(item)) continue;
            NBTItem nbt = new NBTItem(item);
            if (nbt.hasTag("SoSeller")) return true;
        }
        return false;
    }

    public boolean isDefault(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return true;
        NBTCompound tag = new NBTItem(item);
        return !tag.hasTag("SoSeller");
    }

    public void getItem(Player p, ItemStack item, int count) {
        Inventory inv = p.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack currentItem = inv.getItem(slot);
            if (currentItem == null || currentItem.getType().equals(Material.AIR)) {
                int stackSize = Math.min(count, item.getMaxStackSize());
                item.setAmount(stackSize);
                inv.setItem(slot, item);
                count -= stackSize;
                if (count == 0) return;
            } else if (currentItem.isSimilar(item)) {
                int currentAmount = currentItem.getAmount();
                int maxStackSize = item.getMaxStackSize();
                if (currentAmount < maxStackSize) {
                    int stackSize = Math.min(count, maxStackSize - currentAmount);
                    currentItem.setAmount(currentAmount + stackSize);
                    inv.setItem(slot, currentItem);
                    count -= stackSize;
                    if (count == 0) return;
                }
            }
        }
        while (count > 0) {
            int dropCount = Math.min(count, item.getMaxStackSize());
            ItemStack dropItem = item.clone();
            dropItem.setAmount(dropCount);
            p.getWorld().dropItem(p.getLocation(), dropItem);
            count -= dropCount;
        }
    }
}