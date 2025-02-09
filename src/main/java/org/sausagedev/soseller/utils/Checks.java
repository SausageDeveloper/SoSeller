package org.sausagedev.soseller.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.currency.Currency;

public class Checks {
    private final FileConfiguration messages = Config.getMessages();
    private final Player p;

    public Checks(Player p) {
        this.p = p;
    }

    public boolean checkBalance(int balance, int price) {
        if (balance < price) {
            String def = "&8 ┃&f У вас недостаточно рублей &7{object}/{price}";
            String msg = messages.getString("balance_error", def);
            msg = msg.replace("{object}", String.valueOf(balance));
            msg = msg.replace("{price}", String.valueOf(price));
            p.sendMessage(Utils.convert(msg));
            Utils.playSound(p, "onNotEnoughVault");
            return false;
        }
        return true;
    }

    public boolean vaultExists(String vault) {
        if (vault == null) {
            String def = "&8 ┃&f Валюта не &cсуществует &fили не &cуказана";
            String msg = Config.getMessages().getString("vault_error", def);
            p.sendMessage(Utils.convert(msg));
            return false;
        }
        return true;
    }

    public boolean gotBoostsLimit(int price, double boost) {
        if (price != 0) return false;
        String def = "&8 ┃&f Вы достигли последнего буста &7x{object}";
        String msg = messages.getString("max_boost_error", def);
        msg = msg.replace("{object}", String.valueOf(boost));
        p.sendMessage(Utils.convert(msg));
        return true;
    }

    public boolean checkCurrencyAbsence(Currency currency) {
        if (currency == null) return true;
        String def = "&8 ┃&f Валюта не &cсуществует &fили не &cуказана";
        String msg = messages.getString("vault_error", def);
        p.sendMessage(Utils.convert(msg));
        return false;
    }
}
