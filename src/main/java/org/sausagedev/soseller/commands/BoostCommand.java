package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;

public class BoostCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            String def = "&8 ┃&f Игрок {object} не найден";
            Utils.sendMSG(sender, "null_player_error", def, args[2]);
            return;
        }
        if (Utils.isNotDouble(args[4])) {
            String def = "&8 ┃&f Неверное число: {object}";
            Utils.sendMSG(sender, "number_format_error", def, args[4]);
            return;
        }
        double n = Double.parseDouble(args[4]);
        DataManager.PlayerData playerData = DataManager.search(t.getUniqueId()), old = playerData.clone();
        switch (args[3]) {
            case "set":
                playerData.setBoost(n);
                break;
            case "add":
                playerData.addBoost(n);
                break;
            case "take":
                playerData.takeBoost(n);
                break;
        }
        DataManager.replace(old, playerData);
        double boost = playerData.getBoost();
        String def = "&8 ┃&f Установлен на &e{amount} &fбуст &e{player}";
        String msg = Config.getMessages().getString("boost_modify", def);
        msg = msg.replace("{player}", t.getName());
        msg = msg.replace("{amount}", String.valueOf(boost));
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
