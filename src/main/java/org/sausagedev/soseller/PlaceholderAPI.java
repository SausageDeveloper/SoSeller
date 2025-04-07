package org.sausagedev.soseller;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sausagedev.soseller.database.DataManager;
import org.sausagedev.soseller.сonfiguration.Config;

import java.util.Map;

public class PlaceholderAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "soseller";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SausageDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer op, @NotNull String params) {
        if (op != null && op.isOnline()) {
            Player p = op.getPlayer();
            if (p == null) return null;
            DataManager.PlayerData playerData = DataManager.search(p.getUniqueId());
            if (params.equalsIgnoreCase("boost")) {
                return String.valueOf(playerData.getBoost());
            }
            if (params.equalsIgnoreCase("globalboost")) {
                double globalBoost = Config.settings().globalBoost();
                return String.valueOf(globalBoost);
            }
            if (params.equalsIgnoreCase("items")) {
                return String.valueOf(playerData.getItems());
            }
            if (params.equalsIgnoreCase("autosell_price")) {
                return Config.settings().autoSell().get("cost").toString();
            }
            if (params.contains("price_")) {
                Map<String, Object> items = Config.settings().sellItems();
                String item = params.replace("price_", "");
                return items.containsKey(item) ? items.get(item) + ".0" : "0.0";
            }
            if (params.contains("priceboost_")) {
                Map<String, Object> items = Config.settings().sellItems();
                String item = params.replace("priceboost_", "");
                double boost = playerData.getBoost();
                double globalBoost = Config.settings().globalBoost();
                Object intItem = items.get(item);
                int value = (int) (intItem != null ? intItem : 0);
                double res = Math.round((value * boost * globalBoost) * 10.0) / 10.0;
                return items.containsKey(item) ? String.valueOf(res) : "0.0";
            }
        }
        return null;
    }
}
