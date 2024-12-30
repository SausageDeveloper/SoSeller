package org.sausagedev.soseller;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.sausagedev.soseller.utils.SellerUtils;
import org.sausagedev.soseller.utils.Utils;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public class Functions {
    private final SoSeller main;
    private final Utils utils;
    private final SellerUtils sellerUtils;

    public Functions(SoSeller main, Utils utils, SellerUtils sellerUtils) {
        this.main = main;
        this.utils = utils;
        this.sellerUtils = sellerUtils;
    }

    public void sellItems(Player p, Inventory inv) {
        UUID uuid = p.getUniqueId();
        double boost = sellerUtils.getBoost(uuid);
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
            double money = price*amount*boost;
            main.getEconomy().depositPlayer(p.getName(), money);
            profit += (int) money;
            items += amount;
            item.setAmount(0);
        }

        if (profit == 0 || items == 0) return;

        sellerUtils.setItems(uuid, sellerUtils.getItems(uuid) + items);

        String def = "&8 ┃&f Вы продали &e!amount &fпредметов за &a!profit &fмонет";
        String msg = main.getConfig().getString("messages.sold", def);
        msg = msg.replace("!amount", String.valueOf(items));
        msg = msg.replace("!profit", String.valueOf(profit));
        p.sendMessage(utils.convert(msg));
    }

    public void sellItem(Player p, ItemStack item, boolean withMessage) {
        UUID uuid = p.getUniqueId();
        double boost = sellerUtils.getBoost(uuid);
        Map<String, Object> priceList = main.getConfig().getConfigurationSection("sell_items").getValues(false);

        int profit = 0;

        if (item == null || item.getType().equals(Material.AIR)) return;
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasTag("SoSeller")) return;
        String key = item.getType().toString();
        if (!priceList.containsKey(key)) return;
        int price = (int) priceList.get(key);
        int amount = item.getAmount();
        double money = price*amount*boost;
        main.getEconomy().depositPlayer(p.getName(), money);
        profit += (int) money;

        sellerUtils.setItems(uuid, sellerUtils.getItems(uuid) + item.getAmount());
        item.setAmount(0);


        if (!withMessage) return;
        String def = "&8 ┃&f Вы продали &e!amount &fпредметов за &a!profit &fмонет";
        String msg = main.getConfig().getString("messages.sold", def);
        msg = msg.replace("!amount", String.valueOf(amount));
        msg = msg.replace("!profit", String.valueOf(profit));
        p.sendMessage(utils.convert(msg));
    }

    public void buyBoost(Player p) {
        UUID uuid = p.getUniqueId();
        String vault = main.getConfig().getString("boosts.value", "vault");
        int balance = 0;

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                p.sendMessage(utils.convert("&8 ┃&f Валюта &c" + id + " &fне существует"));
                return;
            }
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }
        if (vault.equalsIgnoreCase("playerpoints")) balance = main.getPP().look(uuid);
        if (vault.equalsIgnoreCase("vault")) balance = (int) main.getEconomy().getBalance(p);
        if (vault.equalsIgnoreCase("items")) balance = sellerUtils.getItems(uuid);

        double boost = sellerUtils.getBoost(uuid);
        int price = 0;
        Map<String, Object> boosts = main.getConfig().getConfigurationSection("boosts").getValues(false);
        for (String key : boosts.keySet()) {
            if (key.equalsIgnoreCase("value")) continue;
            if (key.equalsIgnoreCase("message")) continue;
            if (boost < Integer.parseInt(key)) {
                price = (int) boosts.get(key);
                break;
            }
        }
        if (price == 0) {
            String def = "&8 ┃&f Вы достигли последнего буста &7x{object}";
            String msg = main.getConfig().getString("messages.max_boost_error", def);
            msg = msg.replace("{object}", String.valueOf(boost));
            p.sendMessage(utils.convert(msg));
            return;
        }
        if (balance < price) {
            String def = "&8 ┃&f У вас недостаточно рублей &7{object}/{price}";
            String msg = main.getConfig().getString("messages.balance_error", def);
            msg = msg.replace("{object}", String.valueOf(balance));
            msg = msg.replace("{price}", String.valueOf(price));
            p.sendMessage(utils.convert(msg));
            playSound(p, "onNotEnoughVault");
            return;
        }

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                p.sendMessage(utils.convert("&8 ┃&f Валюта &c" + id + " &fне существует"));
                return;
            }
            CoinsEngineAPI.removeBalance(p, currency, price);
        }
        if (vault.equalsIgnoreCase("playerpoints")) main.getPP().take(uuid, price);
        if (vault.equalsIgnoreCase("vault")) main.getEconomy().withdrawPlayer(p, price);
        if (vault.equalsIgnoreCase("items")) sellerUtils.setItems(uuid, balance - price);
        sellerUtils.setBoost(uuid, boost + 0.1);

        String def = "&8 ┃&f Вы купили буст &3x!boost";
        String msg = main.getConfig().getString("messages.buy_boost", def);
        DecimalFormat df = new DecimalFormat("#.0");
        String res = df.format(boost + 0.1);
        msg = msg.replace("!boost", res);
        p.sendMessage(utils.convert(msg));
        playSound(p, "onBuyAnything");
    }
    public void buyAutoSell(Player p) {
        UUID uuid = p.getUniqueId();
        String vault = main.getConfig().getString("boosts.value", "vault");
        int balance = 0;

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                p.sendMessage(utils.convert("&8 ┃&f Валюта &c" + id + " &fне существует"));
                return;
            }
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }
        if (vault.equalsIgnoreCase("playerpoints")) balance = main.getPP().look(uuid);
        if (vault.equalsIgnoreCase("vault")) balance = (int) main.getEconomy().getBalance(p);
        if (vault.equalsIgnoreCase("items")) balance = sellerUtils.getItems(uuid);

        int price = main.getConfig().getInt("auto-sell.cost");
        if (balance < price) {
            String def = "&8 ┃&f У вас недостаточно рублей &7{object}/{price}";
            String msg = main.getConfig().getString("messages.balance_error", def);
            msg = msg.replace("{object}", String.valueOf(balance));
            msg = msg.replace("{price}", String.valueOf(price));
            p.sendMessage(utils.convert(msg));
            playSound(p, "onNotEnoughVault");
            return;
        }

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (currency == null) {
                p.sendMessage(utils.convert("&8 ┃&f Валюта &c" + id + " &fне существует"));
                return;
            }
            CoinsEngineAPI.removeBalance(p, currency, price);
        }
        if (vault.equalsIgnoreCase("playerpoints")) main.getPP().take(uuid, price);
        if (vault.equalsIgnoreCase("vault")) main.getEconomy().withdrawPlayer(p, price);
        if (vault.equalsIgnoreCase("items")) sellerUtils.setItems(uuid, balance - price);
        sellerUtils.setAutoSellBought(uuid, true);
        sellerUtils.setAutoSellEnabled(uuid, false);
        String def = "&8 ┃&f Вы купили доступ к авто-продаже предметов";
        String msg = main.getConfig().getString("messages.buy_autosell", def);
        p.sendMessage(utils.convert(msg));
        playSound(p, "onNotEnoughVault");
    }

    public void playSound(Player p, String path) {
        String value = main.getConfig().getString("sounds." + path, "none");
        if (value.equalsIgnoreCase("none")) return;
        Sound sound = Sound.valueOf(value);
        try {
            p.playSound(p.getLocation(), sound, 1, 1);
        } catch (IllegalArgumentException e) {
            main.getLogger().warning("Звук " + sound + " не существует в майнкрафте (Путь: " + "sounds." + path + ")");
        }
    }
}
