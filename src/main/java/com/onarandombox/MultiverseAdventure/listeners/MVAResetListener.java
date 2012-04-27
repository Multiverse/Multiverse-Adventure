package com.onarandombox.MultiverseAdventure.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.event.MVAResetFinishedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MVAResetListener implements Listener {
    private static final HashMap<String, List<Runnable>> resetFinishedTasks = new HashMap<String, List<Runnable>>();
    private static final Set<String> worldsInReset = new HashSet<String>();

    private MultiverseAdventure plugin;

    public MVAResetListener(MultiverseAdventure plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void resetFinished(MVAResetFinishedEvent event) {
        List<Runnable> tasks = resetFinishedTasks.get(event.getWorld());
        if (tasks != null) {
            for (Runnable r : tasks) {
                MultiverseAdventure.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MultiverseAdventure.getInstance(), r);
            }
        }
        removeResettingWorld(event.getWorld());
        if (worldsInReset.isEmpty() && plugin.isPortalsEnabled()) {
            plugin.log(Level.INFO, "Reloading Multiverse-Portals to make it use the changed world(s).");
            plugin.getPortals().reloadConfigs();
        }
    }

    public static void addTask(String world, Runnable task) {
        if (resetFinishedTasks.get(world) == null) {
            resetFinishedTasks.put(world, new LinkedList<Runnable>());
        }
        resetFinishedTasks.get(world).add(task);
    }

    public static void addResettingWorld(String world) {
        worldsInReset.add(world);
    }
    
    public static void removeResettingWorld(String world) {
        worldsInReset.remove(world);
    }
}
