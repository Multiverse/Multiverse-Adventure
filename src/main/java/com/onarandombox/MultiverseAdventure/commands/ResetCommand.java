package com.onarandombox.MultiverseAdventure.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.listeners.MVAResetListener;

public class ResetCommand extends BaseCommand {
	
	public ResetCommand(MultiverseAdventure plugin) {
        super(plugin);
        this.setName("Manually trigger a reset of the specified world.");
        this.setCommandUsage("/mva reset " + ChatColor.GREEN + "[WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mva reset");
        this.addKey("mvareset");
        this.setPermission("multiverse.adventure.reset", "Manually trigger a reset of the specified world.", PermissionDefault.OP);
    }

	@Override
	public void runCommand(final CommandSender sender, List<String> args) {
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
		
		if (plugin.getAdventureWorldsManager().getMVAInfo(world) == null) {
			sender.sendMessage("This world is no AdventureWorld!");
			return;
		}
		
		sender.sendMessage("Resetting world '" + world + "'...");
		plugin.getAdventureWorldsManager().getMVAInfo(world).resetNow();
		MVAResetListener.addTask(world, new Runnable() {
			public void run() {
				sender.sendMessage("Finished.");
			}});
	}

}
