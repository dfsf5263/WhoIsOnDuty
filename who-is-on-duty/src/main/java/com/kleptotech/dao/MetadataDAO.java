package com.kleptotech.dao;

import com.kleptotech.WhoIsOnDuty;


public class MetadataDAO {
	
	private WhoIsOnDuty plugin;
	
	public MetadataDAO(WhoIsOnDuty plugin){
		this.plugin = plugin;
	}
	
	public void setDescr(String playerName, String value) {
		plugin.getConfig().set("mod.descr." + playerName, value);
	}
	
	public void setTotalWatch(String playerName, long time) {
		plugin.getConfig().set("mod.totalWatch." + playerName, time);
	}
	
	public void setCompletedRequests(String playerName, long count) {
		plugin.getConfig().set("mod.completedRequests." + playerName, count);
	}
	
	public void setHandlePrefixes(Boolean value) {
		plugin.getConfig().set("handlePrefixes", value);
	}
	
	public String getDescr(String playerName) {
		String rank = plugin.getConfig().getString("mod.descr." + playerName);
		if(rank != null) return rank;
		return "";
	}
	
	public long getTotalWatch(String playerName) {
		return plugin.getConfig().getLong("mod.totalWatch." + playerName);
	}
	
	public int getCompletedRequests(String playerName) {
		return plugin.getConfig().getInt("mod.completedRequests." + playerName);
	}
	
	public boolean getHandlePrefixes(){
		return plugin.getConfig().getBoolean("handlePrefixes", false);
	}
}
