package org.sausagedev.soseller;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Database;
import org.sausagedev.soseller.utils.Utils;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.text.DecimalFormat;
import java.util.*;

public class Functions {
    private final SoSeller main;
    private final Database database;

    public Functions(SoSeller main, Database database) {
        this.main = main;
        this.database = database;
    }

    public void sellItems(Player p, Inventory inv) {
        UUID uuid = p.getUniqueId();
        double boost = database.getBoost(uuid);
        double globalBoost = main.getConfig().getDouble("global_boost", 1);
        Map<String, Object> priceList = main.getConfig().getConfigurationSection("sell_items").getValues(false);

        int profit = 0;
        int items = 0;

        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) continue;
            NBTItem nbt = new NBTItem(item);
            if (nbt.hasTag("SoSeller")) continue;
            String key = item.getType().toString();
            if (!priceList.containsKey(key)) continue;
            int price = (int) priceList.get(key);
            int amount = item.getAmount();
            double money = price*amount*boost*globalBoost;
            main.getEconomy().depositPlayer(p.getName(), money);
            profit += (int) money;
            items += amount;
            item.setAmount(0);
        }

        if (profit == 0 || items == 0) return;

        database.setItems(uuid, database.getItems(uuid) + items);

        String def = "&8 ┃&f Вы продали &e{amount} &fпредметов за &a{profit} &fмонет";
        String msg = Config.getMessages().getString("sold", def);
        msg = msg.replace("{amount}", String.valueOf(items));
        msg = msg.replace("{profit}", String.valueOf(profit));
        p.sendMessage(Utils.convert(msg));
    }

    public void sellItem(Player p, ItemStack item, boolean withMessage) {
        UUID uuid = p.getUniqueId();
        double boost = database.getBoost(uuid);
        double globalBoost = main.getConfig().getDouble("global_boost", 1);
        Map<String, Object> priceList = main.getConfig().getConfigurationSection("sell_items").getValues(false);

        int profit = 0;

        if (item == null || item.getType().equals(Material.AIR)) return;
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasTag("SoSeller")) return;
        String key = item.getType().toString();
        if (!priceList.containsKey(key)) return;
        int price = (int) priceList.get(key);
        int amount = item.getAmount();
        double money = price*amount*boost*globalBoost;
        main.getEconomy().depositPlayer(p.getName(), money);
        profit += (int) money;

        database.setItems(uuid, database.getItems(uuid) + item.getAmount());
        item.setAmount(0);


        if (!withMessage) return;
        String def = "&8 ┃&f Вы продали &e{amount} &fпредметов за &a{profit} &fмонет";
        String msg = Config.getMessages().getString("sold", def);
        msg = msg.replace("{amount}", String.valueOf(amount));
        msg = msg.replace("{profit}", String.valueOf(profit));
        p.sendMessage(Utils.convert(msg));
    }

    public void buyBoost(Player p) {
        UUID uuid = p.getUniqueId();
        FileConfiguration config = main.getConfig();
        int balance = 0;
        int price = 0;
        double boost = database.getBoost(uuid);
        Map<String, Object> boosts = config.getConfigurationSection("boosts").getValues(false);
        String vault = null;
        for (String key : boosts.keySet()) {
            if (key.equalsIgnoreCase("message")) continue;
            Map<String, Object> boostParams = config.getConfigurationSection("boosts." + key).getValues(false);
            if (boost >= Integer.parseInt(key)) continue;
            price = (int) boostParams.get("price");
            vault = (String) boostParams.get("value");
            break;
        }

        if (price == 0) {
            String def = "&8 ┃&f Вы достигли последнего буста &7x{object}";
            String msg = Config.getMessages().getString("max_boost_error", def);
            msg = msg.replace("{object}", String.valueOf(boost));
            p.sendMessage(Utils.convert(msg));
            return;
        }

        if (vault == null) {
            String def = "&8 ┃&f Валюта не &cсуществует &fили не &cуказана";
            String msg = Config.getMessages().getString("vault_error", def);
            p.sendMessage(Utils.convert(msg));
            return;
        }
        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                String def = "&8 ┃&f Валюта не &cсуществует &fили не &cуказана";
                String msg = Config.getMessages().getString("vault_error", def);
                p.sendMessage(Utils.convert(msg));
                return;
            }
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }
        if (vault.equalsIgnoreCase("playerpoints")) balance = main.getPP().look(uuid);
        if (vault.equalsIgnoreCase("vault")) balance = (int) main.getEconomy().getBalance(p);
        if (vault.equalsIgnoreCase("items")) balance = database.getItems(uuid);

        if (balance < price) {
            String def = "&8 ┃&f У вас недостаточно рублей &7{object}/{price}";
            String msg = Config.getMessages().getString("balance_error", def);
            msg = msg.replace("{object}", String.valueOf(balance));
            msg = msg.replace("{price}", String.valueOf(price));
            p.sendMessage(Utils.convert(msg));
            playSound(p, "onNotEnoughVault");
            return;
        }

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                String def = "&8 ┃&f Валюта не &cсуществует &fили не &cуказана";
                String msg = Config.getMessages().getString("vault_error", def);
                p.sendMessage(Utils.convert(msg));
                return;
            }
            CoinsEngineAPI.removeBalance(p, currency, price);
        }
        if (vault.equalsIgnoreCase("playerpoints")) main.getPP().take(uuid, price);
        if (vault.equalsIgnoreCase("vault")) main.getEconomy().withdrawPlayer(p, price);
        if (vault.equalsIgnoreCase("items")) database.setItems(uuid, balance - price);
        database.setBoost(uuid, boost + 0.1);

        String def = "&8 ┃&f Вы купили буст &3x{boost}";
        String msg = Config.getMessages().getString("buy_boost", def);
        DecimalFormat df = new DecimalFormat("#.0");
        String res = df.format(boost + 0.1);
        msg = msg.replace("{boost}", res);
        p.sendMessage(Utils.convert(msg));
        playSound(p, "onBuyAnything");
    }
    public void buyAutoSell(Player p) {
        UUID uuid = p.getUniqueId();
        FileConfiguration config = main.getConfig();
        String vault = config.getString("boosts.value", "vault");
        boolean allInclude = config.getBoolean("all_items_include");
        int balance = 0;

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                p.sendMessage(Utils.convert("&8 ┃&f Валюта &c" + id + " &fне существует"));
                return;
            }
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }
        if (vault.equalsIgnoreCase("playerpoints")) balance = main.getPP().look(uuid);
        if (vault.equalsIgnoreCase("vault")) balance = (int) main.getEconomy().getBalance(p);
        if (vault.equalsIgnoreCase("items")) balance = database.getItems(uuid);

        int price = config.getInt("auto-sell.cost", 0);
        if (balance < price) {
            String def = "&8 ┃&f У вас недостаточно рублей &7{object}/{price}";
            String msg = Config.getMessages().getString("balance_error", def);
            msg = msg.replace("{object}", String.valueOf(balance));
            msg = msg.replace("{price}", String.valueOf(price));
            p.sendMessage(Utils.convert(msg));
            playSound(p, "onNotEnoughVault");
            return;
        }

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                p.sendMessage(Utils.convert("&8 ┃&f Валюта &c" + id + " &fне существует"));
                return;
            }
            CoinsEngineAPI.removeBalance(p, currency, price);
        }
        if (vault.equalsIgnoreCase("playerpoints")) main.getPP().take(uuid, price);
        if (vault.equalsIgnoreCase("vault")) main.getEconomy().withdrawPlayer(p, price);
        if (vault.equalsIgnoreCase("items")) database.setItems(uuid, balance - price);

        database.setAutoSellBought(uuid, true);
        database.setAutoSellEnabled(uuid, false);

        if (allInclude) {
            Map<String, Object> items = config.getConfigurationSection("sell_items").getValues(false);
            items.keySet().forEach(key -> {
                database.setAutoSellItem(uuid, key);
            });
        }

        String def = "&8 ┃&f Вы купили доступ к авто-продаже предметов";
        String msg = Config.getMessages().getString("messages.buy_autosell", def);
        p.sendMessage(Utils.convert(msg));
        playSound(p, "onNotEnoughVault");
    }

    public void offOnAutoSellItem(Player p, String material) {
        UUID uuid = p.getUniqueId();
        if (!database.isBoughtAutoSell(uuid)) return;
        boolean itemEnabled = database.isAutoSellItem(uuid, material);
        if (itemEnabled) {
            database.removeAutoSellItem(uuid, material);
            return;
        }
        database.setAutoSellItem(uuid, material);
    }

    public void playSound(Player p, String path) {
        String value = main.getConfig().getString("sounds." + path, "none");
        List<String> params = Arrays.asList(value.split(";"));
        if (value.equalsIgnoreCase("none")) return;
        Sound sound = Sound.valueOf(params.get(0));
        float pitch = Float.parseFloat(params.get(1)) == 0 ? Float.parseFloat(params.get(1)) : 1;
        float volume = Float.parseFloat(params.get(2)) == 0 ? Float.parseFloat(params.get(2)) : 1;

        try {
            p.playSound(p.getLocation(), sound, pitch, volume);
        } catch (IllegalArgumentException e) {
            main.getLogger().warning("Звук " + sound + " не существует в майнкрафте (Путь: " + "sounds." + path + ")");
        }
    }
}
