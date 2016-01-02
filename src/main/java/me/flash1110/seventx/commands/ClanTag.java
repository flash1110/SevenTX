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
import java.util.regex.Pattern;

/**
 * Created by sandleraj on 1/2/16.
 */
public class ClanTag implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick players from a clan");
            return true;
        }


        if (sender.hasPermission("seventx.tag.admin")) {
            Player player = (Player) sender;
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "You need to specify the clan and a name");
                return true;
            }

            String clan = args[0];
            String name = args[1];

            Clan c = SevenTX.INSTANCE.getClan(clan);
            if (c == null) {
                sender.sendMessage(ChatColor.RED + "There is no clan by that name");
                return true;
            }

            if (isValid(name)) {
                Clan test = SevenTX.INSTANCE.getClan(name);
                if (test != null) {
                    sender.sendMessage(ChatColor.RED + "A clan with that name already exists");
                    return true;
                }
                String old = c.getName();
                c.setName(name);
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has changed the name of " + ChatColor.RED + old + ChatColor.GOLD + " to " + ChatColor.RED + WordUtils.capitalizeFully(name));
                player.sendMessage(ChatColor.GREEN + "Successfully changed the clan's name");
                Player p1 = Bukkit.getPlayer(c.getLeader());
                if (p1 != null && p1.isOnline())
                    p1.sendMessage(ChatColor.WHITE + player.getDisplayName() + ChatColor.GOLD + " has changed the name of your clan to " + ChatColor.RED + WordUtils.capitalizeFully(name));
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Your name has some illegal characters in it. You can only use letters and numbers.");
                return true;
            }

        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.tag")) {
            player.sendMessage(ChatColor.RED + "You lack permission to change the tag of a clan");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Relog, fatal error");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "You need to specify the name for your clan");
            return true;
        }

        Clan change = cp.getClan();
        if (change == null) {
            sender.sendMessage(ChatColor.RED + "You first need a clan to change the name of it");
            return true;
        }

        if (!(player.getUniqueId() == change.getLeader())) {
            sender.sendMessage(ChatColor.RED + "You need to be the leader of your clan to change the name of it");
            return true;
        }

        String n = args[0];

        if (isValid(n)) {
            Clan exists = SevenTX.INSTANCE.getClan(n);
            if (exists != null) {
                sender.sendMessage(ChatColor.RED + "A clan with that name already exists");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "Successfully changed the name of your clan to " + n);

            for (UUID uuid : change.getMembers()) {
                Player p2 = Bukkit.getPlayer(uuid);
                if (p2 != null && p2.isOnline())
                    p2.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GOLD + " has changed the clan's name to " + ChatColor.RED + WordUtils.capitalizeFully(n));
            }

            change.setName(n);
            return true;

        } else {
            sender.sendMessage(ChatColor.RED + "Your name has some illegal characters in it. You can only use letters and numbers");
            return true;
        }
    }

    public boolean isValid(String name) {
        if (Pattern.matches("[A-Za-z0-9]*", name))
            return true;
        return false;
    }
}
