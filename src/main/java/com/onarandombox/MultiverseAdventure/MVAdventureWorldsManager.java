package com.onarandombox.MultiverseAdventure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.onarandombox.MultiverseAdventure.event.MVAWorldFinishedReloadingEvent;
import org.bukkit.Bukkit;
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
    private final HashMap<String, AdventureWorld> adventureWorlds;
    private final MultiverseAdventure plugin;
    private final MultiverseCore core;
    private final FileConfiguration config;

    public MVAdventureWorldsManager(MultiverseAdventure plugin, MultiverseCore core, FileConfiguration config) {
        this.adventureWorlds = new HashMap<String, AdventureWorld>();
        this.plugin = plugin;
        this.core = core;
        this.config = config;

        this.plugin.log(Level.FINER, "A new MVAdventureWorldsManager was created!");
    }

    protected MultiverseCore getCore() {
        return core;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdventureWorld getMVAInfo(String name) {
        return this.adventureWorlds.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadWorlds() {
        for (MultiverseWorld world : this.getCore().getMVWorldManager().getMVWorlds()) {
            tryEnableWorld(world.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unloadWorlds() {
        List<String> worldNames = new ArrayList<String>(this.adventureWorlds.keySet());
        for (String name : worldNames)
            disableWorld(name);

        this.adventureWorlds.clear(); // safety
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryEnableWorld(String name) {
        return tryEnableWorld(name, false);
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean disableWorld(String name) {
        if (this.adventureWorlds.containsKey(name)) {
            this.adventureWorlds.remove(name);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createWorld(String name) {
        MultiverseWorld mvworld;
        if ((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) == null) {
            return true;
        }

        // first write it to the config, then load
        this.config.set("adventure." + name + ".enabled", true);

        ConfigurationSection node = this.config.getConfigurationSection("adventure." + name);
        MVAdventureWorld mvawi = new MVAdventureWorld(mvworld, plugin, node);
        mvawi.scheduleWriteTemplate();
        this.adventureWorlds.put(name, mvawi);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createWorldWithNotifications(String name, final CommandSender sender) {
        MultiverseWorld mvworld;
        if ((mvworld = this.getCore().getMVWorldManager().getMVWorld(name)) == null) {
            sender.sendMessage("That world doesn't exist...");
            return;
        }

        sender.sendMessage("Converting world '" + name + "' into an AdventureWorld...");

        // first write it to the config, then load

        this.config.set("adventure." + name + ".enabled", true);

        ConfigurationSection node = this.config.getConfigurationSection("adventure." + name);
        MVAdventureWorld mvawi = new MVAdventureWorld(mvworld, plugin, node);
        mvawi.scheduleWriteTemplate(new Callable<Void>() {
            public Void call() throws Exception {
                sender.sendMessage("Finished.");
                return null;
            }}, null);
        this.adventureWorlds.put(name, mvawi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorld(final String name) {
        deleteWorld(name, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorld(final String name, final CommandSender sender) {
        final String template;
        if (this.getMVAInfo(name) == null) {
            // idiots.
            return;
        }
        else {
            template = this.getMVAInfo(name).getTemplate();
        }

        // reset, unload, modify the config and then load
        this.getCore().getMVWorldManager().removePlayersFromWorld(name); // coming soon
        this.getMVAInfo(name).resetNow();

        // Now use our task-system to do the rest when the reset is finished.
        MVAResetListener.addTask(name, new Runnable() {
            public void run() {
                getCore().getMVWorldManager().unloadWorld(name);
                config.set("adventure." + name, null);
                File serverFolder = plugin.getServer().getWorldContainer();
                File templateFile = new File(serverFolder, template);
                FileUtils.deleteFolder(templateFile);
                getCore().getMVWorldManager().loadWorld(name);
                Bukkit.getPluginManager().callEvent(new MVAWorldFinishedReloadingEvent(name));
                // notification
                if (sender != null)
                    sender.sendMessage("Finished.");
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushWorld(String name) {
        flushWorld(name, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flushWorld(String name, final CommandSender sender) {
        if (this.getMVAInfo(name) == null) {
            // idiots.
            return false;
        }
        else return this.getMVAInfo(name).scheduleWriteTemplate(new Callable<Void>() {
                public Void call() throws Exception {
                    sender.sendMessage("Finished.");
                    return null;
                }}, null);
    }

    /**
     * {@inheritDoc}
     */
    public void saveAllTo(ConfigurationSection config) {
        for (AdventureWorld aw : adventureWorlds.values()) {
            aw.saveTo(config.getConfigurationSection(aw.getName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<AdventureWorld> getMVAWorlds() {
        return adventureWorlds.values();
    }
}
