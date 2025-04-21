package org.sausagedev.soseller.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.configuration.data.GuiField;
import org.sausagedev.soseller.configuration.data.MessagesField;
import org.sausagedev.soseller.configuration.data.SettingsField;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private final SoSeller main = SoSeller.getPlugin();

    public Config() {
        setupSettings(loadConfig("config.yml"));
        setupMessages(loadConfig("language/" + settings().lang() + ".yml"));
        loadConfig("gui/main.yml");
        loadConfig("gui/items.yml");

        String path = main.getDataFolder() + "/gui";
        File guis = new File(path);
        File[] files = guis.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            setupMenu(name, loadConfig("gui/" + name + ".yml"));
        }
    }

    private FileConfiguration loadConfig(String path) {
        File file = new File(main.getDataFolder(), path);
        if (!file.exists()) {
            main.saveResource(path, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void save(FileConfiguration config, String path) {
        File file = new File("plugins/SoSeller/", path);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static MessagesField messages;

    private void setupMessages(FileConfiguration config) {
        messages = new MessagesField(
                config.getStringList("help"),
                config.getConfigurationSection("materials").getValues(false),
                config.getString("config_reload"),
                config.getString("old_version"),
                config.getString("last_version"),
                config.getString("have_no_perms"),
                config.getString("number_format_error"),
                config.getString("null_player_error"),
                config.getString("balance_error"),
                config.getString("max_boost_error"),
                config.getString("vault_error"),
                config.getString("buy_boost"),
                config.getString("buy_autosell"),
                config.getString("boost_modify"),
                config.getString("global_boost_modify"),
                config.getString("items_modify"),
                config.getString("sold"),
                config.getString("auto-sell_remove"),
                config.getString("auto-sell_give"),
                config.getString("allow-autosell"),
                config.getString("deny-autosell"),
                config.getString("gui_max_boost"));
    }

    public static MessagesField messages() {
        return messages;
    }

    private static SettingsField settingsField;

    private static FileConfiguration settingsFile;

    private static void setupSettings(FileConfiguration config) {
        settingsFile = config;
        settingsField = new SettingsField(config.getString("lang", "ru"),
                config.getBoolean("check_update", true),
                config.getDouble("global_boost", 1),
                config.getConfigurationSection("boosts"),
                config.getMapList("selling_fields"),
                config.getConfigurationSection("sounds").getValues(false),
                config.getConfigurationSection("sell_items").getValues(false),
                config.getConfigurationSection("auto-sell").getValues(false));
    }

    public static void setGlobalBoost(double newGlobalBoost) {
        settingsFile.set("global_boost", newGlobalBoost);
        save(settingsFile, "config.yml");
        setupSettings(settingsFile);

    }

    public static SettingsField settings() {
        return settingsField;
    }

    private static final Map<String, GuiField> guisMap = new HashMap<>();

    private void setupMenu(String name, FileConfiguration config) {
        GuiField menu = new GuiField(config.getString("title"),
                config.getInt("size"),
                config.getConfigurationSection("icons"));
        guisMap.put(name, menu);
    }

    public static Map<String, GuiField> guis() {
        return guisMap;
    }
}
