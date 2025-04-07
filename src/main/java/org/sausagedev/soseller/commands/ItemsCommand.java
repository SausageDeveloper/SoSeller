package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.сonfiguration.Config;
import org.sausagedev.soseller.utils.Utils;

public class ItemsCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            Utils.sendMSG(sender, Config.messages().nullPlayerError(), args[2]);
            return;
        }
        if (Utils.isNotInt(args[4])) {
            Utils.sendMSG(sender, Config.messages().numberFormatError(), args[4]);
            return;
        }
        int n = Integer.parseInt(args[4]);
        DataManager.PlayerData playerData = DataManager.search(t.getUniqueId()), old = playerData.clone();
        int items = playerData.getItems();
        switch (args[3]) {
            case "set":
                playerData.setItems(n);
                break;
            case "add":
                playerData.addItems(n);
                break;
            case "take":
                if (items <= 0) return;
                playerData.takeItems(n);
                break;
        }

        DataManager.replace(old, playerData);
        items = playerData.getItems();
        String msg = Config.messages().itemsModify();
        msg = msg.replace("{player}", t.getName());
        msg = msg.replace("{amount}", String.valueOf(items));
        if (sender instanceof Player) msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
