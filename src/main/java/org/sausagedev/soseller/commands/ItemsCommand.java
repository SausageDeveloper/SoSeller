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

public class ItemsCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            String def = "&8 ┃&f Игрок {object} не найден";
            Utils.sendMSG(sender, "null_player_error", def, args[2]);
            return;
        }
        UUID uuid = t.getUniqueId();
        if (Utils.isNotInt(args[4])) {
            String def = "&8 ┃&f Неверное число: {object}";
            Utils.sendMSG(sender, "number_format_error", def, args[4]);
            return;
        }
        int items = Database.getItems(uuid);
        int n = Integer.parseInt(args[4]);
        switch (args[3]) {
            case "set":
                Database.setItems(uuid, n);
                break;
            case "add":
                Database.setItems(uuid, items + n);
                break;
            case "take":
                if (items <= 0) return;
                Database.setItems(uuid, items - n);
                break;

        }
        items = Database.getItems(uuid);
        String def = "&8 ┃&f Установлено на &e{amount} &fпроданные предметы &e{player}";
        String msg = Config.getMessages().getString("items_modify", def);
        msg = msg.replace("{player}", t.getName());
        msg = msg.replace("{amount}", String.valueOf(items));
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
