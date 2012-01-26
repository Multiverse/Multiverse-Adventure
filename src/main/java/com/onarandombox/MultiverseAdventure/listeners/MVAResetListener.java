package com.onarandombox.MultiverseAdventure.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.event.MVAResetFinishedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MVAResetListener implements Listener {
    private static final HashMap<String, List<Runnable>> resetFinishedTasks = new HashMap<String, List<Runnable>>();

    @EventHandler
    public void resetFinished(MVAResetFinishedEvent event) {
        for (Runnable r : resetFinishedTasks.get(event.getWorld())) {
            MultiverseAdventure.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MultiverseAdventure.getInstance(), r);
        }
    }

    public static void addTask(String world, Runnable task) {
        if (resetFinishedTasks.get(world) == null) {
            resetFinishedTasks.put(world, new ArrayList<Runnable>());
        }
        resetFinishedTasks.get(world).add(task);
    }
}
