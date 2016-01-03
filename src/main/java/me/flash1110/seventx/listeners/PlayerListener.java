package me.flash1110.seventx.listeners;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Created by sandleraj on 1/2/16.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GOLD + "Contacting the SQL servers to load your data...");

        Clan clan = SevenTX.INSTANCE.loadClan(event.getPlayer());
        if (clan == null) return;
        if (clan != null && clan.getName() != null && !clan.getName().equals("default")) {
            SevenTX.INSTANCE.updateClan(clan.getName(), clan);
            event.getPlayer().sendMessage(ChatColor.GREEN + "...Successfully loaded your clan data");
        }

        ClanPlayer player = SevenTX.INSTANCE.loadPlayer(event.getPlayer());
        if (player == null) return;
        if (player != null) {
            SevenTX.INSTANCE.updatePlayer(event.getPlayer().getUniqueId(), player);
            event.getPlayer().sendMessage(ChatColor.GREEN + "...Successfully loaded your data");
        }

        if (SevenTX.INSTANCE.getClan(clan.getName()) == null) {
            player.setClan(null);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Your clan was disbanded");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(event.getPlayer());
        if (cp == null) return;
        cp.save();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player death = event.getEntity();
        Player killer = event.getEntity().getKiller();

        ClanPlayer d = SevenTX.INSTANCE.getPlayer(death);
        ClanPlayer k = SevenTX.INSTANCE.getPlayer(killer);

        if (d == null || k == null)
            return;

        if (d.getKillStreak() >= 3)
            Bukkit.broadcastMessage(ChatColor.GOLD + killer.getDisplayName() + ChatColor.GOLD + " has just ended " + death.getDisplayName() + "'s killstreak of " + ChatColor.LIGHT_PURPLE + d.getKillStreak());

        int points = k.getPoints();
        float formula = d.getKillStreak() / 2;
        int toAdd = Math.round(formula);
        k.setPoints(points + toAdd);
        killer.sendMessage(ChatColor.GOLD + "You have received " + ChatColor.LIGHT_PURPLE + toAdd + ChatColor.GOLD + " points for killing " + ChatColor.RED + death.getDisplayName());

        d.removeKillstreak();
        k.addKillStreak();

        d.addDeath();
        k.addKill();

        if (k.getClan() != null) {
            k.getClan().setPoints(k.getPoints() + toAdd);
            k.getClan().handleKill(killer);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (event.getEntity() instanceof Player) {
                Player p = (Player) event.getEntity();

                ClanPlayer p1 = SevenTX.INSTANCE.getPlayer(player);
                ClanPlayer p2 = SevenTX.INSTANCE.getPlayer(p);
                if (p1 == null || p2 == null) return;

                if (p1.getClan() != null && p2.getClan() != null) {
                    if (p1.getClan().equals(p2.getClan())) {
                        event.setCancelled(true);
                    }
                }
            } else if (event.getDamager() instanceof Projectile) {
                Projectile project = (Projectile) event.getDamager();

                if (project.getShooter() instanceof Player) {
                    Player shooter = (Player) project.getShooter();

                    ClanPlayer p1 = SevenTX.INSTANCE.getPlayer(player);
                    ClanPlayer p2 = SevenTX.INSTANCE.getPlayer(shooter);

                    if (p1 == null || p2 == null) return;

                    if (p1.getClan() != null && p2.getClan() != null) {
                        if (p1.getClan().equals(p2.getClan())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) return;

        Clan c = cp.getClan();
        if (c == null) {
            event.setFormat(ChatColor.GOLD + "[" + cp.getPoints() + "] " + PermissionsEx.getUser(player).getPrefix() + event.getMessage());
        } else {
            event.setFormat(c.getColor().getColor() + "[" + c.getName() + "] " + PermissionsEx.getUser(player).getPrefix() + event.getMessage());
        }
    }
}
