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
import org.sausagedev.soseller.utils.SellerUtils;
import org.sausagedev.soseller.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SoSeller extends JavaPlugin {
    private Economy econ = null;
    private PlayerPointsAPI ppAPI;
    private Connection connection;
    private File database;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!setupEconomy() ) {
                        getLogger().severe("Плагин Vault не был найден! Скачайте его: https://www.spigotmc.org/resources/vault.34315/");
                        getServer().getPluginManager().disablePlugin(SoSeller.getPlugin(SoSeller.class));
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
        SellerUtils sellerUtils = new SellerUtils(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(sellerUtils, this).register();
        }
        save("gui/items.yml");
        save("gui/main.yml");
        database = new File(getDataFolder(), "database.db");
        if (!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Utils utils = new Utils(this);
        Functions functions = new Functions(this, utils, sellerUtils);
        getCommand("soseller").setExecutor(new Commands(this, utils, sellerUtils));
        getCommand("soseller").setTabCompleter(new TabCompleter());
        getServer().getPluginManager().registerEvents(new FuctionsListener(this, functions), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new AutoSellListener(functions, sellerUtils, this), this);
        saveDefaultConfig();
        createDataBase();
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
                    "autosell BOOLEAN," +
                    "autosell_enabled BOOLEAN)");
            statement.close();
        } catch (SQLException e) {
            getLogger().severe("SQLException error: " + e.getCause());
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
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
}
