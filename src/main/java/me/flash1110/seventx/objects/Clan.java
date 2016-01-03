package me.flash1110.seventx.objects;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.enums.Colors;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by sandleraj on 1/1/16.
 */
public class Clan {

    private UUID leader;
    private ArrayList<UUID> members;
    private ArrayList<UUID> invites;
    private String name;
    private String color;
    private int points;

    public Clan(UUID leader, String name, String color, int points, ArrayList<UUID> members) {
        this.leader = leader;
        this.members = members;

        if (!members.contains(leader))
        members.add(leader);

        this.name = WordUtils.capitalize(name);
        this.color = color;
        this.points = points;

        this.invites = new ArrayList<>();
    }

    public UUID getLeader() {
        return this.leader;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<UUID> getMembers() {
        return this.members;
    }

    public Colors getColor() {
        return Colors.getColor(points);
    }

    public int getPoints() {
        return points;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<UUID> getInvites() {
        return invites;
    }

    public void addInvite(UUID uuid) {
        if (invites.contains(uuid))
            return;

        invites.add(uuid);
    }

    public void removeInvite(UUID uuid) {
        if (!invites.contains(uuid))
            return;

        invites.remove(uuid);
    }

    public boolean addMember(UUID member) {
        boolean willAdd = false;

        if (this.members.contains(member))
            willAdd = false;

        if (members.size() >= SevenTX.INSTANCE.getConfig().getInt("max-players", 5)) {
            willAdd = false;
        }
        willAdd = true;

        if (willAdd)
        this.members.add(member);

        this.points += SevenTX.INSTANCE.getPlayer(member) != null ? SevenTX.INSTANCE.getPlayer(member).getPoints() : 0;

        return willAdd;
    }

    public void removeMember(UUID member) {
        if (!this.members.contains(member))
            return;
        this.members.remove(member);

        this.points -= SevenTX.INSTANCE.getPlayer(member) != null ? SevenTX.INSTANCE.getPlayer(member).getPoints() : 0;
    }

    public void addPoint() {
        this.points++;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void removePoint() {
        this.points--;
    }

    public void handleKill(Player player) {
        color = Colors.getColor(points).getColor().toString();
    }

    public void handleLeave(Player player) {
        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) return;
        int p = cp.getPoints();

        setPoints(this.points - p);

        color = Colors.getColor(points).getColor().toString();
    }

    public void handleJoin(Player player) {
        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) return;
        int p = cp.getPoints();

        setPoints(this.points + p);

        color = Colors.getColor(points).getColor().toString();
    }

   // UUID leader, String name, String color, int points, ArrayList<UUID> members
    private static final String SAVE = "UPDATE clans SET uuid=?, name=?, color=?, points=?, members=? WHERE uuid=?";

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

            StringBuilder member = new StringBuilder();

            boolean isFirst = true;

            for (UUID u : members) {
                if (isFirst) {
                    member.append(u.toString());
                    isFirst = false;
                } else {
                    member.append(u.toString() + ",");
                }
            }

            String memberString = member.toString();

            preparedStatement = con.prepareStatement(SAVE);
            preparedStatement.setString(1, leader.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, color);
            preparedStatement.setInt(4, points);
            preparedStatement.setString(5, memberString);
            preparedStatement.execute();
            preparedStatement.close();

            SevenTX.INSTANCE.getLogger().log(Level.INFO, "Saved clan: " + name);

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

    public void disband() {
        Connection con = null;
        Statement st = null;
        PreparedStatement preparedStatement = null;

        String host = SevenTX.INSTANCE.getConfig().getString("MySQL.HostName");
        String port = SevenTX.INSTANCE.getConfig().getString("MySQL.Port");
        String db = SevenTX.INSTANCE.getConfig().getString("MySQL.DataBaseName");
        String user = SevenTX.INSTANCE.getConfig().getString("MySQL.UserName");
        String password = SevenTX.INSTANCE.getConfig().getString("MySQL.Password");

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, password);
            st = con.createStatement();

            String DELETE = "DELETE FROM CLANS WHERE NAME=?";

            preparedStatement = con.prepareStatement(DELETE);

            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            preparedStatement.close();


            SevenTX.INSTANCE.getLogger().log(Level.INFO, "Disbanded clan: " + name);

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
