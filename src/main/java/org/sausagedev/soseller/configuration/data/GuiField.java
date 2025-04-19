package org.sausagedev.soseller.configuration.data;

import org.bukkit.configuration.ConfigurationSection;

public class GuiField {
    private final String title;
    private final int size;
    private final ConfigurationSection icons;

    public GuiField(String title, int size, ConfigurationSection icons) {
        this.title = title;
        this.size = size;
        this.icons = icons;
    }

    public String title() {
        return title;
    }

    public int size() {
        return size;
    }

    public ConfigurationSection icons() {
        return icons;
    }
}
