package org.sausagedev.soseller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.sausagedev.soseller.SoSeller;

public class CheckUpdateListener implements Listener {
    private final SoSeller main;

    public CheckUpdateListener(SoSeller main) {
        this.main = main;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("soseller.admin")) return;
        main.checkUpdate(e.getPlayer());
    }
}
