package com.onarandombox.MultiverseAdventure.listeners;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class MVAPlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerChangedWorld(PlayerChangedWorldEvent event) {
        handle(event.getFrom().getName(), event.getPlayer().getWorld().getName(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        handle(null, event.getPlayer().getWorld().getName(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        handle(event.getPlayer().getWorld().getName(), null, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerKick(PlayerKickEvent event) {
        handle(event.getPlayer().getWorld().getName(), null, event.getPlayer());
    }

    private void handle(String fromWorldName, String toWorldName, Player player) {
        AdventureWorld fromWorld = null;
        AdventureWorld toWorld = null;

        if (fromWorldName != null)
            fromWorld = MultiverseAdventure.getInstance().getAdventureWorldsManager().getMVAInfo(fromWorldName);

        if (toWorldName != null)
            toWorld = MultiverseAdventure.getInstance().getAdventureWorldsManager().getMVAInfo(toWorldName);

        handle(fromWorld, toWorld, player);
    }

    private void handle(AdventureWorld fromWorld, AdventureWorld toWorld, Player player) {
        if (fromWorld != null) {
            // somebody has left an adventure world ==> check if there's anybody left
            List<Player> playersInWorld = fromWorld.getMVWorld().getCBWorld().getPlayers();
            if (playersInWorld.isEmpty()
                    || (player != null && playersInWorld.size() == 1 && playersInWorld.get(0).equals(player))) {
                // nobody left behind! Was the world even set to active?
                if (fromWorld.isActive()) {
                    if (fromWorld.shouldResetWhenEmpty()) {
                        // it was activated and should reset when empty. *sigh* let's reset it...
                        fromWorld.scheduleReset();
                    }
                }
                else {
                    fromWorld.cancelActivation();
                }
            }
        }
        if (toWorld != null) {
            if (!(toWorld.isActive() || toWorld.isActivating())) {
                toWorld.scheduleActivation(); // somebody has entered one of the adventure-worlds and it was neither active nor activating ==> schedule its activation
            }
            else if (toWorld.isResetting()) {
                toWorld.cancelReset();
            }
        }
    }
}
