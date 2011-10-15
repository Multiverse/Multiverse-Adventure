package com.onarandombox.MultiverseAdventure;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.config.ConfigurationNode;

import com.onarandombox.MultiverseAdventure.event.MVAResetEvent;
import com.onarandombox.MultiverseAdventure.event.MVAResetFinishedEvent;
import com.onarandombox.MultiverseAdventure.listeners.MVAWorldListener;
import com.onarandombox.MultiverseAdventure.util.FileUtils;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * Provides support for "adventure"-worlds
 * @author main()
 */
public final class MVAdventureWorldInfo {
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
	
	public MVAdventureWorldInfo(MultiverseWorld world, MultiverseAdventure plugin, String template, int activationdelay, int resetdelay) {
		this.world = world;
		this.plugin = plugin;
		active = false;
		
		this.setTemplate(template);
		this.setActivationDelay(activationdelay);
		this.setResetDelay(resetdelay);
		
		resetTaskId = -1;
		activationTaskId = -1;
	}
	
	public MVAdventureWorldInfo(MultiverseWorld world, MultiverseAdventure plugin, ConfigurationSection node) {
		this.world = world;
		this.plugin = plugin;
		active = false;
		
		this.setTemplate(node.getString("template", "NAME.template")); // "NAME" will be replaced with the world's name
		this.setActivationDelay(node.getInt("activationdelay", 10));
		this.setResetDelay(node.getInt("resetdelay", 10));
        plugin.saveConfig();
		
		resetTaskId = -1;
		activationTaskId = -1;
	}
	
	public void saveTo(ConfigurationNode config) {
		config.setProperty("enabled", true);
		config.setProperty("template", this.template);
		config.setProperty("activationdelay", activationdelay);
		config.setProperty("resetdelay", resetdelay);
		plugin.saveConfig();
	}

 	public boolean isActive() {
		return active;
	}
	
	public boolean isActivating() {
		if (isActive())
			return false;
		else
			return plugin.getServer().getScheduler().isQueued(activationTaskId);
	}

	/**
	 * Sets the activation state DIRECTLY. This is DEPRECATED!
	 * @param active
	 * The new activation state.
	 * @deprecated Use {@link #scheduleActivation()} instead.
	 */
	@Deprecated
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isResetting() {
		return plugin.getServer().getScheduler().isQueued(resetTaskId); // || plugin.getServer().getScheduler().isCurrentlyRunning(resetTaskId); We don't need to check this since the ResetPreparer runs in the main thread
	}

	public MultiverseWorld getMVWorld() {
		return world;
	}

	public String getTemplate() {
		return template.replaceAll("NAME", world.getName());
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getActivationDelay() {
		return activationdelay;
	}

	public void setActivationDelay(int activationdelay) {
		this.activationdelay = activationdelay;
	}

	public int getResetDelay() {
		return resetdelay;
	}

	public void setResetDelay(int resetdelay) {
		this.resetdelay = resetdelay;
	}

	/**
	 * Schedules a reset
	 * @return If the reset was successfully scheduled
	 */
	public boolean scheduleReset() {
		resetTaskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new ResetPreparer(this), getResetDelay() * 20);
			//  resetdelay is in seconds and with 20 ticks per second we have to take this * 20
		
		return resetTaskId != -1;
	}
	
	/**
	 * Resets the AdventureWorld immediately, ignoring the resetdelay.
	 */
	public void resetNow() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ResetPreparer(this));
	}
	
	/**
	 * Cancels a scheduled reset
	 */
	public void cancelReset() {
		if (isResetting()) {
			plugin.getServer().getScheduler().cancelTask(resetTaskId);
		}
	}
	
	/**
	 * Schedules the activation of this AdventureWorld
	 */
	public void scheduleActivation() {
		if (getActivationDelay() == 0) {
			active = true;
			return;
			// We don't need the scheduler here
		}
		activationTaskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				active = true;
			}},
			getActivationDelay() * 20);
	}
	
	/**
	 * Cancels the activation of this AdventureWorld
	 */
	public void cancelActivation() {
		if (isActivating()) {
			plugin.getServer().getScheduler().cancelTask(activationTaskId);
		}
	}
	
	/**
	 * This is a convenience method that can be used instead of {@link #getMVWorld()}.getName()
	 * @return
	 * The name of the world as a String
	 */
	public String getName() {
		return world.getName();
	}
	
	/**
	 * Writes the current state of the world to the template. Useful for initializing.
	 * @return
	 * True if success, false if failed.
	 */
	public boolean scheduleWriteTemplate() {
		return scheduleWriteTemplate(new TemplateWriter());
	}
	
	/**
	 * Writes the current state of the world to the template. Useful for initializing.
	 * @param sender
	 * A CommandSender that receives a notification after the work is done.
	 * @return
	 * True if success, false if failed.
	 * @deprecated Use {@link #scheduleWriteTemplate(Runnable)} instead
	 */
	public boolean scheduleWriteTemplate(final CommandSender sender) {
		return scheduleWriteTemplate(new Callable<Void>() {
			public Void call() throws Exception {
				sender.sendMessage("Finished.");
				return null;
			}});
	}

	/**
	 * Writes the current state of the world to the template. Useful for initializing.
	 * @param onFinish
	 * A Callable<Void> that's executed after the work is done.
	 * @return
	 * True if success, false if failed.
	 */
	public boolean scheduleWriteTemplate(Callable<Void> onFinish) {
		return scheduleWriteTemplate(new TemplateWriter(onFinish));
	}
	
	private boolean scheduleWriteTemplate(TemplateWriter tw) {
		if (tw == null)
			throw new IllegalArgumentException("tw can't be null!");
		
		int id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, tw);
		return id != -1;
	}
	
	/**
	 * Prepares the name for the reset and then schedules the actual reset. (SYNC)
	 */
	private class ResetPreparer implements Runnable {
		private MVAdventureWorldInfo world;
		
		@Override
		public void run() {
			String name = world.getName();
			String template = world.getTemplate();
			
			//check
			if (!world.getMVWorld().getCBWorld().getPlayers().isEmpty()) {
				// What the...?
				return;
			}
			
			plugin.log(Level.INFO, "Beginning reset of world '" + name + "'...");
			
			//now call the event
			MVAResetEvent resetEvent = new MVAResetEvent(name);
			plugin.getServer().getPluginManager().callEvent(resetEvent);
			if (resetEvent.isCancelled()) {
				plugin.log(Level.INFO, "Reset of world '" + name + "' cancelled.");
				return;
			}
			
			//everything is OK, let's start:
			// 1. Unload it
			plugin.getCore().getMVWorldManager().unloadWorld(name);
			
			// The Rest is done async
			int ret = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new ResetWorker(name, template));
			if (ret == -1) //WTF? Scheduling failed???
				plugin.log(Level.SEVERE, "Couldn't schedule a ResetWorker!");
		}
		
		public ResetPreparer(MVAdventureWorldInfo world) {
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
			boolean deletedWorld = FileUtils.deleteFolder(worldFile);
			if (!deletedWorld) {
				//WTF? Couldn't delete it???
				plugin.log(Level.SEVERE, "Couldn't delete a world!");
				return; //failed...
			}
			
			// 3. Copy the new world
			File templateFile = new File(serverFolder, template);
			if (!FileUtils.copyFolder(templateFile, worldFile)) {
				//Damn.
				plugin.log(Level.SEVERE, "Couldn't copy a world!");
				return; //failed...
			}
			
			// Now finish it in the main thread
			int ret = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new ResetFinisher(name));
			if (ret == -1) //WTF? Scheduling failed???
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
			
			//call the event
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
		
		@Override
		public void run() {
			File serverFolder = new File(plugin.getDataFolder().getAbsolutePath()).getParentFile().getParentFile();
			File worldFile = new File(serverFolder, getName());
			File templateFile = new File(serverFolder, getTemplate());
			
			// 1. Unload
			plugin.getCore().getMVWorldManager().unloadWorld(getName());
			// 2. Remove template (if exists)
			FileUtils.deleteFolder(templateFile);
			// 3. Copy
			FileUtils.copyFolder(worldFile, templateFile);
			// 4. Load
			plugin.getCore().getMVWorldManager().loadWorld(getName());
			
			if (onFinish != null) {
				// 5. Notify
				plugin.getServer().getScheduler().callSyncMethod(plugin, onFinish);
			}
		}
		
		/**
		 * Create a new TemplateWriter.
		 */
		public TemplateWriter() {
			this.onFinish = null;
		}
		
		/**
		 * Create a new TemplateWriter that sends a notification to a client after the work is done.
		 * @param client
		 * The client
		 */
		public TemplateWriter(Callable<Void> onFinish) {
			this.onFinish = onFinish;
		}
	}
}
