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
public class KickInvite implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick players from a clan");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.invite")) {
            player.sendMessage(ChatColor.RED + "You lack permission to invite players to a clan");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Relog, fatal error");
            return true;
        }

        Clan clan = cp.getClan();
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "You don't have a clan! Create one to be able to kick players");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You must specify someone to kick");
            return true;
        }

        String name = args[0];

        Player p = Bukkit.getPlayer(name);
        if (p == null || !p.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        ClanPlayer cp1 = SevenTX.INSTANCE.getPlayer(p);
        if (cp1 == null) {
            p.kickPlayer("Relog, fatal error");
            return true;
        }

        Clan clan1 = cp1.getClan();

        if (clan != clan1) {
            player.sendMessage(ChatColor.RED + "You must be in the same clan as the player you are trying to kick");
            return true;
        }

        if (clan.getLeader() != player.getUniqueId()) {
            player.sendMessage(ChatColor.RED + "You must be the leader of your clan to kick a player");
            return true;
        }

        clan.removeMember(p.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "Successfully kicked " + ChatColor.LIGHT_PURPLE + p.getDisplayName() + ChatColor.GOLD + " from the clan.");
        p.sendMessage(ChatColor.GOLD + "You have been kicked from the clan " + ChatColor.RED + clan.getName() + ChatColor.GOLD + " by " + ChatColor.LIGHT_PURPLE + player.getDisplayName());
        
        for (UUID uuid : clan.getMembers()) {
            Player p1 = Bukkit.getPlayer(uuid);
            if (p1 != null && p1.isOnline()) {
                p1.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " kicked " + ChatColor.RED + p.getDisplayName() + ChatColor.GOLD + " from the clan");
            }
        }

        return true;
    }
}
