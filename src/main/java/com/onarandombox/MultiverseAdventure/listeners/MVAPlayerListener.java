package com.onarandombox.MultiverseAdventure.listeners;


import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.api.AdventureWorld;

public class MVAPlayerListener extends PlayerListener {
	@Override
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        preHandle(event.getFrom().getName(), event.getPlayer().getWorld().getName());
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
        preHandle(null, event.getPlayer().getWorld().getName());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
        preHandle(event.getPlayer().getWorld().getName(), null);
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		preHandle(event.getPlayer().getWorld().getName(), null);
	}
	
	private void preHandle(String fromWorldName, String toWorldName) {
		AdventureWorld fromWorld = null;
		AdventureWorld toWorld = null;
		
		if (fromWorldName != null)
			fromWorld = MultiverseAdventure.getInstance().getAdventureWorldsManager().getMVAInfo(fromWorldName);
		
		if (toWorldName != null)
			toWorld = MultiverseAdventure.getInstance().getAdventureWorldsManager().getMVAInfo(toWorldName);
		
		handle(fromWorld, toWorld);
	}
	
	private void handle(AdventureWorld fromWorld, AdventureWorld toWorld) {
		if (fromWorld != null) {
    		//somebody has left an adventure world ==> check if there's anybody left
    		if (fromWorld.getMVWorld().getCBWorld().getPlayers().isEmpty()) {
    			//nobody left behind! Was the world even set to active?
    			if (fromWorld.isActive()) {
    				//it was activated. *sigh* let's reset it...
    				fromWorld.scheduleReset();
    			}
    			else {
    				fromWorld.cancelActivation();
    			}
    		}
    	}
		if (toWorld != null) {
			if (!(toWorld.isActive() || toWorld.isActivating())) {
				toWorld.scheduleActivation(); //somebody has entered one of the adventure-worlds and it was neither active nor activating ==> schedule its activation
			}
			else if (toWorld.isResetting()) {
				toWorld.cancelReset();
			}
		}
	}
}
