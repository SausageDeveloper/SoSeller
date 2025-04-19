package org.sausagedev.soseller.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuDetect {
    private static final Map<UUID, String> menu = new HashMap<>();

    public static void setMenu(UUID uuid, String menu) {
        MenuDetect.menu.put(uuid, menu);
    }

    public static String getMenu(UUID uuid) {
        return menu.get(uuid);
    }

    public static void remove(Player p) {
        menu.remove(p.getUniqueId());
    }
}
