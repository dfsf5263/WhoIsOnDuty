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

public class CompletedRequestsScoreboard {
	
	private ScoreboardManager manager;
	private Scoreboard completedRequestsBoard;
	private Objective completedRequests;
	private WhoIsOnDuty plugin;

	public CompletedRequestsScoreboard(WhoIsOnDuty plugin){
		this.plugin = plugin;
		manager = plugin.getServer().getScoreboardManager();
		completedRequestsBoard = manager.getNewScoreboard();
		completedRequests = completedRequestsBoard.registerNewObjective("requestCount", "dummy");
		completedRequests.setDisplayName(ChatColor.DARK_RED + "Mod Request Count");
		completedRequests.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void refreshBoard(){
		List<String> offlineMods = plugin.getConfig().getStringList("mods");
		
		for(Moderator mod : plugin.getModerators().values()){
			Score score = completedRequests.getScore(plugin.getServer().getOfflinePlayer(mod.getPlayer().getName()));
			score.setScore(mod.getCompletedRequests());
			offlineMods.remove(mod.getPlayer().getName());
		}
		
		for(String mod : offlineMods){
			Score score = completedRequests.getScore(plugin.getServer().getOfflinePlayer(mod));
			score.setScore(plugin.getMetadataDao().getCompletedRequests(mod));
		}
	}

	public Scoreboard getCompletedRequestsBoard() {
		return completedRequestsBoard;
	}

	public void setCompletedRequestsBoard(Scoreboard completedRequestsBoard) {
		this.completedRequestsBoard = completedRequestsBoard;
	}
}
