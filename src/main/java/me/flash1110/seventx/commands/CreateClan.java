package me.flash1110.seventx.commands;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sandleraj on 1/2/16.
 */
public class CreateClan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create clans");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seventx.create")) {
            player.sendMessage(ChatColor.RED + "You lack permission to create a clan");
            return true;
        }

        ClanPlayer cp = SevenTX.INSTANCE.getPlayer(player);
        if (cp == null) {
            player.kickPlayer("Relog, fatal error");
            return true;
        }

        Clan clan = cp.getClan();
        if (clan != null) {
            player.sendMessage(ChatColor.RED + "You already have a clan! Leave it to create a new one");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You must specify a name for your clan");
            return true;
        }

        String name = args[0];

        if (SevenTX.INSTANCE.getClan(name) != null) {
            player.sendMessage(ChatColor.RED + "A clan with that name already exists");
            return true;
        }

        Clan c = new Clan(player.getUniqueId(), name, "WHITE", cp.getPoints(), new ArrayList<UUID>());
        c.save();
        player.sendMessage(ChatColor.GOLD + "Created clan titled " + ChatColor.RED + WordUtils.capitalizeFully(name));
        SevenTX.INSTANCE.updateClan(name, c);
        return true;
    }
}
