package org.sausagedev.soseller.gui;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.*;

public class Menu {

    public void open(Player p, String menu) {
        p.openInventory(MenuUtils.generate(p, menu));
        new BukkitRunnable() {
            @Override
            public void run() {
                MenuDetect.setMenu(p.getUniqueId(), menu);
            }
        }.runTaskLater(SoSeller.getPlugin(), 1);
    }
}