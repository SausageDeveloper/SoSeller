package org.sausagedev.soseller.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("soseller")) return null;
        Player p = Bukkit.getPlayer(sender.getName());
        if (p == null) return null;
        if (!p.hasPermission("soseller.admin")) return null;
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("help");
            list.add("admin");
            list.add("reload");
            return list;
        }
        if (!args[0].equalsIgnoreCase("admin")) return null;
        if (args.length == 2) {
            list.add("boost");
            list.add("items");
            list.add("autosell");
            return list;
        }
        if (args.length == 4) {
            if (args[1].equalsIgnoreCase("autosell")) {
                list.add("give");
                list.add("remove");
                return list;
            }
            list.add("set");
            list.add("add");
            list.add("take");
            return list;
        }
        if (args.length == 5) {
            if (args[1].equalsIgnoreCase("autosell")) return null;
            if (args[1].equalsIgnoreCase("boost")) {
                list.add("0.1");
                list.add("0.5");
                list.add("1.0");
                return list;
            }
            list.add("1");
            list.add("5");
            list.add("10");
            return list;
        }
        return null;
    }
}
