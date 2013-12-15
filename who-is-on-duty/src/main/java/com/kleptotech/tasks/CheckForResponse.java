package com.kleptotech.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.Moderator;
import com.kleptotech.WhoIsOnDuty;

public class CheckForResponse extends BukkitRunnable {
	 
    private final WhoIsOnDuty plugin;
    private final Moderator moderator;
 
    public CheckForResponse(WhoIsOnDuty plugin, Moderator moderator) {
        this.plugin = plugin;
        this.moderator = moderator;
    }
 
    public void run() {
        if(!moderator.hasResponded()){
        	plugin.getRequestees().put(moderator.getClient().getName(), true);
        	moderator.getClient().getPlayer().sendMessage(ChatColor.GREEN + "We're sorry. " + moderator.getPlayer().getDisplayName() + ChatColor.GREEN +
        			" is unable to assist you at this time.");
        	moderator.getPlayer().sendMessage(ChatColor.RED + "You have been set off duty for not responding to a request within the 15 second timeframe.");
        	moderator.setOnDuty(false);
        	moderator.setOccupied(false);
        	moderator.setClient(null);
        	long startTime = moderator.getStartTime();
			long stopTime = System.currentTimeMillis();
			long watch = stopTime - startTime;
			long totalWatch = moderator.getTotalWatch() + watch;
			plugin.getMetadataDao().setTotalWatch(moderator.getPlayer().getName(), totalWatch);
        }
    }
}
 
