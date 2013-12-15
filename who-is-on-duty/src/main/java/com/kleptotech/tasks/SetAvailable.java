package com.kleptotech.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.kleptotech.Moderator;

public class SetAvailable extends BukkitRunnable {
	 
    private final Moderator moderator;
 
    public SetAvailable(Moderator moderator) {
        this.moderator = moderator;
    }
 
    public void run() {
        moderator.setOnBreak(false);
    }
}
 
