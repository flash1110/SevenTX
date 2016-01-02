package me.flash1110.seventx.objects;

import me.flash1110.seventx.SevenTX;

import java.sql.*;
import java.util.UUID;

/**
 * Created by sandleraj on 1/1/16.
 */
public class ClanPlayer {

    private UUID uuid;
    private int points;
    private Clan clan;
    private int currentKillstreak;
    private int highestKillstreak;
    private int kills;
    private int deaths;

    public ClanPlayer(UUID uuid, int kills, int deaths, int points, int ks, int high, Clan clan) {
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.points = points;
        this.currentKillstreak = ks;
        this.highestKillstreak = high;
        this.clan = clan;
    }

    public int getPoints() {
        return this.points;
    }

    public Clan getClan() {
        return this.clan;
    }

    public int getKillStreak() {
        return this.currentKillstreak;
    }

    public void addKillStreak() {
        this.currentKillstreak++;
    }

    public void removeKillstreak() {
        if (this.currentKillstreak > this.highestKillstreak) {
            this.highestKillstreak = this.currentKillstreak;
        }

        this.currentKillstreak = 0;
    }

    public int getHighestKillstreak() {
        return highestKillstreak;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    private static final String SAVE = "UPDATE players SET uuid=?, kills=?, deaths=?, points=?, killstreak=?, highkill=?, clan=? WHERE uuid=?";

    public void save() {
        Connection con = null;
        Statement st = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String host = SevenTX.INSTANCE.getConfig().getString("MySQL.HostName");
        String port = SevenTX.INSTANCE.getConfig().getString("MySQL.Port");
        String db = SevenTX.INSTANCE.getConfig().getString("MySQL.DataBaseName");
        String user = SevenTX.INSTANCE.getConfig().getString("MySQL.UserName");
        String password = SevenTX.INSTANCE.getConfig().getString("MySQL.Password");

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password);
            st = con.createStatement();

            preparedStatement = con.prepareStatement(SAVE);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, kills);
            preparedStatement.setInt(3, deaths);
            preparedStatement.setInt(4, points);
            preparedStatement.setInt(5, currentKillstreak);
            preparedStatement.setInt(6, highestKillstreak);
            preparedStatement.setString(7, clan.getName());
            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
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
            }
        }
    }
}
