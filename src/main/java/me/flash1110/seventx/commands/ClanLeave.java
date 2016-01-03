package me.flash1110.seventx.commands;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by sandleraj on 1/2/16.
 */
public class ClanLeave implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can leave a clan");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.leave")) {
            player.sendMessage(ChatColor.RED + "You lack permission to leave a clan");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Fatal error, relog");
            return true;
        }

        Clan clan = cp.getClan();
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "You do not have a clan to leave!");
            return true;
        }

        if (clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must disband the clan to leave it");
            return true;
        }

        clan.removeMember(player.getUniqueId());

        player.sendMessage(ChatColor.GOLD + "You have successfully left your clan");

        Player leader = Bukkit.getPlayer(clan.getLeader());
        if (leader != null && leader.isOnline())
            leader.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has left your clan");

        for (UUID uuid : clan.getMembers()) {
            Player to = Bukkit.getPlayer(uuid);
            if (to != null && to.isOnline())
                to.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has left your clan");
        }

        clan.handleLeave(player);

        return true;
    }
}
