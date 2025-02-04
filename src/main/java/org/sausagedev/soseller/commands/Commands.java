package org.sausagedev.soseller.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.gui.Menu;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;

import java.util.List;

public class Commands implements CommandExecutor {
    private final SoSeller main;

    public Commands(SoSeller main) {
        this.main = main;
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
                List<String> list = Config.getMessages().getStringList("help");
                sender.sendMessage(Utils.getStringByList(list));
                return true;
            case "reload":
                main.saveDefaultConfig();
                main.reloadConfig();
                String def = "&8 ┃&f Конфиг перезагружен";
                Utils.sendMSG(sender, "config_reload", def);
            case "admin":
                if (args.length < 2) return true;

                switch (args[1].toLowerCase()) {
                    case "globalboost":
                        if (args.length != 4) return true;
                        new GlobalBoostCommand(main).execute(sender, args);
                        return true;
                    case "boost":
                        if (args.length < 5) return true;
                        new BoostCommand().execute(sender, args);
                        return true;
                    case "items":
                        if (args.length != 5) return true;
                        new ItemsCommand().execute(sender, args);
                        return true;
                    case "autosell":
                        if (args.length != 4) return true;
                        new AutoSellCommand().execute(sender, args);
                }
        }
        return true;
    }
}