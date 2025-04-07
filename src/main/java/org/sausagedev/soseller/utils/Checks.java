package org.sausagedev.soseller.utils;

import org.bukkit.entity.Player;
import org.sausagedev.soseller.Configuration.Config;
import su.nightexpress.coinsengine.api.currency.Currency;

public class Checks {
    private final Player p;

    public Checks(Player p) {
        this.p = p;
    }

    public boolean checkBalance(int balance, int price) {
        if (balance < price) {
            String msg = Config.messages().balanceEror();
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
            p.sendMessage(Utils.convert(Config.messages().balanceEror()));
            return false;
        }
        return true;
    }

    public boolean gotBoostsLimit(int price, double boost) {
        if (price != 0) return false;
        String msg = Config.messages().balanceEror();
        msg = msg.replace("{object}", String.valueOf(boost));
        p.sendMessage(Utils.convert(msg));
        return true;
    }

    public boolean checkCurrencyAbsence(Currency currency) {
        if (currency == null) return true;
        p.sendMessage(Utils.convert(Config.messages().balanceEror()));
        return false;
    }
}
