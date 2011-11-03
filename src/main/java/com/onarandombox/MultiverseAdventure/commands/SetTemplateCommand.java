package com.onarandombox.MultiverseAdventure.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;

public class SetTemplateCommand extends BaseCommand {

    public SetTemplateCommand(MultiverseAdventure plugin) {
        super(plugin);
        this.setName("Set the template-folder");
        this.setCommandUsage("/mva set template " + ChatColor.GREEN + "{TEMPLATE} [WORLD]");
        this.setArgRange(1, 2);
        this.addKey("mva set template");
        this.addKey("mvaset template");
        this.addKey("mvasettemplate");
        this.addKey("mva settemplate");
        this.setPermission("multiverse.adventure.set.template", "Sets the template-folder", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String newtemplate = args.get(0);
        String world;
        if (args.size() < 2) {
            if (sender instanceof Player) {
                world = ((Player) sender).getWorld().getName();
            }
            else {
                sender.sendMessage("If you want me to automatically recognize your world, you'd better be a player ;)");
                return;
            }
        }
        else {
            world = args.get(1);
        }

        // checks
        if (plugin.getCore().getMVWorldManager().getMVWorld(world) == null) {
            sender.sendMessage("That world doesn't exist!");
            return;
        }

        if (plugin.getAdventureWorldsManager().getMVAInfo(world) == null) {
            sender.sendMessage("This world is no AdventureWorld!");
            return;
        }

        plugin.getAdventureWorldsManager().getMVAInfo(world).setTemplate(newtemplate);

        sender.sendMessage("Successfully set template to \"" + newtemplate + "\"!");
    }

}
