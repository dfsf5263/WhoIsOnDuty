package com.kleptotech.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.WhoIsOnDuty;

public class RequestCooldown extends BukkitRunnable {
	 
    private Player client;
    private WhoIsOnDuty plugin;
 
    public RequestCooldown(Player client, WhoIsOnDuty plugin) {
        this.client = client;
        this.plugin = plugin;
    }
 
    public void run() {
        plugin.getRequestees().put(client.getName(), true);
    }
}
 
