package org.sausagedev.soseller.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.gui.CustomHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)&(#\\w{6})");
    private static final SoSeller main = SoSeller.getPlugin();

    public static String getStringByList(List<String> list) {
        StringBuilder stb = new StringBuilder();
        for(String key : list){
            stb.append(key);
            stb.append("\n");
        }
        return convert(stb.toString());
    }


    public static String convert(String msg) {

        msg = ChatColor.translateAlternateColorCodes('&', msg);
        Matcher matcher = HEX_PATTERN.matcher(msg);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, hexToChatColor(hex));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }


    private static String hexToChatColor(String hex) {
        StringBuilder builder = new StringBuilder("§x");
        for (char c : hex.substring(1).toCharArray()) {
            builder.append('§').append(c);
        }
        return builder.toString();
    }

    public static boolean hasPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(convert(Config.messages().haveNoPerms()));
            return false;
        }
        return true;
    }

    public static void checkUpdates(SoSeller plugin, Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new URL("https://raw.githubusercontent.com/SausageDeveloper/SoSeller/master/VERSION")
                            .openStream()))) {
                consumer.accept(reader.readLine().trim());
            } catch (IOException ex) {
                plugin.getLogger().warning("Не удалось проверить наличие обновлений: " + ex.getMessage());
            }
        });
    }

    public static void sendMSG(CommandSender p, String msg, String arg) {
        msg = msg.replace("{object}", arg);
        if (SoSeller.usePAPI()) msg = PlaceholderAPI.setPlaceholders((OfflinePlayer) p, msg);
        p.sendMessage(Utils.convert(msg));
    }

    public static boolean isNotInt(Object o) {
        try {
            Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
    public static boolean isNotDouble(Object o) {
        try {
            Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    public static void playSound(Player p, String path) {
        String value = Config.settings().sounds().get(path).toString();
        List<String> params = Arrays.asList(value.split(";"));
        if (value.equalsIgnoreCase("none")) return;
        Sound sound = Sound.valueOf(params.get(0));
        float pitch = Float.parseFloat(params.get(1)) == 0 ? Float.parseFloat(params.get(1)) : 1;
        float volume = Float.parseFloat(params.get(2)) == 0 ? Float.parseFloat(params.get(2)) : 1;

        try {
            p.playSound(p.getLocation(), sound, pitch, volume);
        } catch (IllegalArgumentException e) {
            String msg = "Звук " + sound + " не существует в майнкрафте (Путь: " + "sounds." + path + ")";
            main.getLogger().warning(msg);
        }
    }

    public static void getItem(Player p, ItemStack item, int count) {
        Inventory inv = p.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack currentItem = inv.getItem(slot);
            if (currentItem == null || currentItem.getType().equals(Material.AIR)) {
                int stackSize = Math.min(count, item.getMaxStackSize());
                item.setAmount(stackSize);
                inv.setItem(slot, item);
                count -= stackSize;
                if (count == 0) return;
            } else if (currentItem.isSimilar(item)) {
                int currentAmount = currentItem.getAmount();
                int maxStackSize = item.getMaxStackSize();
                if (currentAmount < maxStackSize) {
                    int stackSize = Math.min(count, maxStackSize - currentAmount);
                    currentItem.setAmount(currentAmount + stackSize);
                    inv.setItem(slot, currentItem);
                    count -= stackSize;
                    if (count == 0) return;
                }
            }
        }
        while (count > 0) {
            int dropCount = Math.min(count, item.getMaxStackSize());
            ItemStack dropItem = item.clone();
            dropItem.setAmount(dropCount);
            p.getWorld().dropItem(p.getLocation(), dropItem);
            count -= dropCount;
        }
    }

    public static boolean isDefaultInv(Inventory inv) {
        if (inv == null) return true;
        InventoryHolder holder = inv.getHolder();
        return (!(holder instanceof CustomHolder));
    }
}