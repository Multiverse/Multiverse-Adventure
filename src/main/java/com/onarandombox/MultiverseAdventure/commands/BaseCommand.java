package com.onarandombox.MultiverseAdventure.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.pneumaticraft.commandhandler.multiverse.Command;

public abstract class BaseCommand extends Command {
    protected MultiverseAdventure plugin;

    public BaseCommand(MultiverseAdventure plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

}
