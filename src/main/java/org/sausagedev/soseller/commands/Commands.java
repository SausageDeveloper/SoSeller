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
import org.sausagedev.soseller.gui.MainMenu;
import org.sausagedev.soseller.utils.SellerUtils;
import org.sausagedev.soseller.utils.SkullCreator;
import org.sausagedev.soseller.utils.Utils;

import java.util.List;
import java.util.UUID;

public class Commands implements CommandExecutor {
    private final SoSeller main;
    private final Utils utils;
    private final SellerUtils sellerUtils;

    public Commands(SoSeller main, Utils utils, SellerUtils sellerUtils) {
        this.main = main;
        this.utils = utils;
        this.sellerUtils = sellerUtils;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = Bukkit.getPlayer(sender.getName());
        if (args.length == 0 && p != null) {
            MainMenu mainMenu = new MainMenu(main, utils, sellerUtils);
            mainMenu.open(p);
            return true;
        }
        if (!utils.hasPerm(sender, "soseller.admin")) return true;
        if (args[0].equalsIgnoreCase("help")) {
            List<String> list = main.getConfig().getStringList("messages.help");
            sender.sendMessage(utils.getStringByList(list));
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
                double boost = sellerUtils.getBoost(uuid);
                double n = Double.parseDouble(args[4]);
                switch (args[3]) {
                    case "set":
                        sellerUtils.setBoost(uuid, n);
                        break;
                    case "add":
                        sellerUtils.setBoost(uuid, boost + n);
                        break;
                    case "take":
                        if (boost <= 1) return true;
                        sellerUtils.setBoost(uuid, boost - n);
                        break;
                }
                boost = sellerUtils.getBoost(uuid);
                String setItems = "&8 ┃&f Установлен на &e!amount &fбуст &e!player";
                String msg = main.getConfig().getString("messages.boost_modify", setItems);
                msg = msg.replace("!player", t.getName());
                msg = msg.replace("!amount", String.valueOf(boost));
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(utils.convert(msg));
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
                int items = sellerUtils.getItems(uuid);
                int n = Integer.parseInt(args[4]);
                switch (args[3]) {
                    case "set":
                        sellerUtils.setItems(uuid, n);
                        break;
                    case "add":
                        sellerUtils.setItems(uuid, items + n);
                        break;
                    case "take":
                        if (items <= 0) return true;
                        sellerUtils.setItems(uuid, items - n);
                        break;

                }
                items = sellerUtils.getItems(uuid);
                String setItems = "&8 ┃&f Установлено на &e!amount &fпроданные предметы &e!player";
                String msg = main.getConfig().getString("messages.items_modify", setItems);
                msg = msg.replace("!player", t.getName());
                msg = msg.replace("!amount", String.valueOf(items));
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(utils.convert(msg));
                return true;
            }
            if (args[1].equalsIgnoreCase("autosell")) {
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
                        sellerUtils.setAutoSellBought(uuid, true);
                        break;
                    case "remove":
                        sellerUtils.setAutoSellBought(uuid, false);
                        break;
                }
                String def = "&8 ┃&f Убран доступ к авто-продаже предметов для &e!player";
                String msg = main.getConfig().getString("messages.autosell_remove", def);
                if (sellerUtils.isBoughtAutoSell(uuid)) {
                    def = "&8 ┃&f Выдан доступ к авто-продаже предметов для &e!player";
                    msg = main.getConfig().getString("messages.autosell_give", def);
                }
                sellerUtils.setAutoSellEnabled(uuid, false);

                msg = msg.replace("!player", t.getName());
                msg = PlaceholderAPI.setPlaceholders(p, msg);
                sender.sendMessage(utils.convert(msg));
                return true;
            }
        }
        return true;
    }
    public void sendMSG(CommandSender p, String path, String def, String arg) {
        String msg = main.getConfig().getString("messages." + path, def);
        msg = msg.replace("{object}", arg);
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) p, msg);
        p.sendMessage(utils.convert(msg));
    }

    public void sendMSG(CommandSender p, String path, String def) {
        String msg = main.getConfig().getString("messages." + path, def);
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) p, msg);
        p.sendMessage(utils.convert(msg));
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
