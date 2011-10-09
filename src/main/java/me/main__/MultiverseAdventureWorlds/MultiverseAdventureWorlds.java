package me.main__.MultiverseAdventureWorlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.main__.MultiverseAdventureWorlds.commands.EnableCommand;
import me.main__.MultiverseAdventureWorlds.listeners.MVAWConfigReloadListener;
import me.main__.MultiverseAdventureWorlds.listeners.MVAWPlayerListener;
import me.main__.MultiverseAdventureWorlds.listeners.MVAWPluginListener;
import me.main__.MultiverseAdventureWorlds.listeners.MVAWWorldListener;
import me.main__.MultiverseAdventureWorlds.util.FileUtils;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVPlugin;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commands.HelpCommand;
import com.onarandombox.MultiverseCore.utils.DebugLog;
import com.pneumaticraft.commandhandler.CommandHandler;

public class MultiverseAdventureWorlds extends JavaPlugin implements MVPlugin {
	private static MultiverseAdventureWorlds instance;

	private static final Logger log = Logger.getLogger("Minecraft");
    private static final String logPrefix = "[Multiverse-AdventureWorlds] ";
    protected static DebugLog debugLog;
    private MultiverseCore core;

    private CommandHandler commandHandler;
    
    private HashMap<String, MVAdventureWorldInfo> adventureWorlds;

    private Configuration MVAWConfig;
    private final static int requiresProtocol = 5;

    public void onLoad() {
        instance = this;
    }
	
    public static void staticLog(Level level, String msg) {
        log.log(level, logPrefix + " " + msg);
        debugLog.log(level, logPrefix + " " + msg);
    }

    public static void staticDebugLog(Level level, String msg) {
        log.log(level, "[MVPortals-Debug] " + msg);
        debugLog.log(level, "[MVPortals-Debug] " + msg);
    }
    
	@Override
	public void log(Level level, String msg) {
		if (level == Level.FINE && MultiverseCore.GlobalDebug >= 1) {
            staticDebugLog(Level.INFO, msg);
        } else if (level == Level.FINER && MultiverseCore.GlobalDebug >= 2) {
            staticDebugLog(Level.INFO, msg);
        } else if (level == Level.FINEST && MultiverseCore.GlobalDebug >= 3) {
            staticDebugLog(Level.INFO, msg);
        } else if (level != Level.FINE && level != Level.FINER && level != Level.FINEST) {
            staticLog(level, msg);
        }
	}

	@Override
	public void onEnable() {
		this.core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

        // Test if the Core was found, if not we'll disable this plugin.
        if (this.core == null) {
            log.info(logPrefix + "Multiverse-Core not found, will keep looking.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (this.core.getProtocolVersion() < requiresProtocol) {
            log.severe(logPrefix + "Your Multiverse-Core is OUT OF DATE");
            log.severe(logPrefix + "This version of AdventureWorlds requires Protocol Level: " + requiresProtocol);
            log.severe(logPrefix + "Your of Core Protocol Level is: " + this.core.getProtocolVersion());
            log.severe(logPrefix + "Grab an updated copy at: ");
            log.severe(logPrefix + "http://bukkit.onarandombox.com/?dir=multiverse-core");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        this.adventureWorlds = new HashMap<String, MVAdventureWorldInfo>();

        this.loadConfig();

        // Turn on Logging and register ourselves with Core
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled - By " + getAuthors());
        getDataFolder().mkdirs();
        File debugLogFile = new File(getDataFolder(), "debug.log");
        try {
			debugLogFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        debugLog = new DebugLog("Multiverse-AdventureWorlds", getDataFolder() + File.separator + "debug.log");
        this.core.incrementPluginCount();

        // Register our commands
        this.registerCommands();

        // Ensure permissions are created
        this.createDefaultPerms();

        this.registerEvents();
	}
	
	private void registerEvents() {
		MVAWPluginListener pluginListener = new MVAWPluginListener();
        MVAWPlayerListener playerListener = new MVAWPlayerListener();
        MVAWConfigReloadListener customListener = new MVAWConfigReloadListener();
        MVAWWorldListener worldListener = new MVAWWorldListener();

        // Register our listeners with the Bukkit Server
        this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Normal, this);
        
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_CHANGED_WORLD, playerListener, Priority.Monitor, this);
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_KICK, playerListener, Priority.Monitor, this);
        this.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        
        this.getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT, customListener, Priority.Normal, this);
        
        this.getServer().getPluginManager().registerEvent(Type.WORLD_LOAD, worldListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Type.WORLD_UNLOAD, worldListener, Priority.Normal, this);
	}

	private void loadConfig() {
		//new MVPDefaultConfiguration(getDataFolder(), "config.yml", this.migrator);
        this.MVAWConfig = this.getConfiguration();
        loadWorlds();
	}
	
	/**
	 * Iterates through all loaded MVWorlds and enables AdventureWorlds
	 */
	public void loadWorlds() {
		for (MultiverseWorld world : this.getCore().getMVWorldManager().getMVWorlds()) {
        	tryEnableWorld(world.getName());
        }
	}
	
	/**
	 * Disables all AdventureWorlds
	 */
	public void unloadWorlds() {
		for (MVAdventureWorldInfo world : this.adventureWorlds.values()) {
			disableWorld(world.getName());
		}
		this.adventureWorlds.clear(); //safety
	}
	
	/**
	 * Tries to enable an AdventureWorld that's already known to Multiverse-AdventureWorlds.
	 * @param name
	 * The name of that world.
	 * @return
	 * True if success, false if failed.
	 */
	public boolean tryEnableWorld(String name) {
		return tryEnableWorld(name, false);
	}
	
	/**
	 * Tries to enable an AdventureWorld that's already known to Multiverse-AdventureWorlds.
	 * @param name
	 * The name of that world.
	 * @param noreset
	 * If the world shouldn't be reset.
	 * @return
	 * True if success, false if failed.
	 */
	public boolean tryEnableWorld(String name, boolean noreset) {
		MultiverseWorld mvworld;
		if (((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) != null) && (this.MVAWConfig.getKeys("adventureworlds") != null) && this.MVAWConfig.getKeys("adventureworlds").contains(name)) {
			ConfigurationNode node = this.MVAWConfig.getNode("adventureworlds." + name);
			boolean enabled = this.MVAWConfig.getBoolean("adventureworlds." + name + ".enabled", true);
			if (enabled) {
				MVAdventureWorldInfo mvawi = new MVAdventureWorldInfo(mvworld, this, node);
				if (!noreset)
					mvawi.resetNow();
				this.adventureWorlds.put(name, mvawi);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Tries to disable an AdventureWorld.
	 * @param name
	 * The name of the world.
	 * @return
	 * True if success, false if failed.
	 */
	public boolean disableWorld(String name) {
		if (this.adventureWorlds.containsKey(name)) {
			this.adventureWorlds.remove(name);
			return true;
		}
		return false;
	}
	
	/**
	 * Converts a normal world into an AdventureWorld
	 * @param name
	 * The name of the world
	 * @return
	 * True if success, false if failed.
	 */
	public boolean createWorld(String name) {
		//first write it to the config, then load
		this.MVAWConfig.setProperty("adventureworlds." + name + ".enabled", true);
		MultiverseWorld mvworld;
		if (((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) != null) && (this.MVAWConfig.getKeys("adventureworlds") != null) && this.MVAWConfig.getKeys("adventureworlds").contains(name)) {
			ConfigurationNode node = this.MVAWConfig.getNode("adventureworlds." + name);
			boolean enabled = this.MVAWConfig.getBoolean("adventureworlds." + name + ".enabled", true);
			if (enabled) {
				MVAdventureWorldInfo mvawi = new MVAdventureWorldInfo(mvworld, this, node);
				mvawi.scheduleWriteTemplate();
				this.adventureWorlds.put(name, mvawi);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Converts a normal world into an AdventureWorld and sends notifications to a CommandSender
	 * @param name
	 * The name of the world
	 * @param sender
	 * The CommandSender that receives the notifications
	 */
	public void createWorldWithNotifications(String name, CommandSender sender) {
		sender.sendMessage("Converting world '" + name + "' into an AdventureWorld...");
		//first write it to the config, then load
		this.MVAWConfig.setProperty("adventureworlds." + name + ".enabled", true);
		MultiverseWorld mvworld;
		if (((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) != null) && (this.MVAWConfig.getKeys("adventureworlds") != null) && this.MVAWConfig.getKeys("adventureworlds").contains(name)) {
			ConfigurationNode node = this.MVAWConfig.getNode("adventureworlds." + name);
			boolean enabled = this.MVAWConfig.getBoolean("adventureworlds." + name + ".enabled", true);
			if (enabled) {
				MVAdventureWorldInfo mvawi = new MVAdventureWorldInfo(mvworld, this, node);
				mvawi.scheduleWriteTemplate();
				this.adventureWorlds.put(name, mvawi);
				return;
			}
		}
	}
	
	/**
	 * Converts an AdventureWorld back into a normal world.
	 * @param name
	 * The name of the world.
	 */
	public void deleteWorld(String name) {
		String template;
		if (this.getMVAWInfo(name) == null) {
			//idiots.
			return;
		}
		else {
			template = this.getMVAWInfo(name).getTemplate();
		}
		
		//reset, unload, modify the config and then load
		this.getCore().getMVWorldManager().removePlayersFromWorld(name); // coming soon
		this.getMVAWInfo(name).resetNow();
		// TODO wait for reset finish
		this.getCore().getMVWorldManager().unloadWorld(name);
		this.MVAWConfig.removeProperty("adventureworlds." + name);
		File serverFolder = new File(this.getDataFolder().getAbsolutePath()).getParentFile().getParentFile();
		File templateFile = new File(serverFolder, template);
		FileUtils.deleteFolder(templateFile);
		this.getCore().getMVWorldManager().loadWorld(name);
	}
	
	private void createDefaultPerms() {
		// TODO Auto-generated method stub
		
	}

	private void registerCommands() {
		this.commandHandler = this.core.getCommandHandler();
        this.commandHandler.registerCommand(new EnableCommand(this));
        for (com.pneumaticraft.commandhandler.Command c : this.commandHandler.getAllCommands()) {
            if (c instanceof HelpCommand) {
                c.addKey("mvaw");
            }
        }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!this.isEnabled()) {
            sender.sendMessage("This plugin is Disabled!");
            return true;
        }
        ArrayList<String> allArgs = new ArrayList<String>(Arrays.asList(args));
        allArgs.add(0, command.getName());
        return this.commandHandler.locateAndRunCommand(sender, allArgs);
    }
	
	/**
     * Parse the Authors Array into a readable String with ',' and 'and'.
     *
     * @return String containing all the authors formatted correctly with ',' and 'and'.
     */
    private String getAuthors() {
        if (this.getDescription().getAuthors().size() > 1) {
			String authors = "";
			for (int i = 0; i < this.getDescription().getAuthors().size(); i++) {
				if (i == this.getDescription().getAuthors().size() - 1) {
					authors += " and "
							+ this.getDescription().getAuthors().get(i);
				} else {
					authors += ", " + this.getDescription().getAuthors().get(i);
				}
			}
			return authors.substring(2);
		}
        else {
        	return this.getDescription().getAuthors().get(0); //in case it's just one author like here
        }
    }

	@Override
	public void onDisable() {
		
	}

	@Override
	public String dumpVersionInfo(String buffer) {
		buffer += logAndAddToPasteBinBuffer("Multiverse-AdventureWorlds Version: " + this.getDescription().getVersion());
        buffer += logAndAddToPasteBinBuffer("Bukkit Version: " + this.getServer().getVersion());
        //buffer += logAndAddToPasteBinBuffer("Loaded Portals: " + this.getPortalManager().getAllPortals().size());
        //buffer += logAndAddToPasteBinBuffer("Dumping Portal Values: (version " + this.getPortalsConfig().getString("version", "NOT SET") + ")");
        //buffer += logAndAddToPasteBinBuffer(this.getPortalsConfig().getAll() + "");
        //buffer += logAndAddToPasteBinBuffer("Dumping Config Values: (version " + this.getMainConfig().getString("version", "NOT SET") + ")");
        //buffer += logAndAddToPasteBinBuffer("wand: " + this.getMainConfig().getString("wand", "NOT SET"));
        //buffer += logAndAddToPasteBinBuffer("Special Code: FRN001");
        return buffer;
	}
	

    private String logAndAddToPasteBinBuffer(String string) {
        this.log(Level.INFO, string);
        return "[Multiverse-AdventureWorlds] " + string + "\n";
    }

	@Override
	public MultiverseCore getCore() {
		return this.core;
	}

	@Override
	public void setCore(MultiverseCore core) {
		this.core = core;
	}

	@Override
	public int getProtocolVersion() {
		return 1;
	}

	public void reloadConfigs() {
		this.unloadWorlds();
		this.loadConfig();
	}
	
	/**
	 * Gets the MVAdventureWorldInfo-Object of a world.
	 * @param name
	 * The name of the world.
	 * @return
	 * The MVAdventureWorldInfo-Object
	 */
	public MVAdventureWorldInfo getMVAWInfo(String name) {
		return this.adventureWorlds.get(name);
	}
	
	public static MultiverseAdventureWorlds getInstance() {
		return instance;
	}

	public void saveConfig() {
        this.MVAWConfig.save();
	}
}
