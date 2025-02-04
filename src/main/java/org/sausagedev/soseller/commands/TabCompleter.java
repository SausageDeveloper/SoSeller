package org.sausagedev.soseller.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("soseller")) return null;
        Player p = Bukkit.getPlayer(sender.getName());
        if (p == null || !p.hasPermission("soseller.admin")) return null;
        int length = args.length;
        if (length == 1) return Arrays.asList("help", "admin", "reload");
        String arg2 = args[1].toLowerCase();

        if (!args[0].equalsIgnoreCase("admin")) return null;
        if (length == 2) return Arrays.asList("boost", "globalboost", "items", "autosell");
        switch (arg2) {
            case "boost":
                if (length == 3) return null;
                else if (length == 4) return Arrays.asList("set", "add", "take");
                else if (length == 5) return Arrays.asList("0.5", "1.0", "2.0");
                else return Collections.emptyList();
            case "globalboost":
                if (length == 3) return Arrays.asList("set", "add", "take");
                else if (length == 4) return Arrays.asList("0.5", "1.0", "2.0");
                else return Collections.emptyList();
            case "items":
                if (length == 3) return null;
                else if (length == 4) return Arrays.asList("set", "add", "take");
                else if (length == 5) return Arrays.asList("10", "25", "50");
                else return Collections.emptyList();
            case "autosell":
                if (length == 3) return null;
                else if (length == 4) return Arrays.asList("give", "remove");
                else return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
