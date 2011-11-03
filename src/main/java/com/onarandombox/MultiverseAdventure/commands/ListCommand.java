package com.onarandombox.MultiverseAdventure.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.api.AdventureWorld;

public class ListCommand extends BaseCommand {

    public ListCommand(MultiverseAdventure plugin) {
        super(plugin);
        this.setName("List all currently enabled AdventureWorlds");
        this.setCommandUsage("/mva list");
        this.setArgRange(0, 0);
        this.addKey("mva list");
        this.addKey("mvalist");
        this.setPermission("multiverse.adventure.list", "Lists all currently enabled AdventureWorlds", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {

        Collection<AdventureWorld> worlds = plugin.getAdventureWorldsManager().getMVAWorlds();

        StringBuilder b = new StringBuilder("The following AdventureWorlds are curently enabled: ");
        int index = 0;
        for (AdventureWorld aw : worlds) {
            b.append(aw.getName());

            if (!(index == (worlds.size() - 1))) {
                b.append(", ");
            }

            index++;
        }

        sender.sendMessage(b.toString());
    }

}
