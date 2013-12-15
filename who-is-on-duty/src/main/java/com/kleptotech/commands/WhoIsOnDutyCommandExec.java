package com.kleptotech.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kleptotech.Moderator;
import com.kleptotech.WhoIsOnDuty;
import com.kleptotech.tasks.CloseTicketReminder;
import com.kleptotech.tasks.RemoveScoreboard;
import com.kleptotech.tasks.RequestCooldown;
import com.kleptotech.tasks.SetAvailable;
import com.kleptotech.tasks.UnGod;

public class WhoIsOnDutyCommandExec implements CommandExecutor{
	
	private WhoIsOnDuty plugin;
	 
	public WhoIsOnDutyCommandExec(WhoIsOnDuty plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("wiod")) {
			
			if(args.length < 1) return false;
			
			else if(args.length > 1){
				if(args[0].equalsIgnoreCase("descr")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;

						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							String descr = "";
							for(int i = 1; i<args.length; i++){
								descr += args[i];
								descr += " ";
							}
							plugin.getMetadataDao().setDescr(player.getName(), descr);
							player.sendMessage(ChatColor.GREEN + "Your description has been set to:");
							player.sendMessage(descr);
						} else {
							player.sendMessage("Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("getticket")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							int ticketNumber = 0;
							try{
								ticketNumber = Integer.parseInt(args[1]);
							}
							catch(NumberFormatException e){
								player.sendMessage(ChatColor.RED + "The ticket number you specified is not valid.");
								return true;
							}
							if(plugin.getTickets().size() == 0){
								player.sendMessage(ChatColor.GREEN + "There are no open tickets at this time.");
								return true;
							}
							else{
								try{
									String ticket = plugin.getTickets().get(ticketNumber-1);
									moderator.setTicket(ticket);
									//Begin ugly String manipulation
									String[] ticketParts = new String[3];
									int index =	ticket.indexOf('|');
									ticketParts[0] = ticket.substring(0, index);
									ticket = ticket.substring(index+1);
									index =	ticket.indexOf('|');
									ticketParts[1] = ticket.substring(0, index);
									ticketParts[2] = ticket.substring(index+1);
										
									player.sendMessage(ticketParts[1] + ChatColor.GREEN + " created this ticket with description:");
									player.sendMessage(ChatColor.GREEN + ticketParts[2]);
								}
								catch(Exception e){
									e.printStackTrace();
									player.sendMessage(ChatColor.RED + "Error Occurred. Ticket might no longer be available or something worse. Go yell at Klepto.");
								}
								
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("handlePrefixes")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;

						if (player.hasPermission("whoisonduty.admin")) {
							String arg = args[1];
							Boolean value = Boolean.parseBoolean(arg);
							plugin.getMetadataDao().setHandlePrefixes(value);
							player.sendMessage(ChatColor.GREEN + "HandlePrefixes has been set to: " + value.toString());
						} else {
							player.sendMessage("Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
			}
			
			else if(args.length == 1){
				if(args[0].equalsIgnoreCase("getticket")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(plugin.getTickets().size() == 0){
								player.sendMessage(ChatColor.GREEN + "There are no open tickets at this time.");
								return true;
							}
							else{
								try{
									String ticket = plugin.getTickets().get(0);
									moderator.setTicket(ticket);
									//Begin ugly String manipulation
									String[] ticketParts = new String[3];
									int index =	ticket.indexOf('|');
									ticketParts[0] = ticket.substring(0, index);
									ticket = ticket.substring(index+1);
									index =	ticket.indexOf('|');
									ticketParts[1] = ticket.substring(0, index);
									ticketParts[2] = ticket.substring(index+1);
										
									player.sendMessage(ticketParts[1] + ChatColor.GREEN + " created this ticket with description:");
									player.sendMessage(ChatColor.DARK_PURPLE + ticketParts[2]);
								}
								catch(Exception e){
									e.printStackTrace();
									player.sendMessage(ChatColor.RED + "Error Occurred. Ticket might no longer be available or something worse. Go yell at Klepto.");
								}
								
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("closeticket")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(moderator.getTicket() == null){
								player.sendMessage(ChatColor.RED + "You have no open ticket to close.");
								return true;
							}
							else{
								plugin.getTickets().remove(moderator.getTicket());
								moderator.setTicket(null);
								moderator.setCompletedRequests(moderator.getCompletedRequests() + 1);
								player.sendMessage(ChatColor.GREEN + "Ticket Closed!");
								
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("tickets")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(plugin.getTickets().size() == 0){
								player.sendMessage(ChatColor.GREEN + "No Open Tickets.");
								return true;
							}
							else{
								player.sendMessage(ChatColor.GREEN + "Open Tickets:");
								for(int i = 0; i < plugin.getTickets().size(); i++){
									String ticket = plugin.getTickets().get(i);
									moderator.setTicket(ticket);
									//Begin ugly String manipulation
									String[] ticketParts = new String[3];
									int index =	ticket.indexOf('|');
									ticketParts[0] = ticket.substring(0, index);
									ticket = ticket.substring(index+1);
									index =	ticket.indexOf('|');
									ticketParts[1] = ticket.substring(0, index);
									ticketParts[2] = ticket.substring(index+1);
									player.sendMessage(ChatColor.GREEN + Integer.toString(i+1) + ". " + ticketParts[1] + 
											ChatColor.DARK_PURPLE + " - " + ticketParts[2]);
								}
								
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("onduty")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(moderator.isOnDuty()){
								player.sendMessage(ChatColor.GREEN + "You are already on duty!");
								return true;
							}
							else{
								moderator.setOnDuty(true);
								moderator.setStartTime(System.currentTimeMillis());
								player.sendMessage(ChatColor.GREEN + "You are ON Duty!");
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				
				else if(args[0].equalsIgnoreCase("offduty")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(!moderator.isOnDuty()){
								player.sendMessage(ChatColor.GREEN + "You are already off duty!");
								return true;
							}
							else if(moderator.isOccupied()){
								player.sendMessage(ChatColor.GREEN + "You need to finish handling your current request first!");
								return true;
							}
							else{
								moderator.setOnDuty(false);
								moderator.setOnBreak(false);
								long startTime = moderator.getStartTime();
								long stopTime = System.currentTimeMillis();
								long watch = stopTime - startTime;
								long totalWatch = moderator.getTotalWatch() + watch;
								plugin.getMetadataDao().setTotalWatch(moderator.getPlayer().getName(), totalWatch);
								
								player.sendMessage(ChatColor.GREEN + "You are OFF Duty! Your watch lasted " + watch/60000 + " minutes.");
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("accept")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(!moderator.isOnDuty()){
								player.sendMessage(ChatColor.GREEN + "You aren't on Duty!");
								return true;
							}
							else if(moderator.getClient() == null){
								player.sendMessage(ChatColor.GREEN + "You have no current requests to accept!");
								return true;
							}
							else{
								moderator.setResponded(true);
								moderator.setOccupied(true);
								moderator.setLocation(player.getLocation());
								plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "god " + moderator.getPlayer().getName());
								new UnGod(moderator.getPlayer().getName(), plugin).runTaskLater(plugin, 400);
								player.teleport(moderator.getClient().getLocation());
								player.getWorld().createExplosion(player.getLocation(), 0);
								player.performCommand("msg " + moderator.getClient().getName() + " Hey " +
										moderator.getClient().getDisplayName() + "! How can I help you?");
								new CloseTicketReminder(plugin, moderator).runTaskLater(plugin, 6000);
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("done")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
						
						Moderator moderator = plugin.getModerators().get(player.getName());
	
						if (player.hasPermission("whoisonduty.mod") || player.hasPermission("whoisonduty.admin")) {
							if(!moderator.isOnDuty()){
								player.sendMessage(ChatColor.GREEN + "You aren't on Duty!");
								return true;
							}
							else if(!moderator.isOccupied() || moderator.getClient() == null){
								player.sendMessage(ChatColor.GREEN + "You have no current request!");
								return true;
							}
							else{
								player.getWorld().createExplosion(player.getLocation(), 0);
								new RequestCooldown(moderator.getClient(), plugin).runTaskLater(plugin, 6000);
								player.performCommand("msg " + moderator.getClient().getName() + " Farewell " +
										moderator.getClient().getDisplayName() + "! May your pick always strike diamond! *Poof*");
								player.teleport(moderator.getLocation());
								moderator.setClient(null);
								moderator.setCompletedRequests(moderator.getCompletedRequests() + 1);
								moderator.setOccupied(false);
								
								if(plugin.getModerators().size() == 1){
									return true;
								}
								else{
									int count = 0;
									for(Moderator mod : plugin.getModerators().values()){
										if(moderator.equals(mod) || !mod.isOnDuty()) continue;
										
										long startTime = moderator.getStartTime();
										long stopTime = System.currentTimeMillis();
										long watch = stopTime - startTime;
										long totalWatch = moderator.getTotalWatch() + watch;
										
										startTime = mod.getStartTime();
										stopTime = System.currentTimeMillis();
										watch = stopTime - startTime;
										long totalWatch2 = mod.getTotalWatch() + watch;
										
										if(totalWatch > totalWatch2) count++;
									}
									if(count == 0){
										return true;
									}
									count = count * 20 * 60;
									moderator.setOnBreak(true);
									new SetAvailable(moderator).runTaskLater(plugin, count);
								}
								return true;
							}
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("time")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be run by a player.");
					} else {
						Player player = (Player) sender;
				
						if (player.hasPermission("whoisonduty.admin")) {
							plugin.getTotalWatchScoreboard().refreshBoard();
							player.setScoreboard(plugin.getTotalWatchScoreboard().getTotalWatchBoard());
							new RemoveScoreboard(player, plugin).runTaskLater(plugin, 200);
						} else {
							player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
						}
					}
					return true;
				}
				
			}
			
			else if(args[0].equalsIgnoreCase("count")){
				if (!(sender instanceof Player)) {
					sender.sendMessage("This command can only be run by a player.");
				} else {
					Player player = (Player) sender;
			
					if (player.hasPermission("whoisonduty.admin")) {
						plugin.getCompletedRequestsScoreboard().refreshBoard();
						player.setScoreboard(plugin.getCompletedRequestsScoreboard().getCompletedRequestsBoard());
						new RemoveScoreboard(player, plugin).runTaskLater(plugin, 200);
					} else {
						player.sendMessage(ChatColor.GREEN + "Silly noob. You don't have rights to that.");
					}
				}
				return true;
			}
		}
		
		//Default
		return false;
		
	}
}
