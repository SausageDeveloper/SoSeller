package org.sausagedev.soseller.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database {
    private static final SoSeller main = SoSeller.getPlugin();

    public static void setItems(UUID uuid, int items) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = main.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();

                if (!resultSet.next()) {
                    add(uuid, connection);
                }
                PreparedStatement ps2 = connection.prepareStatement("UPDATE database SET items = ? WHERE uuid = ?");
                ps2.setString(2, uuid.toString());
                ps2.setInt(1, items);
                ps2.executeUpdate();
                ps2.close();
                ps.close();
            } catch (SQLException e) {
                main.getLogger().severe("SQLException error: " + e.getCause());
                e.printStackTrace();
            }
        });
    }

    public static void checkAndCreate(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = main.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT uuid FROM database WHERE uuid = ?"
                );
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    Player player = main.getServer().getPlayer(uuid);
                    if (player != null) {
                        PreparedStatement insertPs = connection.prepareStatement(
                                "INSERT INTO database(uuid, nick, items, boost, autosell) " +
                                        "VALUES (?, ?, 0, 1, 0)"
                        );
                        insertPs.setString(1, uuid.toString());
                        insertPs.setString(2, player.getName());
                        insertPs.executeUpdate();
                        insertPs.close();
                    }
                }

                ps.close();
                rs.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public static void setAutoSellBought(UUID uuid, boolean autoSell) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = main.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();

                if (!resultSet.next()) {
                    add(uuid, connection);
                }
                PreparedStatement ps2 = connection.prepareStatement("UPDATE database SET autosell = ? WHERE uuid = ?");
                ps2.setString(2, uuid.toString());
                ps2.setBoolean(1, autoSell);
                ps2.executeUpdate();
                ps2.close();
                ps.close();
            } catch (SQLException e) {
                main.getLogger().severe("SQLException error: " + e.getCause());
                e.printStackTrace();
            }
        });
    }

    public static void setBoost(UUID uuid, double boost) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = main.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();

                if (!resultSet.next()) {
                    add(uuid, connection);
                }
                PreparedStatement ps2 = connection.prepareStatement("UPDATE database SET boost = ? WHERE uuid = ?");
                ps2.setString(2, uuid.toString());
                ps2.setDouble(1, boost);
                ps2.executeUpdate();
                ps2.close();
                ps.close();
            } catch (SQLException e) {
                main.getLogger().severe("SQLException error: " + e.getCause());
                e.printStackTrace();
            }
        });
    }

    private static void add(UUID uuid, Connection connection) throws SQLException {
        CompletableFuture.runAsync(() -> {
            try {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return;
                PreparedStatement ps = connection.prepareStatement("INSERT INTO database(" +
                        "uuid, " +
                        "nick, " +
                        "items, " +
                        "boost, " +
                        "autosell) VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, player.getName());
                ps.setInt(3, 0);
                ps.setDouble(4, 1);
                ps.setBoolean(5, false);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static int getItems(UUID uuid) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT items FROM database WHERE uuid = ?");
            if (ps.isClosed()) return 0;
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("items");
            }
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
        
        return 0;
    }

    public static double getBoost(UUID uuid) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT boost FROM database WHERE uuid = ?");
            if (ps.isClosed()) return 1.0;
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                double n = resultSet.getDouble("boost");
                DecimalFormat df = new DecimalFormat("#.0");
                return Double.parseDouble(df.format(n).replace(',', '.'));
            }
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
        return 1.0;
    }

    public static boolean isBoughtAutoSell(UUID uuid) {
        if (main.getConfig().getInt("auto-sell.cost", 0) <= 0) return true;
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT autosell FROM database WHERE uuid = ?");
            if (ps.isClosed()) return false;
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("autosell");
            }
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isClosed(UUID uuid) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            return ps.isClosed();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: Is closed");
            e.printStackTrace();
        }
        return true;
    }
}
