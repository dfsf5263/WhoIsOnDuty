package com.kleptotech.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.Moderator;
import com.kleptotech.WhoIsOnDuty;

public class CloseTicketReminder extends BukkitRunnable {
	 
    private WhoIsOnDuty plugin;
    private Moderator mod;
 
    public CloseTicketReminder(WhoIsOnDuty plugin, Moderator mod) {
        this.plugin = plugin;
        this.mod = mod;
    }
 
    public void run() {
        if(mod.isOccupied()){
        	mod.getPlayer().sendMessage(ChatColor.RED + "Make sure to close your open moderator requests with /wiod done");
        	new CloseTicketReminder(plugin, mod).runTaskLater(plugin, 6000);
        }
    }
}
 
