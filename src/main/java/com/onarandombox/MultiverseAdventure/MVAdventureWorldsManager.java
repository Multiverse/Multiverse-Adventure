package com.onarandombox.MultiverseAdventure;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import com.onarandombox.MultiverseAdventure.api.AdventureWorldsManager;
import com.onarandombox.MultiverseAdventure.listeners.MVAResetListener;
import com.onarandombox.MultiverseAdventure.util.FileUtils;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * @author main()
 */
public class MVAdventureWorldsManager implements AdventureWorldsManager {
    private final HashMap<String, MVAdventureWorld> adventureWorlds;
    private final MultiverseAdventure plugin;
    private final MultiverseCore core;
    private final FileConfiguration config;
    
	public MVAdventureWorldsManager(MultiverseAdventure plugin, MultiverseCore core, FileConfiguration config) {
    	this.adventureWorlds = new HashMap<String, MVAdventureWorld>();
    	this.plugin = plugin;
    	this.core = core;
    	this.config = config;
    }
	
	protected MultiverseCore getCore() {
		return core;
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#getMVAWInfo(java.lang.String)
	 */
	@Override
	public AdventureWorld getMVAInfo(String name) {
		return this.adventureWorlds.get(name);
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#loadWorlds()
	 */
	@Override
	public void loadWorlds() {
		for (MultiverseWorld world : this.getCore().getMVWorldManager().getMVWorlds()) {
        	tryEnableWorld(world.getName());
        }
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#unloadWorlds()
	 */
	@Override
	public void unloadWorlds() {
		for (AdventureWorld world : this.adventureWorlds.values()) {
			disableWorld(world.getName());
		}
		this.adventureWorlds.clear(); //safety
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#tryEnableWorld(java.lang.String)
	 */
	@Override
	public boolean tryEnableWorld(String name) {
		return tryEnableWorld(name, false);
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#tryEnableWorld(java.lang.String, boolean)
	 */
	@Override
	public boolean tryEnableWorld(String name, boolean noreset) {
		MultiverseWorld mvworld;
		if (((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) != null) && this.config.contains("adventure." + name)) {
			ConfigurationSection node = this.config.getConfigurationSection("adventure." + name);
			boolean enabled = this.config.getBoolean("adventure." + name + ".enabled", true);
			if (enabled) {
				MVAdventureWorld mvawi = new MVAdventureWorld(mvworld, plugin, node);
				if (!noreset)
					mvawi.resetNow();
				this.adventureWorlds.put(name, mvawi);
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#disableWorld(java.lang.String)
	 */
	@Override
	public boolean disableWorld(String name) {
		if (this.adventureWorlds.containsKey(name)) {
			this.adventureWorlds.remove(name);
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#createWorld(java.lang.String)
	 */
	@Override
	public boolean createWorld(String name) {
		MultiverseWorld mvworld;
		if ((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) == null) {
			return true;
		}
		

		//first write it to the config, then load
		this.config.set("adventure." + name + ".enabled", true);

		ConfigurationSection node = this.config.getConfigurationSection("adventure." + name);
		MVAdventureWorld mvawi = new MVAdventureWorld(mvworld, plugin, node);
		mvawi.scheduleWriteTemplate();
		this.adventureWorlds.put(name, mvawi);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#createWorldWithNotifications(java.lang.String, org.bukkit.command.CommandSender)
	 */
	@Override
	public void createWorldWithNotifications(String name, final CommandSender sender) {
		MultiverseWorld mvworld;
		if ((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) == null) {
			sender.sendMessage("That world doesn't exist...");
			return;
		}
		
		sender.sendMessage("Converting world '" + name + "' into an AdventureWorld...");
		
		//first write it to the config, then load
		
		this.config.set("adventure." + name + ".enabled", true);

		ConfigurationSection node = this.config.getConfigurationSection("adventure." + name);
		MVAdventureWorld mvawi = new MVAdventureWorld(mvworld, plugin, node);
		mvawi.scheduleWriteTemplate(new Callable<Void> () {
			public Void call() throws Exception {
				sender.sendMessage("Finished.");
				return null;
			}});
		this.adventureWorlds.put(name, mvawi);
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#deleteWorld(java.lang.String)
	 */
	@Override
	public void deleteWorld(final String name) {
		deleteWorld(name, null);
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#deleteWorld(java.lang.String, org.bukkit.command.CommandSender)
	 */
	@Override
	public void deleteWorld(final String name, final CommandSender sender) {
		final String template;
		if (this.getMVAInfo(name) == null) {
			//idiots.
			return;
		}
		else {
			template = this.getMVAInfo(name).getTemplate();
		}
		
		//reset, unload, modify the config and then load
		this.getCore().getMVWorldManager().removePlayersFromWorld(name); // coming soon
		this.getMVAInfo(name).resetNow();

		//Now use our task-system to do the rest when the reset is finished.
		MVAResetListener.addTask(name, new Runnable() {
			public void run() {
				getCore().getMVWorldManager().unloadWorld(name);
				config.set("adventure." + name, null);
				File serverFolder = new File(plugin.getDataFolder().getAbsolutePath()).getParentFile().getParentFile();
				File templateFile = new File(serverFolder, template);
				FileUtils.deleteFolder(templateFile);
				getCore().getMVWorldManager().loadWorld(name);
				
				//notification
				if (sender != null)
					sender.sendMessage("Finished.");
			}});
		
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#flushWorld(java.lang.String)
	 */
	@Override
	public void flushWorld(String name) {
		flushWorld(name, null);
	}
	
	/* (non-Javadoc)
	 * @see me.main__.MultiverseAdventureWorlds.AdventureWorldsManager#flushWorld(java.lang.String, org.bukkit.command.CommandSender)
	 */
	@Override
	public boolean flushWorld(String name, final CommandSender sender) {
		if (this.getMVAInfo(name) == null) {
			//idiots.
			return false;
		}
		else return this.getMVAInfo(name).scheduleWriteTemplate(new Callable<Void>() {
			public Void call() throws Exception {
				sender.sendMessage("Finished.");
				return null;
			}});
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveAllTo(ConfigurationSection config) {
		for (MVAdventureWorld aw : adventureWorlds.values()) {
			aw.saveTo(config.getConfigurationSection(aw.getName()));
		}
	}
}
