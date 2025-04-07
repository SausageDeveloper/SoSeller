package org.sausagedev.soseller.Configuration.data;

import org.bukkit.configuration.ConfigurationSection;

public record GuiField(
        String title,
        int size,
        ConfigurationSection icons
) {
}
