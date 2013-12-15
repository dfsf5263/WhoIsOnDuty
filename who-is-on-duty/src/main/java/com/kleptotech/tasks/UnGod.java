package com.kleptotech.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.WhoIsOnDuty;

public class UnGod extends BukkitRunnable {
	 
    private String client;
    private WhoIsOnDuty plugin;
 
    public UnGod(String client, WhoIsOnDuty plugin) {
        this.client = client;
        this.plugin = plugin;
    }
 
    public void run() {
    	plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "god " + client);
    }
}
 
