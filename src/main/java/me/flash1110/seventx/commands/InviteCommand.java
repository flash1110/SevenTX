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
public class InviteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can invite players to a clan");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.invite")) {
            player.sendMessage(ChatColor.RED + "You lack permission to invite members to a clan");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Fatal error, relog");
            return true;
        }

        Clan clan = cp.getClan();
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "You do not have a clan to invite people to!");
            return true;
        }

        if (!(clan.getLeader() == player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be leader of your clan to invite members");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You must specify a player to invite");
            return true;
        }

        String name = args[0];
        Player p = Bukkit.getPlayer(name);

        if (p == null || !p.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        if (!clan.getInvites().contains(p.getUniqueId())) {
            clan.addInvite(p.getUniqueId());
            p.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has invited you to the clan " + ChatColor.RED + clan.getName());
            player.sendMessage(ChatColor.GREEN + "You have invited " + ChatColor.GRAY + p.getDisplayName());

            for (UUID uuid : clan.getMembers()) {
                Player p1 = Bukkit.getPlayer(uuid);
                if (p1 != null && p.isOnline()) {
                    p1.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has invited " + ChatColor.RED + p.getDisplayName() + ChatColor.GOLD + " to the clan.");
                }
            }
        } else {
            clan.removeInvite(p.getUniqueId());
            p.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has uninvited you to the clan " + ChatColor.RED + clan.getName());
            player.sendMessage(ChatColor.GREEN + "You have uninvited " + ChatColor.GRAY + p.getDisplayName());

            for (UUID uuid : clan.getMembers()) {
                Player p1 = Bukkit.getPlayer(uuid);
                if (p1 != null && p.isOnline()) {
                    p1.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has uninvited " + ChatColor.RED + p.getDisplayName() + ChatColor.GOLD + " to the clan.");
                }
            }
        }
        return true;
    }
}
