package org.sausagedev.soseller.functions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.utils.Checks;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Utils;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BoostsModify {
    private final SoSeller main = SoSeller.getPlugin();
    private final FileConfiguration messages = Config.getMessages();

    public void buyBoost(Player p) {
        Checks checks = new Checks(p);
        UUID uuid = p.getUniqueId();
        DataManager.PlayerData playerData = DataManager.search(uuid);
        FileConfiguration config = main.getConfig();
        int balance = 0;
        int price = 0;
        double boost = playerData.getBoost();
        Map<String, Object> boosts = config.getConfigurationSection("boosts").getValues(false);
        String vault = null;
        for (String key : boosts.keySet()) {
            if (key.equalsIgnoreCase("message")) continue;
            Map<String, Object> boostParams = config.getConfigurationSection("boosts." + key).getValues(false);
            if (boost >= Integer.parseInt(key)) continue;
            price = (int) boostParams.get("price");
            vault = (String) boostParams.get("value");
            if (vault != null) vault = vault.toLowerCase();
            break;
        }

        if (checks.gotBoostsLimit(price, boost)) return;
        else if (!checks.vaultExists(vault)) return;
        else if (vault.contains("coinsengine:")) {
            String id = vault.replace("coinsengine:", "");
            Currency currency = CoinsEngineAPI.getCurrency(id);
            if (!checks.checkCurrencyAbsence(currency)) return;
            assert currency != null;
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

        String def = "&8 ┃&f Вы купили буст &3x{boost}";
        String msg = messages.getString("buy_boost", def);
        DecimalFormat df = new DecimalFormat("#.0");
        String res = df.format(boost + 0.1);
        msg = msg.replace("{boost}", res);
        p.sendMessage(Utils.convert(msg));
        Utils.playSound(p, "onBuyAnything");
    }
}
