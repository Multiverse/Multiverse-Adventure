package com.onarandombox.MultiverseAdventure.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;

public class EnableCommand extends BaseCommand {

    public EnableCommand(MultiverseAdventure plugin) {
        super(plugin);
        this.setName("Enable Adventure");
        this.setCommandUsage("/mva enable " + ChatColor.GREEN + "[WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mva enable");
        this.addKey("mvaenable");
        this.setPermission("multiverse.adventure.enable", "Converts a world into an adventure world.", PermissionDefault.OP);
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

        // checks
        if (plugin.getAdventureWorldsManager().getMVAInfo(world) != null) {
            sender.sendMessage("This world is already an AdventureWorld!");
            return;
        }

        if (plugin.getCore().getMVWorldManager().getMVWorld(world) == null) {
            sender.sendMessage("That world doesn't exist!");
            return;
        }

        plugin.getAdventureWorldsManager().createWorldWithNotifications(world, sender);
    }

}
