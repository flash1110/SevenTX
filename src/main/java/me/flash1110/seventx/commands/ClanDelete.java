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
public class ClanDelete implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can disband a clan");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.disband")) {
            player.sendMessage(ChatColor.RED + "You lack permission to disband a clan");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Fatal error, relog");
            return true;
        }

        if (player.hasPermission("seventx.disband.admin")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "You must specify a clan to disband");
                return true;
            }

            String clanname = args[0];
            Clan c1 = SevenTX.INSTANCE.getClan(clanname);
            if (c1 == null) {
                player.sendMessage(ChatColor.RED + "Clan cannot be found");
                return true;
            }

            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has disbanded the clan " + ChatColor.LIGHT_PURPLE + c1.getName());

            c1.disband();

            for (UUID id : c1.getMembers()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline())
                    p.sendMessage(ChatColor.GOLD + "Your clan has been disbanded");

                ClanPlayer p1 = SevenTX.INSTANCE.getPlayer(p);
                if (p1 == null) continue;
                p1.setClan(null);
            }
        }

        Clan clan = cp.getClan();
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "You do not have a clan to leave!");
            return true;
        }

        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of a clan to disband it");
            return true;
        }

        for (UUID id : clan.getMembers()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline())
                p.sendMessage(ChatColor.GOLD + "Your clan has been disbanded");

            ClanPlayer p1 = SevenTX.INSTANCE.getPlayer(p);
            if (p1 == null) continue;
            p1.setClan(null);
        }

        clan.disband();
        cp.setClan(null);

        player.sendMessage(ChatColor.GOLD + "Your clan has been disbanded");


        return true;
    }
}
