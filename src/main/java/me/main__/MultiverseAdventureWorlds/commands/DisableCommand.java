package me.main__.MultiverseAdventureWorlds.commands;

import java.util.List;

import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class DisableCommand extends BaseCommand {
	
	public DisableCommand(MultiverseAdventureWorlds plugin) {
        super(plugin);
        this.setName("Disable AdventureWorlds");
        this.setCommandUsage("/mvaw disable " + ChatColor.GREEN + "[WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mvaw disable");
        this.addKey("mvawdisable");
        this.setPermission("multiverse.adventure.disable", "Converts an AdventureWorld back into a normal world.", PermissionDefault.OP);
    }

	@Override
	public void runCommand(CommandSender sender, List<String> args) {
		String world;
		if (args.isEmpty()) {
			if (sender instanceof Player) {
				world = ((Player) sender).getWorld().getName();
			}
			else {
				sender.sendMessage("If you want me to automatically recognize your world, you'd better be a player ;)");
				return;
			}
		}
		else {
			world = args.get(0);
		}
		
		//checks
		if (plugin.getCore().getMVWorldManager().getMVWorld(world) == null) {
			sender.sendMessage("That world doesn't exist!");
			return;
		}
		
		if (plugin.getMVAWInfo(world) == null) {
			sender.sendMessage("This world is no AdventureWorld!");
			return;
		}
		
		
		plugin.deleteWorld(world);
	}

}
