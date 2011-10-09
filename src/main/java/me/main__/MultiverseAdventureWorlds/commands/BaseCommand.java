package me.main__.MultiverseAdventureWorlds.commands;

import java.util.List;

import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;

import org.bukkit.command.CommandSender;
import com.pneumaticraft.commandhandler.Command;

/**
 * Convenience class so we don't have to cast each time.
 * @author fernferret
 *
 */
public abstract class BaseCommand extends Command {
	protected MultiverseAdventureWorlds plugin;
	
	public BaseCommand(MultiverseAdventureWorlds plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@Override
	public abstract void runCommand(CommandSender sender, List<String> args);

}
