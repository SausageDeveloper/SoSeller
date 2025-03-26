package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;

public class ItemsCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            String def = "&8 ┃&f Игрок {object} не найден";
            Utils.sendMSG(sender, "null_player_error", def, args[2]);
            return;
        }
        if (Utils.isNotInt(args[4])) {
            String def = "&8 ┃&f Неверное число: {object}";
            Utils.sendMSG(sender, "number_format_error", def, args[4]);
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
        String def = "&8 ┃&f Установлено на &e{amount} &fпроданные предметы &e{player}";
        String msg = Config.getMessages().getString("items_modify", def);
        msg = msg.replace("{player}", t.getName());
        msg = msg.replace("{amount}", String.valueOf(items));
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
