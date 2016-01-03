package me.flash1110.seventx.listeners;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sandleraj on 1/2/16.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GOLD + "Contacting the SQL servers to load your data...");

        Clan clan;
        clan = SevenTX.INSTANCE.loadClan(event.getPlayer());
        if (clan == null) {
            clan = new Clan(event.getPlayer().getUniqueId(), "default", "WHITE", 0, new ArrayList<UUID>());
        }

        if (clan != null && !clan.getName().equals("default")) {
            SevenTX.INSTANCE.updateClan(clan.getName(), clan);
            Bukkit.broadcastMessage("Not null, clan loaded2");
        }

        event.getPlayer().sendMessage("sen");

        ClanPlayer player = SevenTX.INSTANCE.loadPlayer(event.getPlayer());
        if (player != null) {
            SevenTX.INSTANCE.updatePlayer(event.getPlayer().getUniqueId(), player);
            Bukkit.broadcastMessage("Not null, Player loaded2");
        }

        if (SevenTX.INSTANCE.getPlayer(event.getPlayer().getUniqueId()) != null) {
            Bukkit.broadcastMessage("Not null, Player loaded1");
        }

        if (clan.getName() == null) return;

        if (SevenTX.INSTANCE.getClan(clan.getName()) != null) {
            Bukkit.broadcastMessage("clan loaded1");
        }

        event.getPlayer().sendMessage(ChatColor.GREEN + "...Successfully loaded your data");
        event.getPlayer().sendMessage(ChatColor.GREEN + "...Successfully loaded your clan data!");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(event.getPlayer());
        if (cp == null) return;
        cp.save();
    }
}
