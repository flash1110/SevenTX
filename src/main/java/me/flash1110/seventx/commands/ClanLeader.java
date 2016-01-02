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
public class ClanLeader implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can leave a clan");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.leader")) {
            player.sendMessage(ChatColor.RED + "You lack permission to leader of a clan");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You must specify a player to make leader");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Fatal error, relog");
            return true;
        }

        if (player.hasPermission("seventx.leader.admin")) {
            if (args.length <= 1) {
                player.sendMessage(ChatColor.RED + "You must specify a player and a clan");
                return true;
            }
            String clanname = args[0];
            String p = args[1];

            Clan c = SevenTX.INSTANCE.getClan(clanname);
            if (c == null) {
                player.sendMessage(ChatColor.RED + "That clan does not exist");
                return true;
            }

            Player leader = Bukkit.getPlayer(p);
            if (leader == null || !leader.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player does not exist or is not online");
                return true;
            }

            if (c.getLeader().equals(leader.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Player is already the leader");
                return true;
            }

            if (!c.getMembers().contains(leader.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Player must be a member of the clan");
                return true;
            }

            Player oldLeader = Bukkit.getPlayer(c.getLeader());

            c.setLeader(leader.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "Successfully made" + ChatColor.LIGHT_PURPLE + leader.getDisplayName() + ChatColor.GOLD + " leader of " + ChatColor.RED + c.getName());

            if (oldLeader != null && oldLeader.isOnline())
                oldLeader.sendMessage(ChatColor.RED + "You are no longer leader of your clan");

            for (UUID uuid : c.getMembers()) {
                Player send = Bukkit.getPlayer(uuid);
                if (send != null && send.isOnline()) {
                    send.sendMessage(ChatColor.LIGHT_PURPLE + leader.getDisplayName() + ChatColor.GOLD + " is now the leader of your clan");
                }
            }
            return true;
        }

        Clan clan = cp.getClan();
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "You do not have a clan to make someone else a leader of!");
            return true;
        }

        String playerto = args[0];
        // TODO: Add leader for regular players

        Player three = Bukkit.getPlayer(playerto);
        if (three == null || !three.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player cannot be found or is not online");
            return true;
        }

        Player oldLeader = Bukkit.getPlayer(clan.getLeader());

        clan.setLeader(three.getUniqueId());
        player.sendMessage(ChatColor.GOLD + "Successfully made" + ChatColor.LIGHT_PURPLE + three.getDisplayName() + ChatColor.GOLD + " leader of " + ChatColor.RED + clan.getName());

        if (oldLeader != null && oldLeader.isOnline())
            oldLeader.sendMessage(ChatColor.RED + "You are no longer leader of your clan");

        for (UUID uuid : clan.getMembers()) {
            Player send = Bukkit.getPlayer(uuid);
            if (send != null && send.isOnline()) {
                send.sendMessage(ChatColor.LIGHT_PURPLE + three.getDisplayName() + ChatColor.GOLD + " is now the leader of your clan");
            }
        }
        return true;
    }
}
