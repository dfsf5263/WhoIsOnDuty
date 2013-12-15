package com.kleptotech;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kleptotech.tasks.CheckForResponse;

public class ModRequestMenu {
	private WhoIsOnDuty plugin;
	String onlineTitle, offlineTitle;

    public ModRequestMenu(WhoIsOnDuty plugin, String onlineTitle, String offlineTitle) {
        this.plugin = plugin;
        this.onlineTitle = onlineTitle;
        this.offlineTitle = offlineTitle;
    }
    
    public void handleModRequest(Player moderator, Player client){
    	if(moderator.equals(client)){
    		moderator.getPlayer().sendMessage(ChatColor.AQUA + "Silly...");
    		return;
    	}
    	plugin.getRequestees().put(client.getName(), false);
    	moderator.sendMessage(client.getDisplayName() + ChatColor.GREEN + " has requested your assistance! You have 2 minutes to accept (/wiod accept).");
    	client.sendMessage(moderator.getDisplayName() + ChatColor.GREEN + " has been notified. Please standby.");
    	
    	//Play Ding
    	Bukkit.getPlayer(moderator.getName()).getWorld().playSound(moderator.getLocation(),Sound.WITHER_DEATH,1, 0);
    	
		Moderator modObj = plugin.getModerators().get(moderator.getName());
		modObj.setResponded(false);
		modObj.setOccupied(true);
		modObj.setClient(client);
		
    	new CheckForResponse(plugin, modObj).runTaskLater(plugin, 2400);
    }

    public void requestMod(Player player) {
    	openInventory(player);
    }
    
    public void handleOfflineTicket(Player player){
    	player.sendMessage(ChatColor.GREEN + "We're sorry there are no moderators available to assist you at the moment.");
    	player.sendMessage(ChatColor.GREEN + "Please create a ticket using the command /ticket <Problem Description>");
    }
    
    public void openInventory(Player player) {
        
        ArrayList<Moderator> mods = new ArrayList<Moderator>();
        Inventory chest;
        
    	for(Moderator mod : plugin.getModerators().values()){
    		if(mod.isOnDuty() && !mod.isOccupied() && !mod.isOnBreak()) mods.add(mod);
    	}
    	
    	int numberOfRows = (int) Math.ceil(mods.size() / 9.0);
    	
    	if(mods.size() > 0){
			plugin.getPlayerMenus().put(player.getName(), mods);
		    chest = plugin.getServer().createInventory(null, numberOfRows * 9, onlineTitle);
		        
		    for (int slot = 0; slot < mods.size(); slot++) {
		    	chest = handleSlot(player, slot, chest, mods.get(slot));
		    }
    	}
    	else{
    		chest = plugin.getServer().createInventory(null, 9, offlineTitle);
    		chest = handleSlot(player, 0, chest, null);
    	}
        
        player.openInventory(chest);
    }
    
    public Inventory handleSlot(Player player, int slot, Inventory chest, Moderator moderator) {
    	int amount, id;
    	short damage = 0;
    	String name = null;
    	ArrayList<String> lore = new ArrayList<String>();
    	ItemStack stack = null;
    	
    	if(moderator != null){
    		name = "§r" + ChatColor.translateAlternateColorCodes('&', moderator.getPlayer().getDisplayName());

	    	amount = 1;
	    	id = 397;
	    	damage = 0;
            stack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
    	}
    	else{
    		name = "§r" + ChatColor.translateAlternateColorCodes('&', "Ticket");
	    	amount = 1;
	    	id = 386;
	    	damage = 0;
            stack = new ItemStack(id, amount, damage);
    	}
                    
        chest.setItem(slot, setName(stack, name, lore));
        return chest;
    }
    
    public ItemStack setName(ItemStack item, String name, ArrayList<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (name != null) {
                itemMeta.setDisplayName(name);
        }
        if (lore != null) {
                itemMeta.setLore(lore);
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
