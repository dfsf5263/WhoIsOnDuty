package com.kleptotech.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kleptotech.Moderator;
import com.kleptotech.WhoIsOnDuty;
import com.kleptotech.tasks.RequestCooldown;

public class TicketCommandExec implements CommandExecutor{
	
	private WhoIsOnDuty plugin;
	 
	public TicketCommandExec(WhoIsOnDuty plugin) {
		this.plugin = plugin;
	}
 
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ticket")){
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				Player player = (Player) sender;
				
				String descr = "";
				for(int i = 0; i<args.length; i++){
					descr += args[i];
					descr += " ";
				}
				descr = descr.replace('|', '*');
				player.sendMessage(ChatColor.GREEN + "Your ticket has been recorded with a description of:");
				player.sendMessage(descr);
				
				plugin.getRequestees().put(player.getName(), false);
				new RequestCooldown(player, plugin).runTaskLater(plugin, 6000);

				plugin.getTickets().add(player.getName() + '|' + player.getDisplayName() + '|' + descr);
				
				for(Moderator mod : plugin.getModerators().values()){
					mod.getPlayer().sendMessage(ChatColor.RED + "Attention moderators! " + 
							player.getDisplayName() + ChatColor.RED + " has just created a moderator request.");
					mod.getPlayer().sendMessage(ChatColor.RED + "Problem Description: " + ChatColor.DARK_PURPLE + descr);
				}
			}
			return true;
		}
		
		//Default
		return false;
		
	}

		
}
