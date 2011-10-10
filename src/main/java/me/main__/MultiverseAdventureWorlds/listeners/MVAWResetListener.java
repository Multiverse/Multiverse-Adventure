package me.main__.MultiverseAdventureWorlds.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;
import me.main__.MultiverseAdventureWorlds.event.MVAWResetFinishedEvent;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public class MVAWResetListener extends CustomEventListener {
	private static final HashMap<String, List<Runnable>> resetFinishedTasks = new HashMap<String, List<Runnable>>();
	
	@Override
	public void onCustomEvent(Event event) {
		if (event.getEventName().equals("MVAWResetFinished") && event instanceof MVAWResetFinishedEvent) {
			MVAWResetFinishedEvent myevent = (MVAWResetFinishedEvent) event;
			for (Runnable r : resetFinishedTasks.get(myevent.getWorld())) {
				MultiverseAdventureWorlds.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(MultiverseAdventureWorlds.getInstance(), r);
			}
		}
	}
	
	public static void addTask(String world, Runnable task) {
		if (resetFinishedTasks.get(world) == null) {
			resetFinishedTasks.put(world, new ArrayList<Runnable>());
		}
		
		resetFinishedTasks.get(world).add(task);
	}
}
