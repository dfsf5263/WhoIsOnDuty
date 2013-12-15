package com.kleptotech.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.WhoIsOnDuty;

public class RemoveScoreboard extends BukkitRunnable {
	 
    private Player player;
    private WhoIsOnDuty plugin;
 
    public RemoveScoreboard(Player player, WhoIsOnDuty plugin) {
        this.player = player;
        this.plugin = plugin;
    }
 
    public void run() {
        player.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
    }
}
 
