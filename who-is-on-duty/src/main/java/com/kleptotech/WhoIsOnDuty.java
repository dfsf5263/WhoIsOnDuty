package com.kleptotech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.kleptotech.commands.ModsCommandExec;
import com.kleptotech.commands.TicketCommandExec;
import com.kleptotech.commands.WhoIsOnDutyCommandExec;
import com.kleptotech.dao.MetadataDAO;
import com.kleptotech.scoreboards.CompletedRequestsScoreboard;
import com.kleptotech.scoreboards.TotalWatchScoreboard;
import com.kleptotech.tasks.CheckForTickets;

public final class WhoIsOnDuty extends JavaPlugin {
	
	private MetadataDAO metadataDao = new MetadataDAO(this);
	private ModRequestMenu menu;
	private Map<String, Moderator> moderators;
	private CompletedRequestsScoreboard completedRequestsScoreboard;
	private TotalWatchScoreboard totalWatchScoreboard;
	private Map<String, Boolean> requestees;
	private Map<String, ArrayList<Moderator>> playerMenus;
	List<String> tickets;
	protected File ticketsFile;
    protected FileConfiguration ticketsConfig;
    String onlineTitle = ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + "" + ChatColor.BOLD + "Available Moderators");
    String offlineTitle = ChatColor.translateAlternateColorCodes('&', ChatColor.RED + "" + ChatColor.BOLD + "No Moderators Online");
    
	@Override
	public void onEnable() {
		getLogger().info("Who Is On Duty is booting up. Hold on to your britches!");
		saveDefaultConfig();
		List<String> configMods = getConfig().getStringList("mods");
		requestees = new HashMap<String, Boolean>();
		moderators = new HashMap<String, Moderator>();
		playerMenus = new HashMap<String, ArrayList<Moderator>>();
		
		loadTickets();
		tickets = ticketsConfig.getStringList("tickets");
		if(tickets.size() > 0){
			new CheckForTickets(this).runTaskLater(this, 3000);
		}
		else new CheckForTickets(this).runTaskLater(this, 72000);
		
		for(String mod : configMods){
			Player player = getServer().getPlayer(mod);
			if(player != null){
				Moderator moderator = new Moderator(player);
				moderators.put(player.getName(), moderator);
			}
		}
		
		getServer().getPluginManager().registerEvents(new Listener() {
			 
			@edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void playerJoin(PlayerJoinEvent event) {
            	if (event.getPlayer().hasPermission("whoisonduty.mod") || event.getPlayer().hasPermission("whoisonduty.admin")) {
					moderators.put(event.getPlayer().getName(), new Moderator(event.getPlayer()));
					
					Moderator moderator = moderators.get(event.getPlayer().getName());
					
					moderator.setStartTime(System.currentTimeMillis());
					moderator.setTotalWatch(metadataDao.getTotalWatch(event.getPlayer().getName()));
					moderator.setCompletedRequests(metadataDao.getCompletedRequests(event.getPlayer().getName()));
					if(event.getPlayer().hasPermission("whoisonduty.admin")){
						moderator.setOnDuty(false);
					}
					else{
						moderator.setOnDuty(true);
						event.getPlayer().sendMessage(ChatColor.GREEN + "Hey there moderator. You are now ON Duty!");
					}
					if(tickets.size() > 0) event.getPlayer().sendMessage(ChatColor.RED + "There are currently " +
							tickets.size() + " open tickets.");
            	}
            	requestees.put(event.getPlayer().getName(), true);
            }
            
            @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void playerQuit(PlayerQuitEvent event) {
            	if (event.getPlayer().hasPermission("whoisonduty.mod") || event.getPlayer().hasPermission("whoisonduty.admin")) {
            		Moderator moderator = moderators.get(event.getPlayer().getName());
            		if(moderator.isOnDuty()){
						long startTime = moderator.getStartTime();
						long stopTime = System.currentTimeMillis();
						long watch = stopTime - startTime;
						long totalWatch = moderator.getTotalWatch() + watch;
						moderator.setTotalWatch(totalWatch);
            		}
					metadataDao.setTotalWatch(event.getPlayer().getName(), moderator.getTotalWatch());
					metadataDao.setCompletedRequests(event.getPlayer().getName(), moderator.getCompletedRequests());
					moderators.remove(event.getPlayer().getName());
				}
            	requestees.remove(event.getPlayer().getName());
            }
            
            @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void onInventoryInteract(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {
                    Player player = (Player) event.getWhoClicked();
                    if (event.getInventory().getTitle().equals(onlineTitle)) {
                            event.setCancelled(true);
                            int slot = event.getRawSlot();
                        	ArrayList<Moderator> mods = playerMenus.get(player.getName());
                            menu.handleModRequest(mods.get(slot).getPlayer(), player);
                            playerMenus.remove(player.getName());
                            player.closeInventory();
                    }
                    else if (event.getInventory().getTitle().equals(offlineTitle)) {
                        if(event.getRawSlot() == 0){
                        	event.setCancelled(true);
                        	menu.handleOfflineTicket(player);
                        	player.closeInventory();
                        }
                    }
                }
            }
            
            @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            @EventHandler
            public void playerQuit(PlayerKickEvent event) {
            	if (event.getPlayer().hasPermission("whoisonduty.mod") || event.getPlayer().hasPermission("whoisonduty.admin")) {
            		Moderator moderator = moderators.get(event.getPlayer().getName());
            		if(moderator.isOnDuty()){
						long startTime = moderator.getStartTime();
						long stopTime = System.currentTimeMillis();
						long watch = stopTime - startTime;
						long totalWatch = moderator.getTotalWatch() + watch;
						moderator.setTotalWatch(totalWatch);
            		}
					metadataDao.setTotalWatch(event.getPlayer().getName(), moderator.getTotalWatch());
					metadataDao.setCompletedRequests(event.getPlayer().getName(), moderator.getCompletedRequests());
					moderators.remove(event.getPlayer().getName());
				}
            	requestees.remove(event.getPlayer().getName());
            }
        }, this);
		
		getCommand("wiod").setExecutor(new WhoIsOnDutyCommandExec(this));
		getCommand("mods").setExecutor(new ModsCommandExec(this));
		getCommand("ticket").setExecutor(new TicketCommandExec(this));
		
		List<Player> onlinePeeps= new ArrayList<Player>();
		onlinePeeps.addAll(Arrays.asList(getServer().getOnlinePlayers()));
		
		for(Player peep : onlinePeeps){
			if (peep.hasPermission("whoisonduty.mod") || peep.hasPermission("whoisonduty.admin")) {
				moderators.put(peep.getName(), new Moderator(peep));
				
				Moderator moderator = moderators.get(peep.getName());
				
				moderator.setStartTime(System.currentTimeMillis());
				moderator.setTotalWatch(metadataDao.getTotalWatch(peep.getName()));
				moderator.setCompletedRequests(metadataDao.getCompletedRequests(peep.getName()));
				if(peep.hasPermission("whoisonduty.admin")){
					moderator.setOnDuty(false);
				}
				else{
					moderator.setOnDuty(true);
					peep.sendMessage(ChatColor.GREEN + "Hey there moderator. You are now ON Duty!");
				}
			}
		}
		
		menu = new ModRequestMenu(this, onlineTitle, offlineTitle);
		completedRequestsScoreboard = new CompletedRequestsScoreboard(this);
		totalWatchScoreboard = new TotalWatchScoreboard(this);
		
		getLogger().info("Who Is On Duty is enabled! Whew...");
	}

	@Override
	public void onDisable() {
		getLogger().info("Who Is On Duty is shutting down. Good Bye!");
		
		List<String> modList = new ArrayList<String>();
		for(Moderator mod : moderators.values()){
			if(mod.isOnDuty()){
				mod.setOnDuty(false);
				long startTime = mod.getStartTime();
				long stopTime = System.currentTimeMillis();
				long watch = stopTime - startTime;
				long totalWatch = mod.getTotalWatch() + watch;
				metadataDao.setTotalWatch(mod.getPlayer().getName(), totalWatch);
				metadataDao.setCompletedRequests(mod.getPlayer().getName(), mod.getCompletedRequests());
			}
			modList.add(mod.getPlayer().getName());
		}
		
		try {
			ticketsConfig.set("tickets", tickets);
			ticketsConfig.save(ticketsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getConfig().set("mods", modList);
		saveConfig();
	}
	
	public void loadTickets() {
		String fileName = "tickets.yml";
		ticketsFile = new File(getDataFolder(), fileName);
        
        if (!ticketsFile.exists()) {
            getLogger().info("Creating WIOD " + fileName + " File...");
            createFile(fileName);
        }
        else {
        	getLogger().info("Loading WIOD " + fileName + " File...");
        }

        ticketsConfig = YamlConfiguration.loadConfiguration(ticketsFile);
    }
	
	private void createFile(String fileName) {
		ticketsFile.getParentFile().mkdirs();

        InputStream inputStream = getResource(fileName);

        if (inputStream == null) {
            getLogger().severe("Missing resource file: '" + fileName + "'! You should cry and run in circles.");
            return;
        }

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(ticketsFile);

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                inputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	public MetadataDAO getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(MetadataDAO metadataDao) {
		this.metadataDao = metadataDao;
	}

	public Map<String, Moderator> getModerators() {
		return moderators;
	}

	public void setModerators(Map<String, Moderator> moderators) {
		this.moderators = moderators;
	}

	public ModRequestMenu getMenu() {
		return menu;
	}

	public void setMenu(ModRequestMenu menu) {
		this.menu = menu;
	}

	public CompletedRequestsScoreboard getCompletedRequestsScoreboard() {
		return completedRequestsScoreboard;
	}

	public void setCompletedRequestsScoreboard(
			CompletedRequestsScoreboard completedRequestsScoreboard) {
		this.completedRequestsScoreboard = completedRequestsScoreboard;
	}

	public TotalWatchScoreboard getTotalWatchScoreboard() {
		return totalWatchScoreboard;
	}

	public void setTotalWatchScoreboard(TotalWatchScoreboard totalWatchScoreboard) {
		this.totalWatchScoreboard = totalWatchScoreboard;
	}

	public Map<String, Boolean> getRequestees() {
		return requestees;
	}

	public void setRequestees(Map<String, Boolean> requestees) {
		this.requestees = requestees;
	}

	public List<String> getTickets() {
		return tickets;
	}

	public void setTickets(List<String> tickets) {
		this.tickets = tickets;
	}
	
	public Map<String, ArrayList<Moderator>> getPlayerMenus() {
		return playerMenus;
	}

	public void setPlayerMenus(Map<String, ArrayList<Moderator>> playerMenus) {
		this.playerMenus = playerMenus;
	}
}
