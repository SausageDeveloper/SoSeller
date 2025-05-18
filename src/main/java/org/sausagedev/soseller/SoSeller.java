package org.sausagedev.soseller;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.sausagedev.soseller.bstats.Metrics;
import org.sausagedev.soseller.commands.*;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.database.Database;
import org.sausagedev.soseller.listeners.AutoSellListener;
import org.sausagedev.soseller.listeners.CheckUpdateListener;
import org.sausagedev.soseller.listeners.FuctionsListener;
import org.sausagedev.soseller.listeners.MenuListener;
import org.sausagedev.soseller.utils.AutoSell;
import org.sausagedev.soseller.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public final class SoSeller extends JavaPlugin {
    private Economy econ;
    private PlayerPointsAPI ppAPI;
    private static SoSeller plugin;
    private static boolean usePAPI = false;

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
        PluginManager plManager = getServer().getPluginManager();
        new Metrics(this, 25259);
        new Config();
        saveDatabase();
        DataManager.importData();
        checkForSupports(plManager);
        registerListeners(plManager);
        registerCommands();
        AutoSell.setListOfMaterials(new HashMap<>());
        checkUpdate();
    }

    @Override
    public void onDisable() {
        DataManager.exportData();
        Database.close();
    }

    void checkForSupports(PluginManager plManager) {
        if (plManager.isPluginEnabled("PlayerPoints")) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
            getLogger().info("PlayerPoints подключён");
        }
        if (plManager.isPluginEnabled("CoinsEngine")) {
            getLogger().info("CoinsEngine подключён");
        }
        if (plManager.getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
            usePAPI = true;
        }
    }

    void saveDatabase() {
        File database = new File(getDataFolder(), "database.db");
        if (!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Database.initialize(database);
    }

    private void registerCommands() {
        PluginCommand plCmd = getCommand("soseller");
        Commands commands = new Commands(new AutoSellCommand(), new BoostCommand(), new GlobalBoostCommand(), new ItemsCommand());
        plCmd.setExecutor(commands);
        plCmd.setTabCompleter(new TabCompleter());
    }

    private void registerListeners(PluginManager plManager) {
        plManager.registerEvents(new FuctionsListener(), this);
        plManager.registerEvents(new MenuListener(), this);
        plManager.registerEvents(new AutoSellListener(), this);
        plManager.registerEvents(new CheckUpdateListener(this), this);
    }

    private void checkUpdate() {
        if (!Config.settings().checkUpdate()) return;
        Utils.checkUpdates(this, version -> {
            if (getDescription().getVersion().equals(version)) {
                getLogger().info(Config.messages().lastVersion());
            } else {
                String msg = Config.messages().oldVersion();
                msg = msg.replace("{version}", version);
                getLogger().info(msg);
            }
        });
    }

    public void checkUpdate(Player p) {
        if (!Config.settings().checkUpdate()) return;
        Utils.checkUpdates(this, version -> {
            if (getDescription().getVersion().equals(version)) {
                p.sendMessage(Config.messages().lastVersion());
            } else {
                String msg = Config.messages().oldVersion();
                msg = msg.replace("{version}", version);
                p.sendMessage(msg);
            }
        });
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

    public static SoSeller getPlugin() {
        return plugin;
    }

    public static boolean usePAPI() {
        return usePAPI;
    }
}