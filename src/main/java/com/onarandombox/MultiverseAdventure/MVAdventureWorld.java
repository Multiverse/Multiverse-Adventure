package com.onarandombox.MultiverseAdventure;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import com.onarandombox.MultiverseAdventure.event.MVAResetEvent;
import com.onarandombox.MultiverseAdventure.event.MVAResetFinishedEvent;
import com.onarandombox.MultiverseAdventure.listeners.MVAWorldListener;
import com.onarandombox.MultiverseAdventure.util.FileUtils;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * Provides support for "adventure"-worlds
 * 
 * @author main()
 */
public final class MVAdventureWorld implements AdventureWorld {
    /**
     * Whether this AdventureWorld is "active" (contains players that have changed it)
     */
    private boolean active;
    private final MultiverseWorld world;
    private final MultiverseAdventure plugin;

    /**
     * The name of the template-folder for this AdventureWorld
     */
    private String template;
    private int activationdelay;
    private int resetdelay;

    private int resetTaskId;
    private int activationTaskId;

    public MVAdventureWorld(MultiverseWorld world, MultiverseAdventure plugin, String template, int activationdelay, int resetdelay) {
        this.world = world;
        this.plugin = plugin;
        active = false;

        this.setTemplate(template);
        this.setActivationDelay(activationdelay);
        this.setResetDelay(resetdelay);

        resetTaskId = -1;
        activationTaskId = -1;

        this.plugin.log(Level.FINER, "A new MVAdventureWorld-Object was created!");
    }

    public MVAdventureWorld(MultiverseWorld world, MultiverseAdventure plugin, ConfigurationSection node) {
        this.world = world;
        this.plugin = plugin;
        active = false;

        this.setTemplate(node.getString("template", "NAME.template")); // "NAME" will be replaced with the world's name
        this.setActivationDelay(node.getInt("activationdelay", 10));
        this.setResetDelay(node.getInt("resetdelay", 10));
        plugin.saveConfig();

        resetTaskId = -1;
        activationTaskId = -1;

        this.plugin.log(Level.FINER, "A new MVAdventureWorld-Object was created!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveTo(ConfigurationSection config) {
        config.set("enabled", true);
        config.set("template", this.template);
        config.set("activationdelay", activationdelay);
        config.set("resetdelay", resetdelay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActivating() {
        if (isActive())
            return false;
        else
            return plugin.getServer().getScheduler().isQueued(activationTaskId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResetting() {
        return plugin.getServer().getScheduler().isQueued(resetTaskId); // || plugin.getServer().getScheduler().isCurrentlyRunning(resetTaskId); We don't need to check this since the ResetPreparer runs in the main thread
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiverseWorld getMVWorld() {
        return world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplate() {
        return template.replaceAll("NAME", world.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActivationDelay() {
        return activationdelay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActivationDelay(int activationDelay) {
        this.activationdelay = activationDelay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResetDelay() {
        return resetdelay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResetDelay(int resetDelay) {
        this.resetdelay = resetDelay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean scheduleReset() {
        plugin.log(Level.FINER, "Scheduling reset of world '" + this.getName() + "' ...");

        resetTaskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ResetPreparer(this), getResetDelay() * 20);
        // resetdelay is in seconds and with 20 ticks per second we have to take this * 20

        return resetTaskId != -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetNow() {
        plugin.log(Level.FINER, "Resetting world '" + this.getName() + "' NOW!");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ResetPreparer(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelReset() {
        if (isResetting()) {
            plugin.log(Level.FINER, "Cancelling reset of world '" + this.getName() + "' ...");

            plugin.getServer().getScheduler().cancelTask(resetTaskId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scheduleActivation() {
        plugin.log(Level.FINER, "Scheduling activation of world '" + this.getName() + "' ...");

        if (getActivationDelay() == 0) {
            active = true;
            return;
            // We don't need the scheduler here
        }
        activationTaskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                active = true;
            }
        }, getActivationDelay() * 20);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelActivation() {
        if (isActivating()) {
            plugin.log(Level.FINER, "Cancelling activation of world '" + this.getName() + "' ...");

            plugin.getServer().getScheduler().cancelTask(activationTaskId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return world.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean scheduleWriteTemplate() {
        return scheduleWriteTemplate(new TemplateWriter());
    }

    /**
     * Writes the current state of the world to the template. Useful for initializing.
     * 
     * @param sender A CommandSender that receives a notification after the work is done.
     * @return True if success, false if failed.
     * @deprecated Use {@link #scheduleWriteTemplate(Runnable)} instead
     */
    public boolean scheduleWriteTemplate(final CommandSender sender) {
        return scheduleWriteTemplate(new Callable<Void>() {
            public Void call() throws Exception {
                sender.sendMessage("Finished.");
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated Use {@link #scheduleWriteTemplate(Callable<Void>,Callable<Void>)} instead
     */
    @Override
    public boolean scheduleWriteTemplate(Callable<Void> onFinish) {
        return scheduleWriteTemplate(onFinish, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean scheduleWriteTemplate(Callable<Void> onFinish, Callable<Void> onFail) {
        return scheduleWriteTemplate(new TemplateWriter(onFinish, onFail));
    }

    private boolean scheduleWriteTemplate(TemplateWriter tw) {
        plugin.log(Level.FINER, "Scheduling a TemplateWriter for world '" + this.getName() + "' ...");

        if (tw == null)
            throw new IllegalArgumentException("tw can't be null!");

        int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, tw);
        return id != -1;
    }

    public String toString() {
        return String.format("MVAdventureWorld@%h [world=%s, template=%s, activationdelay=%s, resetdelay=%s]",
                hashCode(), world, template, activationdelay, resetdelay);
    }

    /**
     * Prepares the AdventureWorld for the reset and then schedules the actual reset. (SYNC)
     */
    private class ResetPreparer implements Runnable {
        private AdventureWorld world;

        @Override
        public void run() {
            String name = world.getName();
            String template = world.getTemplate();

            // check
            if (!world.getMVWorld().getCBWorld().getPlayers().isEmpty()) {
                // What the...?
                return;
            }

            plugin.log(Level.INFO, "Beginning reset of world '" + name + "'...");

            // now call the event
            MVAResetEvent resetEvent = new MVAResetEvent(name);
            plugin.getServer().getPluginManager().callEvent(resetEvent);
            if (resetEvent.isCancelled()) {
                plugin.log(Level.INFO, "Reset of world '" + name + "' cancelled.");
                return;
            }

            // everything is OK, let's start:
            // 1. Unload it
            plugin.getCore().getMVWorldManager().unloadWorld(name);

            // The Rest is done async
            int ret = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new ResetWorker(name, template));
            if (ret == -1) // WTF? Scheduling failed???
                plugin.log(Level.SEVERE, "Couldn't schedule a ResetWorker!");
        }

        public ResetPreparer(AdventureWorld world) {
            this.world = world;
        }
    }

    /**
     * Does the actual reset. (ASYNC)
     */
    private class ResetWorker implements Runnable {
        private String name;
        private String template;

        @Override
        public void run() {
            // 2. Remove it
            File serverFolder = new File(plugin.getDataFolder().getAbsolutePath()).getParentFile().getParentFile();
            File worldFile = new File(serverFolder, name);
            FileUtils.deleteFolder(worldFile);
            if (worldFile.exists()) {
                // WTF? Couldn't delete it???
                plugin.log(Level.SEVERE, "Couldn't delete a world!");
                return; // failed...
            }

            // 3. Copy the new world
            File templateFile = new File(serverFolder, template);
            if (!FileUtils.copyFolder(templateFile, worldFile)) {
                // Damn.
                plugin.log(Level.SEVERE, "Couldn't copy a world!");
                return; // failed...
            }

            // Now finish it in the main thread
            int ret = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ResetFinisher(name));
            if (ret == -1) // WTF? Scheduling failed???
                plugin.log(Level.SEVERE, "Couldn't schedule a ResetFinisher!");
        }

        public ResetWorker(String name, String template) {
            this.name = name;
            this.template = template;
        }
    }

    /**
     * Finishes the reset (SYNC)
     */
    private class ResetFinisher implements Runnable {
        private String name;

        @Override
        public void run() {
            // 4. Load the world
            MVAWorldListener.addPass(name);
            plugin.getCore().getMVWorldManager().loadWorld(name);

            plugin.log(Level.INFO, "Reset of world '" + name + "' finished.");

            if (plugin.isPortalsEnabled()) {
                // Reload portals
                plugin.log(Level.INFO, "Reloading Multiverse-Portals to make it use the changed world.");
                plugin.getPortals().reloadConfigs();
            }

            // call the event
            plugin.getServer().getPluginManager().callEvent(new MVAResetFinishedEvent(name));
        }

        public ResetFinisher(String name) {
            this.name = name;
        }
    }

    /**
     * Writes the current state of the world to the template. (ASYNC)
     */
    private class TemplateWriter implements Runnable {
        private final Callable<?> onFinish;
        private final Callable<Void> onFail;

        @Override
        public void run() {
            File serverFolder = new File(plugin.getDataFolder().getAbsolutePath()).getParentFile().getParentFile();
            File worldFile = new File(serverFolder, getName());
            File templateFile = new File(serverFolder, getTemplate());

            // 1. Unload
            plugin.getCore().getMVWorldManager().unloadWorld(getName());
            // 2. Remove template (if exists)
            FileUtils.deleteFolder(templateFile);
            if (templateFile.exists()) {
                // Damn.
                plugin.log(Level.SEVERE, "TemplateWriter: Couldn't delete the template!");
                onFail();
                return; // failed...
            }
            // 3. Copy
            if (!FileUtils.copyFolder(worldFile, templateFile)) {
                // Damn.
                plugin.log(Level.SEVERE, "TemplateWriter: Couldn't copy the template!");
                onFail();
                return; // failed...
            }
            // 4. Load
            plugin.getCore().getMVWorldManager().loadWorld(getName());
            MVAWorldListener.addPass(getName());

            if (onFinish != null) {
                // 5. Notify
                plugin.getServer().getScheduler().callSyncMethod(plugin, onFinish);
            }
        }

        private void onFail() {
            plugin.log(Level.SEVERE, "TemplateWriter: Failed!");
            plugin.getServer().getScheduler().callSyncMethod(plugin, onFail);
        }

        /**
         * Create a new TemplateWriter.
         */
        public TemplateWriter() {
            this.onFinish = null;
            this.onFail = null;
        }

        /**
         * Create a new TemplateWriter that does something after the work is done.
         */
        public TemplateWriter(Callable<Void> onFinish, Callable<Void> onFail) {
            this.onFinish = onFinish;
            this.onFail = onFail;
        }
    }
}
