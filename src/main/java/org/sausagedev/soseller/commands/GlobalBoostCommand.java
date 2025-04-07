package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.сonfiguration.Config;
import org.sausagedev.soseller.utils.Utils;

public class GlobalBoostCommand {

    public void execute(CommandSender sender, String[] args) {
        if (Utils.isNotDouble(args[3])) {
            Utils.sendMSG(sender, Config.messages().numberFormatError(), args[3]);
            return;
        }
        double n = Double.parseDouble(args[3]);
        double globalBoost = Config.settings().globalBoost();
        switch (args[2]) {
            case "set":
                Config.setGlobalBoost(n);
                break;
            case "add":
                Config.setGlobalBoost(globalBoost + n);
                break;
            case "take":
                double res = globalBoost - n;
                Config.setGlobalBoost(res < 1 ? 1 : res);
                break;
        }
        globalBoost = Config.settings().globalBoost();
        String msg = Config.messages().globalBoostModify();
        msg = msg.replace("{amount}", String.valueOf(globalBoost));
        if (sender instanceof Player) msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
