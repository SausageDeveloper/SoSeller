package org.sausagedev.soseller.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.AutoSell;
import org.sausagedev.soseller.Configuration.Config;
import org.sausagedev.soseller.utils.Utils;

public class AutoSellCommand {

    public void execute(CommandSender sender, String[] args) {
        Player t = Bukkit.getPlayer(args[2]);
        if (t == null) {
            Utils.sendMSG(sender, Config.messages().nullPlayerError(), args[2]);
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

        String msg = Config.messages().autoSellRemove();
        if (playerData.isAutoSellBought()) {
            msg = Config.messages().autoSellGive();
        }
        AutoSell.disable(t.getUniqueId());

        msg = msg.replace("{player}", t.getName());
        if (sender instanceof Player) msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, msg);
        sender.sendMessage(Utils.convert(msg));
    }
}
