package com.kleptotech.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kleptotech.WhoIsOnDuty;

public class ModsCommandExec implements CommandExecutor{
	
	private WhoIsOnDuty plugin;
	 
	public ModsCommandExec(WhoIsOnDuty plugin) {
		this.plugin = plugin;
	}
 
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mods")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				Player player = (Player) sender;
				if(!plugin.getRequestees().get(player.getName())){
					player.sendMessage(ChatColor.DARK_PURPLE + "You must wait awhile until making another moderator request.");
					return true;
				}
				plugin.getMenu().requestMod(player);
				return true;
			}
		}
		
		//Default
		return false;
		
	}

		
}
