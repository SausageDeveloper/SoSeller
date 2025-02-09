package org.sausagedev.soseller;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.sausagedev.soseller.commands.Commands;
import org.sausagedev.soseller.commands.TabCompleter;
import org.sausagedev.soseller.listeners.AutoSellListener;
import org.sausagedev.soseller.listeners.FuctionsListener;
import org.sausagedev.soseller.listeners.MenuListener;
import org.sausagedev.soseller.utils.AutoSell;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public final class SoSeller extends JavaPlugin {
    private Economy econ = null;
    private PlayerPointsAPI ppAPI;
    private Connection connection;
    private File database;
    private static SoSeller plugin;

    @Override
    public void onEnable() {
        plugin = this;
        if (isNotSetEconomy()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (isNotSetEconomy()) {
                        getLogger().severe("Плагин Vault не был найден! Скачайте его: https://www.spigotmc.org/resources/vault.34315/");
                        getServer().getPluginManager().disablePlugin(SoSeller.getPlugin());
                        return;
                    }
                    enable();
                }
            }.runTaskLater(this, 100);
            return;
        }
        enable();
    }

    public void enable() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
            getLogger().info("PlayerPoints подключён");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CoinsEngine")) {
            getLogger().info("CoinsEngine подключён");
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
        }
        AutoSell.setListOfMaterials(new HashMap<>());
        save("gui/items.yml");
        save("gui/main.yml");
        save("language/en.yml");
        save("language/ru.yml");
        save("language/uk.yml");
        this.database = new File(getDataFolder(), "database.db");
        if (!this.database.exists()) {
            try {
                this.database.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        getCommand("soseller").setExecutor(new Commands(this));
        getCommand("soseller").setTabCompleter(new TabCompleter());
        getServer().getPluginManager().registerEvents(new FuctionsListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new AutoSellListener(), this);
        saveDefaultConfig();
        createDataBase();

        if (!getConfig().getBoolean("check_update")) return;
        Utils.checkUpdates(this, version -> {
            if (getDescription().getVersion().equals(version)) {
                String def = "Вы используете последнюю версию!";
                String msg = Config.getMessages().getString("last_version", def);
                getLogger().info(msg);
            } else {
                String def = "Вышла новая версия {version}! Ссылка: https://www.spigotmc.org/resources/soseller.121023/";
                String msg = Config.getMessages().getString("old_version", def);
                msg = msg.replace("{version}", version);
                getLogger().info(msg);
            }
        });
    }

    @Override
    public void onDisable() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) {
                getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(String path) {
        File file = new File(getDataFolder(), path);
        if (!file.exists()) {
            saveResource(path, false);
        }
    }

    public void createDataBase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            Statement statement = getConnection().createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS database (" +
                    "uuid TEXT, " +
                    "nick TEXT, " +
                    "items INTEGER, " +
                    "boost DOUBLE, " +
                    "autosell BOOLEAN)");
            statement.close();
        } catch (SQLException e) {
            getLogger().severe("SQLException error: " + e.getCause());
        }
    }

    private boolean isNotSetEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return true;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return true;
        }
        econ = rsp.getProvider();
        return false;
    }

    public Economy getEconomy() {
        return econ;
    }
    public PlayerPointsAPI getPP() {
        return ppAPI;
    }
    public Connection getConnection() {
        return connection;
    }

    public static SoSeller getPlugin() {
        return plugin;
    }
}