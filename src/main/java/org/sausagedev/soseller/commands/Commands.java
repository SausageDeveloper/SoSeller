package org.sausagedev.soseller.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sausagedev.soseller.gui.Menu;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.utils.Utils;

import java.util.List;

public class Commands implements CommandExecutor {
    private final AutoSellCommand autoSellCommand;
    private final BoostCommand boostCommand;
    private final GlobalBoostCommand globalBoostCommand;
    private final ItemsCommand itemsCommand;


    public Commands(AutoSellCommand autoSellCommand, BoostCommand boostCommand, GlobalBoostCommand globalBoostCommand, ItemsCommand itemsCommand) {
        this.autoSellCommand = autoSellCommand;
        this.boostCommand = boostCommand;
        this.globalBoostCommand = globalBoostCommand;
        this.itemsCommand = itemsCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = Bukkit.getPlayer(sender.getName());
        if (args.length == 0 && p != null) {
            Menu menu = new Menu();
            menu.open(p, "main");
            return true;
        }
        if (!Utils.hasPerm(sender, "soseller.admin")) return true;
        switch (args[0].toLowerCase()) {
            case "help":
                List<String> list = Config.messages().help();
                sender.sendMessage(Utils.getStringByList(list));
                return true;
            case "reload":
                new Config();
                sender.sendMessage(Utils.convert(Config.messages().configReload()));
            case "admin":
                if (args.length < 2) return true;
                switch (args[1].toLowerCase()) {
                    case "globalboost":
                        if (args.length != 4) return true;
                        globalBoostCommand.execute(sender, args);
                        return true;
                    case "boost":
                        if (args.length < 5) return true;
                        boostCommand.execute(sender, args);
                        return true;
                    case "items":
                        if (args.length != 5) return true;
                        itemsCommand.execute(sender, args);
                        return true;
                    case "autosell":
                        if (args.length != 4) return true;
                        autoSellCommand.execute(sender, args);
                }
        }
        return true;
    }
}