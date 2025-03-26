package org.sausagedev.soseller.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SellingFields {
    static FileConfiguration settings = Config.getSettings();
    static final List<Map<?, ?>> fields = settings.getMapList("selling_fields");

    public static List<Integer> getSlots(String menu) {
        for (Map<?, ?> f : fields) {
            String fieldMenu = String.valueOf(f.get("gui"));
            if (!fieldMenu.equals(menu)) continue;
            return (List<Integer>) f.get("slots");
        }
        return Collections.emptyList();
    }
}
