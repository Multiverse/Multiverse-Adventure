package com.onarandombox.MultiverseAdventure.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.event.MVAWResetFinishedEvent;

public class MVAResetListener extends CustomEventListener {
	private static final HashMap<String, List<Runnable>> resetFinishedTasks = new HashMap<String, List<Runnable>>();
	
	@Override
	public void onCustomEvent(Event event) {
		if (event.getEventName().equals("MVAWResetFinished") && event instanceof MVAWResetFinishedEvent) {
			MVAWResetFinishedEvent myevent = (MVAWResetFinishedEvent) event;
			for (Runnable r : resetFinishedTasks.get(myevent.getWorld())) {
				MultiverseAdventure.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(MultiverseAdventure.getInstance(), r);
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
