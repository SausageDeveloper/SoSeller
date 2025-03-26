package org.sausagedev.soseller.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomHolder implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 54, "sdhjk");
    }
}
