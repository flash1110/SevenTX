package me.flash1110.seventx.listeners;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by sandleraj on 1/2/16.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GOLD + "Contacting the SQL servers to load your data...");
        Clan clan = SevenTX.INSTANCE.loadClan(event.getPlayer());
        if (clan != null)
        SevenTX.INSTANCE.updateClan(clan.getName(), clan);

        ClanPlayer player = SevenTX.INSTANCE.loadPlayer(event.getPlayer());
        if (player != null)
        SevenTX.INSTANCE.updatePlayer(event.getPlayer().getUniqueId(), player);

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
