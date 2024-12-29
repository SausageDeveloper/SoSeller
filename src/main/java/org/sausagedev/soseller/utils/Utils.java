package org.sausagedev.soseller.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private final SoSeller main;
    private final Pattern HEX_PATTERN = Pattern.compile("(?i)&(#\\w{6})");

    public Utils(SoSeller main) {
        this.main = main;
    }

    public String getStringByList(List<String> list){
        StringBuilder stb = new StringBuilder();
        for(String key : list){
            stb.append(key);
            stb.append("\n");
        }
        return convert(stb.toString());
    }
    public String convert(String msg) {

        msg = ChatColor.translateAlternateColorCodes('&', msg);
        Matcher matcher = HEX_PATTERN.matcher(msg);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, hexToChatColor(hex));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }
    private String hexToChatColor(String hex) {
        StringBuilder builder = new StringBuilder("§x");
        for (char c : hex.substring(1).toCharArray()) {
            builder.append('§').append(c);
        }
        return builder.toString();
    }
    public boolean hasPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission(perm)) {
            String def = "&cУ вас недостаточно прав";
            String msg = main.getConfig().getString("messages.have_no_perms", def);
            sender.sendMessage(convert(msg));
            return false;
        }
        return true;
    }
}
