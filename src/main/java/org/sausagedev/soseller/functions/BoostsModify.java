package org.sausagedev.soseller.functions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.Checks;
import org.sausagedev.soseller.Configuration.Config;
import org.sausagedev.soseller.utils.Utils;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BoostsModify {
    private final SoSeller main = SoSeller.getPlugin();

    public void buyBoost(Player p) {
        Checks checks = new Checks(p);
        UUID uuid = p.getUniqueId();
        DataManager.PlayerData playerData = DataManager.search(uuid);
        int balance = 0;
        int price = 0;
        double boost = playerData.getBoost();
        ConfigurationSection boosts = Config.settings().boosts();
        String vault = null;
        for (String key : boosts.getKeys(false)) {
            Map<String, Object> boostParams = boosts.getConfigurationSection(key).getValues(false);
            if (boost >= Integer.parseInt(key)) continue;
            price = (int) boostParams.get("price");
            vault = (String) boostParams.get("value");
            if (vault != null) vault = vault.toLowerCase();
            break;
        }

        if (vault == null || checks.gotBoostsLimit(price, boost)) return;
        else if (!checks.vaultExists(vault)) return;
        else if (vault.contains("coinsengine:")) {
            String id = vault.replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (!checks.checkCurrencyAbsence(currency)) return;
            assert currency != null;
            balance = (int) CoinsEngineAPI.getBalance(p, currency);
        }

        balance = switch (vault) {
            case "playerpoints" -> main.getPP().look(uuid);
            case "vault" -> (int) main.getEconomy().getBalance(p);
            case "items" -> playerData.getItems();
            default -> balance;
        };

        if (!checks.checkBalance(balance, price)) return;
        else if (vault.toLowerCase().contains("coinsengine:")) {
            String id = vault.toLowerCase().replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (!checks.checkCurrencyAbsence(currency)) return;
            assert currency != null;
            CoinsEngineAPI.removeBalance(p, currency, price);
        }

        int finalPrice = price;
        String finalVault = vault;
        CompletableFuture.runAsync(() -> {
            switch (finalVault) {
                case "playerpoints":
                    main.getPP().take(uuid, finalPrice);
                    break;
                case "vault":
                    main.getEconomy().withdrawPlayer(p, finalPrice);
                    break;
                case "items":
                    playerData.takeItems(finalPrice);
                    break;
            }
            playerData.addBoost(0.1);
        });

        String msg = Config.messages().buyBoost();
        DecimalFormat df = new DecimalFormat("#.0");
        String res = df.format(boost + 0.1);
        msg = msg.replace("{boost}", res);
        p.sendMessage(Utils.convert(msg));
        Utils.playSound(p, "onBuyAnything");
    }
}
