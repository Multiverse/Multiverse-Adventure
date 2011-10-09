package me.main__.MultiverseAdventureWorlds.listeners;

import me.main__.MultiverseAdventureWorlds.MVAdventureWorldInfo;
import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MVAWPlayerListener extends PlayerListener {
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
		MVAdventureWorldInfo fromWorld = null;
		MVAdventureWorldInfo toWorld = null;
		
		if (fromWorldName != null)
			fromWorld = MultiverseAdventureWorlds.getInstance().getMVAWInfo(fromWorldName);
		
		if (toWorldName != null)
			toWorld = MultiverseAdventureWorlds.getInstance().getMVAWInfo(toWorldName);
		
		handle(fromWorld, toWorld);
	}
	
	private void handle(MVAdventureWorldInfo fromWorld, MVAdventureWorldInfo toWorld) {
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
