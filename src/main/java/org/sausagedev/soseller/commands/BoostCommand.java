package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Database;
import org.sausagedev.soseller.utils.Utils;

import java.util.UUID;

public class BoostCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            String def = "&8 ┃&f Игрок {object} не найден";
            Utils.sendMSG(sender, "null_player_error", def, args[2]);
            return;
        }
        UUID uuid = t.getUniqueId();
        if (Utils.isNotDouble(args[4])) {
            String def = "&8 ┃&f Неверное число: {object}";
            Utils.sendMSG(sender, "number_format_error", def, args[4]);
            return;
        }
        double boost = Database.getBoost(uuid);
        double n = Double.parseDouble(args[4]);
        switch (args[3]) {
            case "set":
                Database.setBoost(uuid, n);
                break;
            case "add":
                Database.setBoost(uuid, boost + n);
                break;
            case "take":
                double res = boost - n;
                Database.setBoost(uuid, res < 1 ? 1 : res);
                break;
        }
        boost = Database.getBoost(uuid);
        String def = "&8 ┃&f Установлен на &e{amount} &fбуст &e{player}";
        String msg = Config.getMessages().getString("boost_modify", def);
        msg = msg.replace("{player}", t.getName());
        msg = msg.replace("{amount}", String.valueOf(boost));
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
