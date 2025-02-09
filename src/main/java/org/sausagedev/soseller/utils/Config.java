package org.sausagedev.soseller.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;

import java.io.File;

public class Config {
    private static final SoSeller main = SoSeller.getPlugin();

    public static FileConfiguration getMessages() {
        String lang = main.getConfig().getString("lang", "ru");
        File file = new File(main.getDataFolder(), "language/" + lang + ".yml");
        if (!file.exists()) {
            main.saveResource("language/" + lang + ".yml", false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getMenu(String menu) {
        if (menu == null) menu = "main";
        File file = new File(main.getDataFolder(), "gui/" + menu + ".yml");
        if (!file.exists()) {
            main.saveResource("gui/" + menu + ".yml", false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getSettings() {
        return main.getConfig();
    }
}
