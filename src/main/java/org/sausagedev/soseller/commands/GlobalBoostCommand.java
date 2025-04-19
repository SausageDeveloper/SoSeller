package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.configuration.data.MessagesField;
import org.sausagedev.soseller.configuration.data.SettingsField;
import org.sausagedev.soseller.utils.Utils;

public class GlobalBoostCommand {

    public void execute(CommandSender sender, String[] args) {
        MessagesField messages = Config.messages();
        SettingsField settings = Config.settings();

        if (Utils.isNotDouble(args[3])) {
            Utils.sendMSG(sender, messages.numberFormatError(), args[3]);
            return;
        }
        double n = Double.parseDouble(args[3]);
        double globalBoost = settings.globalBoost();
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
        globalBoost = settings.globalBoost();
        String msg = messages.globalBoostModify();
        msg = msg.replace("{amount}", String.valueOf(globalBoost));
        if (sender instanceof Player && SoSeller.usePAPI()) msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
