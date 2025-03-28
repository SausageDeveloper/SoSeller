package org.sausagedev.soseller.functions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.*;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AutoSellModify {
    private final SoSeller main = SoSeller.getPlugin();
    private final FileConfiguration config = Config.getSettings();
    private final FileConfiguration messages = Config.getMessages();

    public void buyAutoSell(Player p) {
        Checks checks = new Checks(p);
        UUID uuid = p.getUniqueId();
        DataManager.PlayerData playerData = DataManager.search(uuid), old = playerData.clone();
        String vault = config.getString("boosts.value", "vault").toLowerCase();
        int balance = 0;

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (checks.checkCurrencyAbsence(currency)) return;
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }

        switch (vault) {
            case "playerpoints":
                balance = main.getPP().look(uuid);
                break;
            case "vault":
                balance = (int) main.getEconomy().getBalance(p);
                break;
            case "items":
                balance = playerData.getItems();
                break;
        }

        int price = config.getInt("auto-sell.cost", 0);
        if (!checks.checkBalance(balance, price)) return;

        if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (checks.checkCurrencyAbsence(currency)) return;
            CoinsEngineAPI.removeBalance(p, currency, price);
        }

        CompletableFuture.runAsync(() -> {
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
        });

        DataManager.replace(old, playerData);
        String def = "&8 ┃&f Вы купили доступ к авто-продаже предметов";
        String msg = messages.getString("messages.buy_autosell", def);
        p.sendMessage(Utils.convert(msg));
        Utils.playSound(p, "onNotEnoughVault");
    }

    public void offOnAutoSellItem(Player p, Material material) {
        CompletableFuture.runAsync(() -> {
            UUID uuid = p.getUniqueId();
            DataManager.PlayerData playerData = DataManager.search(uuid);
            boolean itemEnabled = AutoSell.isEnabled(uuid, material);
            if (!playerData.isAutoSellBought()) return;
            else if (itemEnabled) {
                AutoSell.disableMaterial(uuid, material);
                return;
            }
            AutoSell.enableMaterial(uuid, material);
        });
    }
}
