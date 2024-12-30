package org.sausagedev.soseller;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sausagedev.soseller.utils.SellerUtils;

import java.util.Map;
import java.util.UUID;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final SellerUtils sellerUtils;
    private final SoSeller main;
    public PlaceholderAPI(SellerUtils sellerUtils, SoSeller main) {
        this.sellerUtils = sellerUtils;
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
        return "1.8.1";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer op, @NotNull String params) {
        if (op != null && op.isOnline()) {
            Player p = op.getPlayer();
            if (p == null) return null;
            UUID uuid = p.getUniqueId();
            if (sellerUtils.isClosed(uuid)) return null;
            if (params.equalsIgnoreCase("boost")) {
                return String.valueOf(sellerUtils.getBoost(uuid));
            }
            if (params.equalsIgnoreCase("items")) {
                return String.valueOf(sellerUtils.getItems(uuid));
            }
            if (params.equalsIgnoreCase("autosell_price")) {
                return main.getConfig().getString("auto-sell.cost");
            }
            if (params.contains("price_")) {
                Map<String, Object> items = main.getConfig().getConfigurationSection("sell_items").getValues(false);
                String item = params.replace("price_", "");
                return items.containsKey(item) ? items.get(item) + ".0" : "0.0";
            }
            if (params.contains("priceboost_")) {
                Map<String, Object> items = main.getConfig().getConfigurationSection("sell_items").getValues(false);
                String item = params.replace("priceboost_", "");
                double boost = sellerUtils.getBoost(uuid);
                double res = Math.round(((int) items.get(item) * boost) * 10.0) / 10.0;
                return items.containsKey(item) ? String.valueOf(res) : "0.0";
            }
        }
        return null;
    }
}
