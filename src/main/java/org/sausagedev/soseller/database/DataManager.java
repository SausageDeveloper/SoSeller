package org.sausagedev.soseller.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataManager {
    static final Set<PlayerData> dataContainer = new HashSet<>();

    public static Set<PlayerData> getDataContainer() {
        return dataContainer;
    }

    public static PlayerData search(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        assert p != null;
        PlayerData playerData = new PlayerData(p);
        for (PlayerData data : dataContainer) {
            if (!data.getUUID().equals(uuid)) continue;
            return data;
        }
        return playerData;
    }

    public static void replace(PlayerData old, PlayerData current) {
        dataContainer.remove(old);
        dataContainer.add(current);
    }

    public static void importData(Connection connection) {
        Database.getUUIDs().forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;
            Database.register(uuid, connection);
            PlayerData playerData = new PlayerData(p);
            playerData.setItems(Database.getItems(uuid))
                    .setBoost(Database.getBoost(uuid))
                    .setAutoSellBought(Database.isBoughtAutoSell(uuid));
            DataManager.getDataContainer().add(playerData);
        });
    }

    public static void exportData(Connection connection) {
        dataContainer.forEach(playerData -> {
            UUID uuid = playerData.getUUID();
            Database.register(uuid, connection);
            Database.setItems(uuid, playerData.getItems());
            Database.setBoost(uuid, playerData.getBoost());
            Database.setAutoSellBought(uuid, playerData.isAutoSellBought());
        });
    }

    public static class PlayerData implements Cloneable {
        final UUID uuid;
        final String nick;
        int items;
        double boost;
        boolean autoSell;

        public PlayerData(Player p) {
            uuid = p.getUniqueId();
            nick = p.getDisplayName();
            items = 0;
            boost = 1;
            autoSell = false;
        }

        public PlayerData setItems(int items) {
            this.items = items;
            return this;
        }

        public PlayerData addItems(int items) {
            this.items += items;
            return this;
        }

        public PlayerData takeItems(int items) {
            this.items -= items;
            return this;
        }

        public PlayerData setBoost(double boost) {
            this.boost = boost;
            return this;
        }

        public PlayerData addBoost(double boost) {
            DecimalFormat df = new DecimalFormat("#.0");
            this.boost += Double.parseDouble(df.format(boost).replace(',', '.'));
            return this;
        }

        public PlayerData takeBoost(double boost) {
            this.boost -= boost;
            return this;
        }

        public PlayerData setAutoSellBought(boolean autoSell) {
            this.autoSell = autoSell;
            return this;
        }

        public int getItems() {
            return items;
        }

        public double getBoost() {
            DecimalFormat df = new DecimalFormat("#.0");
            return Double.parseDouble(df.format(boost).replace(',', '.'));
        }

        public boolean isAutoSellBought() {
            return autoSell;
        }

        public UUID getUUID() {
            return uuid;
        }

        @Override
        public PlayerData clone() {
            try {
                PlayerData clone = (PlayerData) super.clone();
                // TODO: copy mutable state here, so the clone can't change the internals of the original
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
