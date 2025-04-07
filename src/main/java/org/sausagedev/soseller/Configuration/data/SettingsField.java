package org.sausagedev.soseller.Configuration.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public record SettingsField(
        String lang,
        boolean checkUpdate,
        double globalBoost,
        ConfigurationSection boosts,
        List<Map<?,?>> sellingFields,
        Map<String, Object> sounds,
        Map<String, Object> sellItems,
        Map<String, Object> autoSell
) {
}
