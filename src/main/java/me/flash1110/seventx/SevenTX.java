package me.flash1110.seventx;

import me.flash1110.seventx.commands.*;
import me.flash1110.seventx.listeners.PlayerListener;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import me.flash1110.seventx.tasks.ClanSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sandleraj on 1/1/16.
 */
public class SevenTX extends JavaPlugin {

    public static SevenTX INSTANCE;

    public HashMap<UUID, ClanPlayer> players = new HashMap<>();
    public HashMap<String, Clan> clans = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        if (createClanTable())
            getLogger().info("Loaded clan table");
        if (createPlayerTable()) ;
        getLogger().info("Loaded player table");

        getCommand("clancreate").setExecutor(new CreateClan());
        getCommand("claninvite").setExecutor(new InviteCommand());
        getCommand("clankick").setExecutor(new KickInvite());
        getCommand("clantag").setExecutor(new ClanTag());
        getCommand("clanjoin").setExecutor(new ClanJoin());
        getCommand("clanleader").setExecutor(new ClanLeader());
        getCommand("clandelete").setExecutor(new ClanDelete());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        ClanSave.startDelayedTasks();

      /*  for (Player player : Bukkit.getOnlinePlayers()) {
            ClanPlayer p = SevenTX.INSTANCE.loadPlayer(player);

            if (p != null)
                SevenTX.INSTANCE.updatePlayer(player.getUniqueId(), p);

            Clan clan = SevenTX.INSTANCE.loadClan(player);

            if (clan != null)
                SevenTX.INSTANCE.updateClan(clan.getName(), clan);
        } */

    }

    @Override
    public void onDisable() {
        for (Clan clan : clans.values()) {
            clan.save();
        }

        for (ClanPlayer player : players.values()) {
            player.save();
        }


        INSTANCE = null;
    }

    public HashMap<UUID, ClanPlayer> getPlayers() {
        return players;
    }

    public ClanPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public ClanPlayer getPlayer(Player player) {
       return this.getPlayer(player.getUniqueId());
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, new ClanPlayer(uuid, 0, 0, 0, 0, 0, null));
    }

    public void addPlayer(Player player) {
        this.addPlayer(player.getUniqueId());
    }

    public void updatePlayer(UUID uuid, ClanPlayer player) {
        players.put(uuid, player);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public HashMap<String, Clan> getClans() {
        return clans;
    }

    public Clan getClan(String name) {
        return clans.get(name);
    }

    public void addClan(String name) {
        clans.put(name, new Clan(null, name, "WHITE", 0, new ArrayList<UUID>()));
    }

    public void updateClan(String name, Clan clan) {
        if (clan.getName().equals("default")) return;
        clans.put(name, clan);
    }

    public void removeClan(String name) {
        clans.remove(name);
    }

    private static final String INSERT = "INSERT INTO PLAYERS VALUES(id, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE UUID=?";
    private static final String SELECT = "SELECT UUID,KILLS,DEATHS,POINTS,KILLSTREAK,HIGHKILL,CLAN FROM PLAYERS WHERE UUID=?";

    public boolean createPlayerTable() {
        boolean boo = false;
        Connection con = null;
        Statement st = null;

        String host = getConfig().getString("MySQL.HostName");
        String port = getConfig().getString("MySQL.Port");
        String db = getConfig().getString("MySQL.DataBaseName");
        String user = getConfig().getString("MySQL.UserName");
        String password = getConfig().getString("MySQL.Password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + db;

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();

            st.execute("CREATE TABLE IF NOT EXISTS PLAYERS(" +
                    "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "UUID VARCHAR(36)," +
                    "KILLS INT," +
                    "DEATHS INT," +
                    "POINTS INT," +
                    "KILLSTREAK INT," +
                    "HIGHKILL INT," +
                    "CLAN TEXT);");
            boo = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            boo = false;
        } finally {
            try {
                if (st != null) {
                    st.close();
                }


                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                boo = false;
            }
        }
        return boo;
    }

    private static final String INSERTCLAN = "INSERT INTO CLANS VALUES(id, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE UUID=?";
    private static final String SELECTCLAN = "SELECT UUID,MEMBERS,POINTS,COLOR,NAME FROM CLANS WHERE UUID=?";

    public boolean createClanTable() {
        boolean boo = false;
        Connection con = null;
        Statement st = null;

        String host = getConfig().getString("MySQL.HostName");
        String port = getConfig().getString("MySQL.Port");
        String db = getConfig().getString("MySQL.DataBaseName");
        String user = getConfig().getString("MySQL.UserName");
        String password = getConfig().getString("MySQL.Password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + db;

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();

            st.execute("CREATE TABLE IF NOT EXISTS CLANS(" +
                    "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "UUID VARCHAR(36)," +
                    "MEMBERS LONGTEXT," +
                    "POINTS INT," +
                    "COLOR TEXT," +
                    "NAME TEXT);");
            boo = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            boo = false;
        } finally {
            try {
                if (st != null) {
                    st.close();
                }


                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                boo = false;
            }
        }

        return boo;
    }

    public Clan loadClan(Player player) {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Clan clan = null;

        String host = getConfig().getString("MySQL.HostName");
        String port = getConfig().getString("MySQL.Port");
        String db = getConfig().getString("MySQL.DataBaseName");
        String user = getConfig().getString("MySQL.UserName");
        String password = getConfig().getString("MySQL.Password");

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password);

            //private static final String INSERTCLAN = "INSERT INTO CLANS VALUES(id, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=?";
            //private static final String SELECTCLAN = "SELECT uuid,members,points,color,name FROM CLANS WHERE uuid=?";

            preparedStatement = con.prepareStatement(SELECTCLAN);
            preparedStatement.setString(1, player.getUniqueId().toString());

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                preparedStatement = con.prepareStatement(INSERTCLAN);
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, "{}");
                preparedStatement.setInt(3, 0);
                preparedStatement.setString(4, "");
                preparedStatement.setString(5, "default");
                preparedStatement.setString(6, "default");
                preparedStatement.executeUpdate();
            }

            preparedStatement = con.prepareStatement(SELECTCLAN);
            preparedStatement.setString(1, player.getUniqueId().toString());

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String s = resultSet.getString("members");

                ArrayList<UUID> members = new ArrayList<>();

                if (s.contains("-")) {
                    for (String string : s.split(",")) {
                        members.add(UUID.fromString(string));
                    }
                }


                int points = resultSet.getInt("points");
                String color = resultSet.getString("color");
                String name = resultSet.getString("name");

                clan = new Clan(uuid, name, color, points, members);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return clan;
    }

    public ClanPlayer loadPlayer(Player player) {
        Connection con = null;
        Statement st = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ClanPlayer cp = null;

        String host = getConfig().getString("MySQL.HostName");
        String port = getConfig().getString("MySQL.Port");
        String db = getConfig().getString("MySQL.DataBaseName");
        String user = getConfig().getString("MySQL.UserName");
        String password = getConfig().getString("MySQL.Password");

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password);
            st = con.createStatement();

            preparedStatement = con.prepareStatement(SELECT);
            preparedStatement.setString(1, player.getUniqueId().toString());

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                preparedStatement = con.prepareStatement(INSERT);
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, 0);
                preparedStatement.setInt(4, 0);
                preparedStatement.setInt(5, 0);
                preparedStatement.setInt(6, 0);
                preparedStatement.setString(7, "default");
                preparedStatement.setString(8, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }

            preparedStatement = con.prepareStatement(SELECT);
            preparedStatement.setString(1, player.getUniqueId().toString());

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                int kills = resultSet.getInt("kills");
                int deaths = resultSet.getInt("deaths");
                int points = resultSet.getInt("points");
                int ks = resultSet.getInt("killstreak");
                int high = resultSet.getInt("highkill");
                String name = resultSet.getString("clan");
                if (name.equals("default")) {
                    cp = new ClanPlayer(uuid, kills, deaths, points, ks, high, null);
                } else {
                    cp = new ClanPlayer(uuid, kills, deaths, points, ks, high, clans.get(name));
                }
            }
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return cp;
    }
}
