package me.flash1110.seventx.commands;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import org.apache.commons.lang.WordUtils;
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
public class ClanJoin implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can join a clan");
            return true;
        }

        Player player = (Player) sender;


        if (!player.hasPermission("seventx.join")) {
            player.sendMessage(ChatColor.RED + "You lack permission to join a clan");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You need to specify the clan to join");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Fatal error, relog");
            return true;
        }

        if (player.hasPermission("seventx.join.admin")) {
            Clan c = SevenTX.INSTANCE.getClan(args[0]);

            if (c == null) {
                player.sendMessage(ChatColor.GOLD + "That clan does not exist");
                return true;
            }

            if (cp.getClan() != null) {
                player.sendMessage(ChatColor.GOLD + "You are already in a clan");
                return true;
            }

           if (c.addMember(player.getUniqueId())) {
               player.sendMessage(ChatColor.GOLD + "You have successfully joined " + ChatColor.LIGHT_PURPLE + WordUtils.capitalizeFully(args[0]));

               Player p = Bukkit.getPlayer(c.getLeader());
               if (p != null && p.isOnline())
                   p.sendMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.GOLD + " has force joined your clan");

               for (UUID uuid : c.getMembers()) {
                   Player mes = Bukkit.getPlayer(uuid);
                   if (mes != null && mes.isOnline()) {
                       mes.sendMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.GOLD + " has force joined your clan");
                   }
               }
           } else {
               player.sendMessage(ChatColor.GOLD + "There are already the max amount of players in that clan");
           }

            return true;
        }

        Clan c = SevenTX.INSTANCE.getClan(args[0]);

        if (c == null) {
            player.sendMessage(ChatColor.GOLD + "That clan does not exist");
            return true;
        }

        if (cp.getClan() != null) {
            player.sendMessage(ChatColor.GOLD + "You are already in a clan");
            return true;
        }

        if (!c.getInvites().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have not beeen invited into the clan");
            return true;
        }

        if (c.addMember(player.getUniqueId())) {
            player.sendMessage(ChatColor.GOLD + "You have successfully joined " + ChatColor.LIGHT_PURPLE + WordUtils.capitalizeFully(args[0]));
            c.handleJoin(player);

            Player p = Bukkit.getPlayer(c.getLeader());
            if (p != null && p.isOnline())
                p.sendMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.GOLD + " has joined your clan");

            for (UUID uuid : c.getMembers()) {
                Player mes = Bukkit.getPlayer(uuid);
                if (mes != null && mes.isOnline()) {
                    mes.sendMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.GOLD + " has joined your clan");
                }

            }


        } else {
            player.sendMessage(ChatColor.GOLD + "There are already the max amount of players in that clan");
            return true;
        }

        return true;
    }
}
