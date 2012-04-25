package com.onarandombox.MultiverseAdventure.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.event.MVAResetFinishedEvent;
import com.onarandombox.MultiverseAdventure.event.MVAWorldFinishedReloadingEvent;
import org.bukkit.Bukkit;
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
        for (Runnable r : resetFinishedTasks.get(event.getWorld())) {
            MultiverseAdventure.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MultiverseAdventure.getInstance(), r);
        }
    }

    @EventHandler
    public void reloadFinished(MVAWorldFinishedReloadingEvent event) {
        removeResettingWorld(event.getWorld());
        if (worldsInReset.isEmpty() && plugin.isPortalsEnabled()) {
            plugin.log(Level.INFO, "Reloading Multiverse-Portals to make it use the changed world(s).");
            plugin.getPortals().reloadConfigs();
        }
    }

    public static void addTask(String world, Runnable task) {
        if (resetFinishedTasks.get(world) == null) {
            resetFinishedTasks.put(world, new ArrayList<Runnable>());
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
