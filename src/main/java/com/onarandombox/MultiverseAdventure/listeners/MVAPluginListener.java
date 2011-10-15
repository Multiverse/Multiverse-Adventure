package com.onarandombox.MultiverseAdventure.listeners;

import java.util.logging.Level;


import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVAPluginListener extends ServerListener {
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Multiverse-Core")) {
			MultiverseAdventure.getInstance().setCore(((MultiverseCore) MultiverseAdventure.getInstance().getServer().getPluginManager().getPlugin("Multiverse-Core")));
			MultiverseAdventure.getInstance().getServer().getPluginManager().enablePlugin(MultiverseAdventure.getInstance());
         } else if (event.getPlugin().getDescription().getName().equals("MultiVerse")) {
            if (event.getPlugin().isEnabled()) {
            	MultiverseAdventure.getInstance().getServer().getPluginManager().disablePlugin(event.getPlugin());
                MultiverseAdventure.staticLog(Level.WARNING, "I just disabled the old version of Multiverse for you. You should remove the JAR now, your configs have been migrated.");
            }
        }
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Multiverse-Core")) {
			MultiverseAdventure.getInstance().setCore(null);
			MultiverseAdventure.getInstance().getServer().getPluginManager().disablePlugin(MultiverseAdventure.getInstance());
        }
	}
	
	
}
