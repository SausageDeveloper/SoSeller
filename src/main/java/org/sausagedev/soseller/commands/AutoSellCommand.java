package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.AutoSell;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;

import java.util.UUID;

public class AutoSellCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            String def = "&8 ┃&f Игрок {object} не найден";
            Utils.sendMSG(sender, "null_player_error", def, args[2]);
            return;
        }
        DataManager.PlayerData playerData = DataManager.search(t.getUniqueId()), old = playerData.clone();
        switch (args[3]) {
            case "give":
                playerData.setAutoSellBought(true);
                break;
            case "remove":
                playerData.setAutoSellBought(false);
                break;
        }
        DataManager.replace(old, playerData);
        FileConfiguration messages = Config.getMessages();

        String def = "&8 ┃&f Убран доступ к авто-продаже предметов для &e{player}";
        String msg = messages.getString("autosell_remove", def);
        if (playerData.isAutoSellBought()) {
            def = "&8 ┃&f Выдан доступ к авто-продаже предметов для &e{player}";
            msg = messages.getString("autosell_give", def);
        }
        AutoSell.disable(t.getUniqueId());

        msg = msg.replace("{player}", t.getName());
        msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
