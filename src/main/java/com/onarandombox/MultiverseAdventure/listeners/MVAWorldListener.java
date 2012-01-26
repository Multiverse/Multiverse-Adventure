package com.onarandombox.MultiverseAdventure.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;

public class MVAWorldListener implements Listener {
    private static final List<String> passes = new ArrayList<String>();

    @EventHandler
    public void worldLoad(WorldLoadEvent event) {
        final WorldLoadEvent fevent = event;
        if (!passes.contains(event.getWorld().getName())) {
            MultiverseAdventure.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MultiverseAdventure.getInstance(), new Runnable() {
                @Override
                public void run() {
                    MultiverseAdventure.getInstance().getAdventureWorldsManager().tryEnableWorld(fevent.getWorld().getName());
                }
            }, 20);
        }
        else {
            MultiverseAdventure.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MultiverseAdventure.getInstance(), new Runnable() {
                @Override
                public void run() {
                    MultiverseAdventure.getInstance().getAdventureWorldsManager().tryEnableWorld(fevent.getWorld().getName(), true); // Without reset here
                }
            }, 20);
            passes.remove(event.getWorld().getName());
        }
    }

    @EventHandler
    public void worldUnload(WorldUnloadEvent event) {
        MultiverseAdventure.getInstance().getAdventureWorldsManager().disableWorld(event.getWorld().getName());
    }

    public static void addPass(String worldname) {
        passes.add(worldname);
    }
}
