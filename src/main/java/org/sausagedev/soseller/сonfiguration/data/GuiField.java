package org.sausagedev.soseller.сonfiguration.data;

import org.bukkit.configuration.ConfigurationSection;

public record GuiField(
        String title,
        int size,
        ConfigurationSection icons
) {
}
