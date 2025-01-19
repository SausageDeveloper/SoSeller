package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.gui.Menu;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Database;
import org.sausagedev.soseller.utils.Utils;

import java.util.List;
import java.util.UUID;

public class Commands implements CommandExecutor {
    private final SoSeller main;
    private final Database database;

    public Commands(SoSeller main, Database database) {
        this.main = main;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = Bukkit.getPlayer(sender.getName());
        if (args.length == 0 && p != null) {
            Menu menu = new Menu(main, database);
            menu.open(p, "main");
            return true;
        }
        if (!Utils.hasPerm(sender, "soseller.admin")) return true;
        if (args[0].equalsIgnoreCase("help")) {
            List<String> list = Config.getMessages().getStringList("help");
            sender.sendMessage(Utils.getStringByList(list));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            main.saveDefaultConfig();
            main.reloadConfig();
            String def = "&8 ┃&f Конфиг перезагружен";
            sendMSG(sender, "config_reload", def);
        }
        if (args[0].equalsIgnoreCase("admin")) {
            if (args.length < 2) return true;
            if (args[1].equalsIgnoreCase("globalboost")) {
                if (args.length != 4) return true;
                if (isNotDouble(args[3])) {
                    String nullNumber = "&8 ┃&f Неверное число: {object}";
                    sendMSG(sender, "number_format_error", nullNumber, args[3]);
                    return true;
                }
                double n = Double.parseDouble(args[3]);
                double globalBoost = main.getConfig().getDouble("global_boost", 1);
                switch (args[2]) {
                    case "set":
                        main.getConfig().set("global_boost", n);
                        break;
                    case "add":
                        main.getConfig().set("global_boost", globalBoost + n);
                        break;
                    case "take":
                        double res = globalBoost - n;
                        main.getConfig().set("global_boost", res < 1 ? 1 : res);
                        break;
                }
                main.saveConfig();
                main.reloadConfig();
                globalBoost = main.getConfig().getDouble("global_boost", 1);
                String setItems = "&8 ┃&f Установлен на &e{amount} &fглобальный буст";
                String msg = Config.getMessages().getString("global_boost_modify", setItems);
                msg = msg.replace("{amount}", String.valueOf(globalBoost));
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(Utils.convert(msg));
            }
            if (args[1].equalsIgnoreCase("boost")) {
                if (args.length != 5) return true;
                Player t = Bukkit.getPlayer(args[2]);
                if (t == null) {
                    String nullPlayer = "&8 ┃&f Игрок {object} не найден";
                    sendMSG(sender, "null_player_error", nullPlayer, args[2]);
                    return true;
                }
                UUID uuid = t.getUniqueId();
                if (isNotDouble(args[4])) {
                    String nullNumber = "&8 ┃&f Неверное число: {object}";
                    sendMSG(sender, "number_format_error", nullNumber, args[4]);
                    return true;
                }
                double boost = database.getBoost(uuid);
                double n = Double.parseDouble(args[4]);
                switch (args[3]) {
                    case "set":
                        database.setBoost(uuid, n);
                        break;
                    case "add":
                        database.setBoost(uuid, boost + n);
                        break;
                    case "take":
                        double res = boost - n;
                        database.setBoost(uuid, res < 1 ? 1 : res);
                        break;
                }
                boost = database.getBoost(uuid);
                String setItems = "&8 ┃&f Установлен на &e{amount} &fбуст &e{player}";
                String msg = Config.getMessages().getString("boost_modify", setItems);
                msg = msg.replace("{player}", t.getName());
                msg = msg.replace("{amount}", String.valueOf(boost));
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(Utils.convert(msg));
                return true;
            }
            if (args[1].equalsIgnoreCase("items")) {
                if (args.length != 5) return true;
                Player t = Bukkit.getPlayer(args[2]);
                if (t == null) {
                    String nullPlayer = "&8 ┃&f Игрок {object} не найден";
                    sendMSG(sender, "null_player_error", nullPlayer, args[2]);
                    return true;
                }
                UUID uuid = t.getUniqueId();
                if (isNotInt(args[4])) {
                    String nullNumber = "&8 ┃&f Неверное число: {object}";
                    sendMSG(sender, "number_format_error", nullNumber, args[4]);
                    return true;
                }
                int items = database.getItems(uuid);
                int n = Integer.parseInt(args[4]);
                switch (args[3]) {
                    case "set":
                        database.setItems(uuid, n);
                        break;
                    case "add":
                        database.setItems(uuid, items + n);
                        break;
                    case "take":
                        if (items <= 0) return true;
                        database.setItems(uuid, items - n);
                        break;

                }
                items = database.getItems(uuid);
                String setItems = "&8 ┃&f Установлено на &e{amount} &fпроданные предметы &e{player}";
                String msg = Config.getMessages().getString("items_modify", setItems);
                msg = msg.replace("{player}", t.getName());
                msg = msg.replace("{amount}", String.valueOf(items));
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(Utils.convert(msg));
                return true;
            }
            if (args[1].equalsIgnoreCase("auto-sell")) {
                if (args.length != 4) return true;
                Player t = Bukkit.getPlayer(args[2]);
                if (t == null) {
                    String nullPlayer = "&8 ┃&f Игрок {object} не найден";
                    sendMSG(sender, "null_player_error", nullPlayer, args[2]);
                    return true;
                }
                UUID uuid = t.getUniqueId();
                switch (args[3]) {
                    case "give":
                        database.setAutoSellBought(uuid, true);
                        break;
                    case "remove":
                        database.setAutoSellBought(uuid, false);
                        break;
                }
                String def = "&8 ┃&f Убран доступ к авто-продаже предметов для &e{player}";
                String msg = Config.getMessages().getString("autosell_remove", def);
                if (database.isBoughtAutoSell(uuid)) {
                    def = "&8 ┃&f Выдан доступ к авто-продаже предметов для &e{player}";
                    msg = Config.getMessages().getString("autosell_give", def);
                }
                database.setAutoSellEnabled(uuid, false);

                msg = msg.replace("{player}", t.getName());
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(Utils.convert(msg));
                return true;
            }
        }
        return true;
    }
    public void sendMSG(CommandSender p, String path, String def, String arg) {
        String msg = Config.getMessages().getString(path, def);
        msg = msg.replace("{object}", arg);
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) p, msg);
        p.sendMessage(Utils.convert(msg));
    }

    public void sendMSG(CommandSender p, String path, String def) {
        String msg = Config.getMessages().getString(path, def);
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) p, msg);
        p.sendMessage(Utils.convert(msg));
    }

    public boolean isNotInt(Object o) {
        try {
            Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
    public boolean isNotDouble(Object o) {
        try {
            Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}