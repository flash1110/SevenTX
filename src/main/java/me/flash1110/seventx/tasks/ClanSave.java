package me.flash1110.seventx.tasks;

import me.flash1110.seventx.SevenTX;
import me.flash1110.seventx.objects.Clan;
import me.flash1110.seventx.objects.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * Created by sandleraj on 1/2/16.
 */
public class ClanSave implements Runnable {

 /*   public static void startDelayedTasks() {

        Bukkit.getScheduler().scheduleSyncDelayedTask(SevenTX.INSTANCE, new Runnable() {

            @Override
            public void run() {
                try {

                    Bukkit.broadcastMessage(ChatColor.GOLD + "Attempting to save clan data...");

                    for (Clan clan : SevenTX.INSTANCE.getClans().values()) {
                        if (clan == null) continue;
                        clan.save();
                        Bukkit.broadcastMessage(ChatColor.GOLD + "Saved" + clan.getName());
                    }

                    Bukkit.broadcastMessage(ChatColor.GOLD + "... " + ChatColor.GREEN + "successfully " + ChatColor.GOLD + "saved clan data.");

                    SevenTX.INSTANCE.getLogger().log(Level.INFO, "Successfully saved clan data");

                    startDelayedTasks();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 6000L);
    } */

    @Override
    public void run() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SevenTX.INSTANCE, new Runnable() {

            @Override
            public void run() {
                try {

                    Bukkit.broadcastMessage(ChatColor.GOLD + "Attempting to save clan data...");

                    for (Clan clan : SevenTX.INSTANCE.getClans().values()) {
                        if (clan == null) continue;
                        clan.save();
                        Bukkit.broadcastMessage(ChatColor.GOLD + "Saved" + clan.getName());
                    }

                    for (ClanPlayer cp : SevenTX.INSTANCE.getPlayers().values()) {
                        if (cp == null) continue;
                        cp.save();
                 //       Bukkit.broadcastMessage(ChatColor.GOLD + "Saved" + cp.getName());
                    }

                    Bukkit.broadcastMessage(ChatColor.GOLD + "... " + ChatColor.GREEN + "successfully " + ChatColor.GOLD + "saved clan data.");

                    SevenTX.INSTANCE.getLogger().log(Level.INFO, "Successfully saved clan data");

                    run();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 6000L);
    }
}
