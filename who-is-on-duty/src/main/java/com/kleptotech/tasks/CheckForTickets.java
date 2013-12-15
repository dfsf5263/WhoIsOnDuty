package com.kleptotech.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.Moderator;
import com.kleptotech.WhoIsOnDuty;

public class CheckForTickets extends BukkitRunnable {
	 
    private WhoIsOnDuty plugin;
 
    public CheckForTickets(WhoIsOnDuty plugin) {
        this.plugin = plugin;
    }
 
    public void run() {
        if(plugin.getTickets().size() > 0){
        	int size = plugin.getTickets().size();
        	for(Moderator mod : plugin.getModerators().values()){
        		mod.getPlayer().sendMessage(ChatColor.RED + "Attention Moderators! There are currently " + size +
        				" unresolved tickets.");
        	}
        }
        new CheckForTickets(plugin).runTaskLater(plugin, 72000);
    }
}
 
