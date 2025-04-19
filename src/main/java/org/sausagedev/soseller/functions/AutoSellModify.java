package org.sausagedev.soseller.functions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.configuration.Config;
import org.sausagedev.soseller.configuration.data.SettingsField;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.*;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import java.util.UUID;

public class AutoSellModify {
    private final SoSeller main = SoSeller.getPlugin();

    public void buyAutoSell(Player p) {
        Checks checks = new Checks(p);
        UUID uuid = p.getUniqueId();
        DataManager.PlayerData playerData = DataManager.search(uuid), old = playerData.clone();
        SettingsField settings = Config.settings();
        String vault = settings.autoSell().get("value").toString().toLowerCase();
        int balance = 0;

        if (vault.contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (checks.checkCurrencyAbsence(currency)) return;
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }

        balance = switch (vault) {
            case "playerpoints" -> main.getPP().look(uuid);
            case "vault" -> (int) main.getEconomy().getBalance(p);
            case "items" -> playerData.getItems();
            default -> balance;
        };

        int price = (int) settings.autoSell().get("cost");
        if (!checks.checkBalance(balance, price)) return;

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (checks.checkCurrencyAbsence(currency)) return;
            assert currency != null;
            CoinsEngineAPI.removeBalance(p, currency, price);
        }

        switch (vault) {
            case "playerpoints":
                main.getPP().take(uuid, price);
                break;
            case "vault":
                main.getEconomy().withdrawPlayer(p, price);
                break;
            case "items":
                playerData.takeItems(price);
                break;
        }

        playerData.setAutoSellBought(true);
        if (AutoSell.isEnabled(uuid)) AutoSell.disable(uuid);

        DataManager.replace(old, playerData);
        p.sendMessage(Utils.convert(Config.messages().buyAutosell()));
        Utils.playSound(p, "onNotEnoughVault");
    }

    public void offOnAutoSellItem(Player p, Material material) {
        UUID uuid = p.getUniqueId();
        DataManager.PlayerData playerData = DataManager.search(uuid);
        boolean itemEnabled = AutoSell.isEnabled(uuid, material);
        if (!playerData.isAutoSellBought()) return;
        else if (itemEnabled) {
            AutoSell.disableMaterial(uuid, material);
            return;
        }
        AutoSell.enableMaterial(uuid, material);
    }
}
