package org.sausagedev.soseller.configuration.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public class SettingsField {
    private final String lang;
    private final boolean checkUpdate;
    private final double globalBoost;
    private final ConfigurationSection boosts;
    private final List<Map<?, ?>> sellingFields;
    private final Map<String, Object> sounds;
    private final Map<String, Object> sellItems;
    private final Map<String, Object> autoSell;

    public SettingsField(String lang, boolean checkUpdate, double globalBoost, ConfigurationSection boosts, List<Map<?, ?>> sellingFields, Map<String, Object> sounds, Map<String, Object> sellItems, Map<String, Object> autoSell) {
        this.lang = lang;
        this.checkUpdate = checkUpdate;
        this.globalBoost = globalBoost;
        this.boosts = boosts;
        this.sellingFields = sellingFields;
        this.sounds = sounds;
        this.sellItems = sellItems;
        this.autoSell = autoSell;
    }

    public String lang() {
        return lang;
    }

    public boolean checkUpdate() {
        return checkUpdate;
    }

    public double globalBoost() {
        return globalBoost;
    }

    public ConfigurationSection boosts() {
        return boosts;
    }

    public List<Map<?, ?>> sellingFields() {
        return sellingFields;
    }

    public Map<String, Object> sounds() {
        return sounds;
    }

    public Map<String, Object> sellItems() {
        return sellItems;
    }

    public Map<String, Object> autoSell() {
        return autoSell;
    }
}
