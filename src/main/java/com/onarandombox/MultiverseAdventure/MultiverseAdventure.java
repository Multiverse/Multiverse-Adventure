package com.onarandombox.MultiverseAdventure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pneumaticraft.commandhandler.multiverse.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import com.onarandombox.MultiverseAdventure.api.AdventureWorldsManager;
import com.onarandombox.MultiverseAdventure.commands.*;
import com.onarandombox.MultiverseAdventure.listeners.*;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVPlugin;
import com.onarandombox.MultiverseCore.commands.HelpCommand;
import com.onarandombox.MultiverseCore.utils.DebugLog;
import com.onarandombox.MultiversePortals.MultiversePortals;

public class MultiverseAdventure extends JavaPlugin implements MVPlugin {

    private static final boolean blocked;
    // I know. That's bad.
    static {
        boolean fail = false;
        try {
            if (!(Bukkit.getServer() instanceof CraftServer)) {
                throw new NoClassDefFoundError();
            }
        } catch (NoClassDefFoundError e1) {
            fail = true;
        }
        blocked = fail;
    }

    private static MultiverseAdventure instance;

    private static final Logger log = Logger.getLogger("Minecraft");
    private static final String logPrefix = "[Multiverse-Adventure] ";
    protected static DebugLog debugLog;
    private MultiverseCore core;
    private MultiversePortals portals;

    private CommandHandler commandHandler;

    // private HashMap<String, MVAdventureWorld> adventureWorlds;
    private AdventureWorldsManager manager;

    private FileConfiguration MVAConfig;
    private final static int requiresProtocol = 13;

    public void onLoad() {
        instance = this;
    }

    public static void staticLog(Level level, String msg) {
        log.log(level, logPrefix + " " + msg);
        debugLog.log(level, logPrefix + " " + msg);
    }

    public static void staticDebugLog(Level level, String msg) {
        log.log(level, "[MVAdventure-Debug] " + msg);
        debugLog.log(level, "[MVAdventure-Debug] " + msg);
    }

    @Override
    public void log(Level level, String msg) {
        if (level == Level.FINE && MultiverseCore.getStaticConfig().getGlobalDebug() >= 1) {
            staticDebugLog(Level.INFO, msg);
        }
        else if (level == Level.FINER && MultiverseCore.getStaticConfig().getGlobalDebug() >= 2) {
            staticDebugLog(Level.INFO, msg);
        }
        else if (level == Level.FINEST && MultiverseCore.getStaticConfig().getGlobalDebug() >= 3) {
            staticDebugLog(Level.INFO, msg);
        }
        else if (level != Level.FINE && level != Level.FINER && level != Level.FINEST) {
            staticLog(level, msg);
        }
    }

    @Override
    public void onEnable() {
        if (blocked) {
            log.severe("Currently Multiverse-Adventure only works with CraftBukkit! Sorry! We're working on this.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check for portals
        Plugin portalsPlugin = this.getServer().getPluginManager().getPlugin("Multiverse-Portals");
        if (portalsPlugin instanceof MultiversePortals) {
            portals = (MultiversePortals) portalsPlugin;
            log.info(logPrefix + "Multiverse-Portals was found.");
        }

        // Turn on Logging
        log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled - By " + getAuthors());
        getDataFolder().mkdirs();
        File debugLogFile = new File(getDataFolder(), "debug.log");
        try {
            debugLogFile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        debugLog = new DebugLog("Multiverse-Adventure", getDataFolder() + File.separator + "debug.log");
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

        this.loadConfig();

        // this.adventureWorlds = new HashMap<String, MVAdventureWorld>();
        manager = new MVAdventureWorldsManager(this, core, MVAConfig);

        manager.loadWorlds();

        // register ourselves with core
        this.core.incrementPluginCount();

        // Register our commands
        this.registerCommands();

        // Ensure permissions are created
        this.createDefaultPerms();

        this.registerEvents();
    }

    public boolean isPortalsEnabled() {
        return portals != null;
    }

    public MultiversePortals getPortals() {
        return portals;
    }

    private void registerEvents() {
        MVAPluginListener pluginListener = new MVAPluginListener();
        MVAPlayerListener playerListener = new MVAPlayerListener();
        MVACoreListener coreListener = new MVACoreListener();
        MVAWorldListener worldListener = new MVAWorldListener();

        // Register our listeners with the Bukkit Server
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(pluginListener, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(coreListener, this);
        pm.registerEvents(worldListener, this);
    }

    private void loadConfig() {
        this.MVAConfig = this.getConfig();
    }

    private void createDefaultPerms() {
        if (this.getServer().getPluginManager().getPermission("multiverse.adventure.*") == null) {
            Permission perm = new Permission("multiverse.adventure.*");
            this.getServer().getPluginManager().addPermission(perm);
        }

        try {
            Permission all = this.getServer().getPluginManager().getPermission("multiverse.*");
            all.getChildren().put("multiverse.adventure.*", true);
            this.getServer().getPluginManager().recalculatePermissionDefaults(all);
        }
        catch (NullPointerException e) {
            // Because all could be null. multiverse.* is not our stuff so we don't touch it.
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        this.commandHandler = this.core.getCommandHandler();

        this.commandHandler.registerCommand(new EnableCommand(this));
        this.commandHandler.registerCommand(new DisableCommand(this));
        this.commandHandler.registerCommand(new FlushCommand(this));
        this.commandHandler.registerCommand(new SetTemplateCommand(this));
        this.commandHandler.registerCommand(new ListCommand(this));
        this.commandHandler.registerCommand(new ResetCommand(this));

        for (com.pneumaticraft.commandhandler.multiverse.Command c : this.commandHandler.getAllCommands()) {
            if (c instanceof HelpCommand) {
                c.addKey("mva");
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
                    authors += " and " + this.getDescription().getAuthors().get(i);
                }
                else {
                    authors += ", " + this.getDescription().getAuthors().get(i);
                }
            }
            return authors.substring(2);
        }
        else {
            return this.getDescription().getAuthors().get(0); // in case it's just one author like here
        }
    }

    @Override
    public void onDisable() {
        if (blocked) return;
        // save config
        saveConfig();
    }

    @Override
    public String dumpVersionInfo(String buffer) {
        buffer += logAndAddToPasteBinBuffer("Multiverse-Adventure Version: " + this.getDescription().getVersion());
        buffer += logAndAddToPasteBinBuffer("Bukkit Version: " + this.getServer().getVersion());
        // buffer += logAndAddToPasteBinBuffer("Loaded Portals: " + this.getPortalManager().getAllPortals().size());
        // buffer += logAndAddToPasteBinBuffer("Dumping Portal Values: (version " + this.getPortalsConfig().getString("version", "NOT SET") + ")");
        // buffer += logAndAddToPasteBinBuffer(this.getPortalsConfig().getAll() + "");
        // buffer += logAndAddToPasteBinBuffer("Dumping Config Values: (version " + this.getMainConfig().getString("version", "NOT SET") + ")");
        // buffer += logAndAddToPasteBinBuffer("wand: " + this.getMainConfig().getString("wand", "NOT SET"));
        // buffer += logAndAddToPasteBinBuffer("Special Code: FRN001");
        return buffer;
    }

    private String logAndAddToPasteBinBuffer(String string) {
        this.log(Level.INFO, string);
        return "[Multiverse-Adventure] " + string + "\n";
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
        manager.unloadWorlds();
        this.saveConfig();
        this.loadConfig();
        manager.loadWorlds();
    }

    public static MultiverseAdventure getInstance() {
        return instance;
    }

    public void saveConfig() {
        this.getAdventureWorldsManager().saveAllTo(this.MVAConfig.getConfigurationSection("adventure"));
        try {
            this.MVAConfig.save(new File(this.getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
            this.log(Level.SEVERE, "Couldn't write to config.yml! Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public AdventureWorldsManager getAdventureWorldsManager() {
        return manager;
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public AdventureWorld getMVAInfo(String fromWorldName) {
        return this.getAdventureWorldsManager().getMVAInfo(fromWorldName);
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public void tryEnableWorld(String name) {
        this.getAdventureWorldsManager().tryEnableWorld(name);
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public void tryEnableWorld(String name, boolean noreset) {
        this.getAdventureWorldsManager().tryEnableWorld(name, noreset);
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public void disableWorld(String name) {
        this.getAdventureWorldsManager().disableWorld(name);
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public void deleteWorld(String world, CommandSender sender) {
        this.getAdventureWorldsManager().deleteWorld(world, sender);
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public void createWorldWithNotifications(String world, CommandSender sender) {
        this.getAdventureWorldsManager().createWorldWithNotifications(world, sender);
    }

    /**
     * @deprecated Use the AdventureWorldsManager instead
     */
    @Deprecated
    public void flushWorld(String world, CommandSender sender) {
        this.getAdventureWorldsManager().flushWorld(world, sender);
    }
}
