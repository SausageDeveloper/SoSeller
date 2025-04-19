package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.configuration.data.MessagesField;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.Utils;

public class BoostCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        MessagesField messages = Config.messages();
        if (t == null) {
            Utils.sendMSG(sender, messages.nullPlayerError(), args[2]);
            return;
        }
        if (Utils.isNotDouble(args[4])) {
            Utils.sendMSG(sender, messages.numberFormatError(), args[4]);
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
        String msg = messages.boostModify();
        msg = msg.replace("{player}", t.getName());
        msg = msg.replace("{amount}", String.valueOf(boost));
        if (sender instanceof Player && SoSeller.usePAPI()) msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
