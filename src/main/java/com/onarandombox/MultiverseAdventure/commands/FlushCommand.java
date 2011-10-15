package com.onarandombox.MultiverseAdventure.commands;

import java.util.List;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;

public class FlushCommand extends BaseCommand {
	
	public FlushCommand(MultiverseAdventure plugin) {
        super(plugin);
        this.setName("Write your changes to the template");
        this.setCommandUsage("/mvaw write " + ChatColor.GREEN + "[WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mvaw write");
        this.addKey("mvawwrite");
        this.addKey("mvaw write template");
        this.addKey("mvaw writetemplate");
        this.addKey("mvawwrite template");
        this.addKey("mvawwritetemplate");
        this.addKey("mvaw flush");
        this.addKey("mvawflush");
        this.setPermission("multiverse.adventure.flush", "Writes the the current state of an AdventureWorld to the template.", PermissionDefault.OP);
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
		
		if (plugin.getMVAInfo(world) == null) {
			sender.sendMessage("This world is no AdventureWorld!");
			return;
		}
		
		plugin.flushWorld(world, sender);
	}

}
