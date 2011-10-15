package me.main__.MultiverseAdventure.commands;

import java.util.List;

import me.main__.MultiverseAdventure.MultiverseAdventure;

import org.bukkit.command.CommandSender;
import com.pneumaticraft.commandhandler.Command;

/**
 * Convenience class so we don't have to cast each time.
 * @author fernferret
 *
 */
public abstract class BaseCommand extends Command {
	protected MultiverseAdventure plugin;
	
	public BaseCommand(MultiverseAdventure plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@Override
	public abstract void runCommand(CommandSender sender, List<String> args);

}
