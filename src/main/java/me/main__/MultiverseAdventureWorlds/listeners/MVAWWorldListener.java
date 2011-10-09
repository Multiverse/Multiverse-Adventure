package me.main__.MultiverseAdventureWorlds.listeners;

import java.util.ArrayList;
import java.util.List;
import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class MVAWWorldListener extends WorldListener {
	private static final List<String> passes = new ArrayList<String>();
		
	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		final WorldLoadEvent fevent = event;
		if (!passes.contains(event.getWorld().getName())) {
			MultiverseAdventureWorlds.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
							MultiverseAdventureWorlds.getInstance(),
							new Runnable() {
								@Override
								public void run() {
									MultiverseAdventureWorlds.getInstance()
											.tryEnableWorld(fevent.getWorld().getName());
								}
							}, 20);
		}
		else {
			MultiverseAdventureWorlds.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
					MultiverseAdventureWorlds.getInstance(),
					new Runnable() {
						@Override
						public void run() {
							MultiverseAdventureWorlds.getInstance()
									.tryEnableWorld(fevent.getWorld().getName(), true); //Without reset here
						}
					}, 20);			
			passes.remove(event.getWorld().getName());
		}
	}

	@Override
	public void onWorldUnload(WorldUnloadEvent event) {
		MultiverseAdventureWorlds.getInstance().disableWorld(event.getWorld().getName());
	}
	
	public static void addPass(String worldname) {
		passes.add(worldname);
	}
}
