package com.onarandombox.MultiverseAdventure.api;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;


public interface AdventureWorldsManager {

	/**
	 * Gets the MVAdventureWorld-Object of a world.
	 * @param name
	 * The name of the world.
	 * @return
	 * The MVAdventureWorld-Object
	 */
	public abstract AdventureWorld getMVAInfo(String name);

	/**
	 * Cycles through all loaded worlds and loads those into MVAW, who are AdventureWorlds.
	 */
	public abstract void loadWorlds();

	/**
	 * Disables all AdventureWorlds
	 */
	public abstract void unloadWorlds();

	/**
	 * Tries to enable an AdventureWorld that's already known to Multiverse-AdventureWorlds.
	 * @param name
	 * The name of that world.
	 * @return
	 * True if success, false if failed.
	 */
	public abstract boolean tryEnableWorld(String name);

	/**
	 * Tries to enable an AdventureWorld that's already known to Multiverse-AdventureWorlds.
	 * @param name
	 * The name of that world.
	 * @param noreset
	 * If the world shouldn't be reset.
	 * @return
	 * True if success, false if failed.
	 */
	public abstract boolean tryEnableWorld(String name, boolean noreset);

	/**
	 * Tries to disable an AdventureWorld.
	 * @param name
	 * The name of the world.
	 * @return
	 * True if success, false if failed.
	 */
	public abstract boolean disableWorld(String name);

	/**
	 * Converts a normal world into an AdventureWorld
	 * @param name
	 * The name of the world
	 * @return
	 * True if success, false if failed.
	 */
	public abstract boolean createWorld(String name);

	/**
	 * Converts a normal world into an AdventureWorld and sends notifications to a CommandSender
	 * @param name
	 * The name of the world
	 * @param sender
	 * The CommandSender that receives the notifications
	 */
	public abstract void createWorldWithNotifications(String name,
			CommandSender sender);

	/**
	 * Converts an AdventureWorld back into a normal world.
	 * @param name
	 * The name of the world.
	 */
	public abstract void deleteWorld(final String name);

	/**
	 * Converts an AdventureWorld back into a normal world and sends notifications to a CommandSender.
	 * @param name
	 * The name of the world.
	 * @param sender
	 * The CommandSender that receives notifications
	 */
	public abstract void deleteWorld(final String name,
			final CommandSender sender);

	/**
	 * Writes the current state of an AdventureWorld to the template.
	 * @param name
	 * The name of the world.
	 */
	public abstract void flushWorld(String name);

	/**
	 * Writes the current state of an AdventureWorld to the template and sends notifications to a CommandSender
	 * @param name
	 * The name of the world.
	 * @param sender
	 * The CommandSender that receives notifications
	 * @return
	 * True if success, false if failed.
	 */
	public abstract boolean flushWorld(String name, CommandSender sender);
	
	/**
	 * Saves all AdventureWorlds to the given ConfigurationSection
	 */
	public abstract void saveAllTo(ConfigurationSection config);

}