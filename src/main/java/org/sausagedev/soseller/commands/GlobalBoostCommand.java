package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;

public class GlobalBoostCommand {
    private final SoSeller main;

    public GlobalBoostCommand(SoSeller main) {
        this.main = main;
    }

    public void execute(CommandSender sender, String[] args) {
        FileConfiguration config = Config.getSettings();
        if (Utils.isNotDouble(args[3])) {
            String def = "&8 ┃&f Неверное число: {object}";
            Utils.sendMSG(sender, "number_format_error", def, args[3]);
            return;
        }
        double n = Double.parseDouble(args[3]);
        double globalBoost = config.getDouble("global_boost", 1);
        switch (args[2]) {
            case "set":
                config.set("global_boost", n);
                break;
            case "add":
                config.set("global_boost", globalBoost + n);
                break;
            case "take":
                double res = globalBoost - n;
                config.set("global_boost", res < 1 ? 1 : res);
                break;
        }
        main.saveConfig();
        main.reloadConfig();
        globalBoost = config.getDouble("global_boost", 1);
        String def = "&8 ┃&f Установлен на &e{amount} &fглобальный буст";
        String msg = Config.getMessages().getString("global_boost_modify", def);
        msg = msg.replace("{amount}", String.valueOf(globalBoost));
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
