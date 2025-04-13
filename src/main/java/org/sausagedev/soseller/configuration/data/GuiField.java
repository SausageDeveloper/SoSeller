package org.sausagedev.soseller.configuration.data;

import org.bukkit.configuration.ConfigurationSection;

public record GuiField(
        String title,
        int size,
        ConfigurationSection icons
) {
}
