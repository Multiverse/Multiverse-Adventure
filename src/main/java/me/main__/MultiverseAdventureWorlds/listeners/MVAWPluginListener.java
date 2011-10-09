package me.main__.MultiverseAdventureWorlds.listeners;

import java.util.logging.Level;

import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVAWPluginListener extends ServerListener {
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Multiverse-Core")) {
			MultiverseAdventureWorlds.getInstance().setCore(((MultiverseCore) MultiverseAdventureWorlds.getInstance().getServer().getPluginManager().getPlugin("Multiverse-Core")));
			MultiverseAdventureWorlds.getInstance().getServer().getPluginManager().enablePlugin(MultiverseAdventureWorlds.getInstance());
         } else if (event.getPlugin().getDescription().getName().equals("MultiVerse")) {
            if (event.getPlugin().isEnabled()) {
            	MultiverseAdventureWorlds.getInstance().getServer().getPluginManager().disablePlugin(event.getPlugin());
                MultiverseAdventureWorlds.staticLog(Level.WARNING, "I just disabled the old version of Multiverse for you. You should remove the JAR now, your configs have been migrated.");
            }
        }
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Multiverse-Core")) {
			MultiverseAdventureWorlds.getInstance().setCore(null);
			MultiverseAdventureWorlds.getInstance().getServer().getPluginManager().disablePlugin(MultiverseAdventureWorlds.getInstance());
        }
	}
	
	
}
