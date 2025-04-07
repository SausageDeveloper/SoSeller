package org.sausagedev.soseller.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;

import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Database {
    private static final SoSeller main = SoSeller.getPlugin();
    private static Connection connection;

    public Database(File database) {
        try {
            Database.connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS database (" +
                    "uuid TEXT, " +
                    "nick TEXT, " +
                    "items INTEGER, " +
                    "boost DOUBLE, " +
                    "autosell BOOLEAN)");
            statement.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setItems(UUID uuid, int items) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
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
    }
    public static void setAutoSellBought(UUID uuid, boolean autoSell) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
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
    }

    public static void setBoost(UUID uuid, double boost) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
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
    }

    public static void register(UUID uuid) {
        try {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            PreparedStatement pstm = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            pstm.setString(1, uuid.toString());
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) return;
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
    }

    public static int getItems(UUID uuid) {
        try {
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

    public static List<UUID> getUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        String query = "SELECT uuid FROM database";

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (pstmt.isClosed()) return Collections.emptyList();

            while (rs.next()) {
                uuids.add(UUID.fromString(rs.getString("uuid")));
            }

        } catch (SQLException e) {
            main.getLogger().severe("Error retrieving UUIDs: " + e.getMessage());
        }

        return uuids;
    }

    public static double getBoost(UUID uuid) {
        try {
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
}
