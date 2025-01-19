package org.sausagedev.soseller;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sausagedev.soseller.utils.Config;
import org.sausagedev.soseller.utils.Database;
import org.sausagedev.soseller.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final Database database;
    private final SoSeller main;
    public PlaceholderAPI(Database database, SoSeller main) {
        this.database = database;
        this.main = main;
    }

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
        return "1.9.3";
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
            UUID uuid = p.getUniqueId();
            if (database.isClosed(uuid)) return null;
            FileConfiguration config = main.getConfig();
            if (params.equalsIgnoreCase("boost")) {
                return String.valueOf(database.getBoost(uuid));
            }
            if (params.equalsIgnoreCase("globalboost")) {
                double globalBoost = config.getDouble("global_boost", 1);
                return String.valueOf(globalBoost);
            }
            if (params.equalsIgnoreCase("items")) {
                return String.valueOf(database.getItems(uuid));
            }
            if (params.equalsIgnoreCase("autosell_price")) {
                return config.getString("auto-sell.cost");
            }
            if (params.contains("price_")) {
                Map<String, Object> items = config.getConfigurationSection("sell_items").getValues(false);
                String item = params.replace("price_", "");
                return items.containsKey(item) ? items.get(item) + ".0" : "0.0";
            }
            if (params.contains("priceboost_")) {
                Map<String, Object> items = config.getConfigurationSection("sell_items").getValues(false);
                String item = params.replace("priceboost_", "");
                double boost = database.getBoost(uuid);
                double globalBoost = config.getDouble("global_boost", 1);
                Object intItem = items.get(item);
                int value = (int) (intItem != null ? intItem : 0);
                double res = Math.round((value * boost * globalBoost) * 10.0) / 10.0;
                return items.containsKey(item) ? String.valueOf(res) : "0.0";
            }
        }
        return null;
    }
}
