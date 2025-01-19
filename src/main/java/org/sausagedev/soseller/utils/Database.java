package org.sausagedev.soseller.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.sausagedev.soseller.SoSeller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Database {
    private final SoSeller main;

    public Database(SoSeller main) {
        this.main = main;
    }

    public void setItems(UUID uuid, int items) {
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
    }
    public void setAutoSellBought(UUID uuid, boolean autoSell) {
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
    }
    public void setAutoSellEnabled(UUID uuid, boolean enabled) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();

            if (!resultSet.next()) {
                add(uuid, connection);
            }
            PreparedStatement ps2 = connection.prepareStatement("UPDATE database SET autosell_enabled = ? WHERE uuid = ?");
            ps2.setString(2, uuid.toString());
            ps2.setBoolean(1, enabled);
            ps2.executeUpdate();
            ps2.close();
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void setBoost(UUID uuid, double boost) {
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
    }

    public void setAutoSellItem(UUID uuid, String material) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();

            if (!resultSet.next()) {
                add(uuid, connection);
            }

            PreparedStatement ps2 = connection.prepareStatement("UPDATE database SET autosell_list = ? WHERE uuid = ?");
            ps2.setString(2, uuid.toString());
            ps2.setString(1, String.join(" " , getAutoSellList(uuid)) + " " + material);
            ps2.executeUpdate();
            ps2.close();
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void removeAutoSellItem(UUID uuid, String material) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM database WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();

            if (!resultSet.next()) {
                add(uuid, connection);
            }

            List<String> list = new ArrayList<>(getAutoSellList(uuid));
            list.removeIf(mat -> mat.equalsIgnoreCase(material));
            PreparedStatement ps2 = connection.prepareStatement("UPDATE database SET autosell_list = ? WHERE uuid = ?");
            ps2.setString(2, uuid.toString());
            ps2.setString(1, String.join(" ", list));
            ps2.executeUpdate();
            ps2.close();
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void add(UUID uuid, Connection connection) throws SQLException {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        PreparedStatement ps = connection.prepareStatement("INSERT INTO database(uuid, " +
                "nick, " +
                "items, " +
                "boost, " +
                "autosell, " +
                "autosell_enabled, " +
                "autosell_list) VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, uuid.toString());
        ps.setString(2, player.getName());
        ps.setInt(3, 0);
        ps.setDouble(4, 1);
        ps.setBoolean(5, false);
        ps.setBoolean(6, false);
        ps.setString(7, "none");
        ps.executeUpdate();
        ps.close();
    }

    public int getItems(UUID uuid) {
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

    public double getBoost(UUID uuid) {
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

    public List<String> getAutoSellList(UUID uuid) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT autosell_list FROM database WHERE uuid = ?");
            if (ps.isClosed()) return new ArrayList<>();
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String listString = resultSet.getString("autosell_list");
                return Arrays.asList(listString.split(" "));
            }
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean isAutoSellItem(UUID uuid, String material) {
        return getAutoSellList(uuid).contains(material);
    }

    public boolean isBoughtAutoSell(UUID uuid) {
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
    public boolean isEnabledAutoSell(UUID uuid) {
        try {
            Connection connection = main.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT autosell_enabled FROM database WHERE uuid = ?");
            if (ps.isClosed()) return false;
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("autosell_enabled");
            }
            ps.close();
        } catch (SQLException e) {
            main.getLogger().severe("SQLException error: " + e.getCause());
            e.printStackTrace();
        }

        return false;
    }

    public boolean isClosed(UUID uuid) {
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