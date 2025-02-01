package org.sausagedev.soseller.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("soseller")) return null;
        Player p = Bukkit.getPlayer(sender.getName());
        if (p == null) return null;
        if (!p.hasPermission("soseller.admin")) return null;
        int length = args.length;
        if (length == 1) return Arrays.asList("help", "admin", "reload");
        String arg1 = args[1].toLowerCase();
        if (!args[0].equalsIgnoreCase("admin")) return null;
        if (length == 2) {
            return Arrays.asList("boost", "globalboost", "items", "auto-sell");
        }
        if (length == 3 && arg1.equals("globalboost")) {
            return Arrays.asList("set", "add", "take");
        }
        if (length == 4) {
            if (arg1.equals("autosell")) {
                return Arrays.asList("give", "remove");
            }
            if (arg1.equals("globalboost")) {
                return Arrays.asList("0.1", "0.5", "1.0");
            }
            return Arrays.asList("set", "add", "take");
        }
        if (length == 5) {
            if (arg1.equals("autosell")) return null;
            if (arg1.equals("globalboost")) return null;
            if (arg1.equals("boost")) {
                return Arrays.asList("0.1", "0.5", "1.0");
            }
            return Arrays.asList("1", "5", "10");
        }
        return null;
    }
}
