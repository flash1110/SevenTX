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

/**
 * Created by sandleraj on 1/3/16.
 */
public class StatCommand implements CommandExecutor {

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

        if (args.length == 0) {

            ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
            if (cp == null) {
                player.kickPlayer("Fatal error, relog");
                return true;
            }

            Clan clan = cp.getClan();

            sender.sendMessage(ChatColor.GOLD + player.getDisplayName() + "'s stats");
            sender.sendMessage(ChatColor.GOLD + "Points: " + ChatColor.LIGHT_PURPLE + cp.getPoints());
            sender.sendMessage(ChatColor.GOLD + "Kills: " + ChatColor.LIGHT_PURPLE + cp.getKills());
            sender.sendMessage(ChatColor.GOLD + "Deaths: " + ChatColor.LIGHT_PURPLE + cp.getDeaths());
            sender.sendMessage(ChatColor.GOLD + "KDR (Kill Death Ratio): " + ChatColor.LIGHT_PURPLE + cp.getKills() / cp.getDeaths());
            sender.sendMessage(ChatColor.GOLD + "Clan: " + ChatColor.LIGHT_PURPLE + (clan == null ? "none" : clan.getName()));
            sender.sendMessage(ChatColor.GOLD + "Current Killstreak: " + ChatColor.LIGHT_PURPLE + cp.getKillStreak());
            sender.sendMessage(ChatColor.GOLD + "Highest Killstreak " + ChatColor.LIGHT_PURPLE + cp.getHighestKillstreak());
        } else {
            String name = args[0];
            Player p = Bukkit.getPlayer(name);
            if (p == null || !p.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Cannot find player, they may be offline");
                return true;
            }

            ClanPlayer cp = SevenTX.INSTANCE.getPlayer(p);
            if (cp == null) {
                player.kickPlayer("Fatal error, relog");
                return true;
            }

            Clan clan = cp.getClan();

            sender.sendMessage(ChatColor.GOLD + p.getDisplayName() + "'s stats");
            sender.sendMessage(ChatColor.GOLD + "Points: " + ChatColor.LIGHT_PURPLE + cp.getPoints());
            sender.sendMessage(ChatColor.GOLD + "Kills: " + ChatColor.LIGHT_PURPLE + cp.getKills());
            sender.sendMessage(ChatColor.GOLD + "Deaths: " + ChatColor.LIGHT_PURPLE + cp.getDeaths());
            sender.sendMessage(ChatColor.GOLD + "KDR (Kill Death Ratio): " + ChatColor.LIGHT_PURPLE + cp.getKills() / cp.getDeaths());
            sender.sendMessage(ChatColor.GOLD + "Clan: " + ChatColor.LIGHT_PURPLE + (clan == null ? "none" : clan.getName()));
            sender.sendMessage(ChatColor.GOLD + "Current Killstreak: " + ChatColor.LIGHT_PURPLE + cp.getKillStreak());
            sender.sendMessage(ChatColor.GOLD + "Highest Killstreak " + ChatColor.LIGHT_PURPLE + cp.getHighestKillstreak());
        }


        return true;
    }
}
