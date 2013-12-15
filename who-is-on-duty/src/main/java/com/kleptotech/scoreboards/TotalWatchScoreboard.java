package com.kleptotech.scoreboards;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.kleptotech.Moderator;
import com.kleptotech.WhoIsOnDuty;

public class TotalWatchScoreboard {
	
	private ScoreboardManager manager;
	private Scoreboard totalWatchBoard;
	private Objective totalWatch;
	private WhoIsOnDuty plugin;

	public TotalWatchScoreboard(WhoIsOnDuty plugin){
		this.plugin = plugin;
		manager = plugin.getServer().getScoreboardManager();
		totalWatchBoard = manager.getNewScoreboard();
		totalWatch = totalWatchBoard.registerNewObjective("totalWatch", "dummy");
		totalWatch.setDisplayName(ChatColor.DARK_RED + "Total Time (Hours)");
		totalWatch.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void refreshBoard(){
		List<String> offlineMods = plugin.getConfig().getStringList("mods");
		
		for(Moderator mod : plugin.getModerators().values()){
			long startTime = mod.getStartTime();
			long stopTime = System.currentTimeMillis();
			long watch = stopTime - startTime;
			int totalWatchTime = (int) Math.floor((mod.getTotalWatch() + watch)/3600000);
			
			Score score = totalWatch.getScore(plugin.getServer().getOfflinePlayer(mod.getPlayer().getName()));
			score.setScore(totalWatchTime);
			offlineMods.remove(mod.getPlayer().getName());
		}
		
		for(String mod : offlineMods){
			long time = plugin.getMetadataDao().getTotalWatch(mod);
			int totalWatchTime = (int) Math.floor((time)/3600000);
			Score score = totalWatch.getScore(plugin.getServer().getOfflinePlayer(mod));
			score.setScore(totalWatchTime);
		}
	}

	public Scoreboard getTotalWatchBoard() {
		return totalWatchBoard;
	}

	public void setTotalWatchBoard(Scoreboard totalWatchBoard) {
		this.totalWatchBoard = totalWatchBoard;
	}
}
