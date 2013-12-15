package com.kleptotech;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Moderator {

	private Player player;
	private boolean occupied;
	private long startTime;
	private long totalWatch;
	private boolean onDuty;
	private boolean onBreak;
	private Location location;
	private boolean responded;
	private Player client;
	private int completedRequests;
	private String ticket;
	
	public Moderator(Player player){
		this.player = player;
		occupied = false;
		onBreak = false;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getTotalWatch() {
		return totalWatch;
	}

	public void setTotalWatch(long totalWatch) {
		this.totalWatch = totalWatch;
	}

	public boolean isOnDuty() {
		return onDuty;
	}

	public void setOnDuty(boolean onDuty) {
		this.onDuty = onDuty;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean hasResponded() {
		return responded;
	}

	public void setResponded(boolean responded) {
		this.responded = responded;
	}

	public Player getClient() {
		return client;
	}

	public void setClient(Player client) {
		this.client = client;
	}

	public int getCompletedRequests() {
		return completedRequests;
	}

	public void setCompletedRequests(int completedRequests) {
		this.completedRequests = completedRequests;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public boolean isOnBreak() {
		return onBreak;
	}

	public void setOnBreak(boolean onBreak) {
		this.onBreak = onBreak;
	}
}
