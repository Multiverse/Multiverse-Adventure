package me.main__.MultiverseAdventureWorlds.listeners;

import me.main__.MultiverseAdventureWorlds.MultiverseAdventureWorlds;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.event.MVVersionRequestEvent;

public class MVAWConfigReloadListener extends CustomEventListener {
	@Override
	public void onCustomEvent(Event event) {
		if (event.getEventName().equals("MVConfigReload") && event instanceof MVConfigReloadEvent) {
            MultiverseAdventureWorlds.getInstance().reloadConfigs();
            ((MVConfigReloadEvent) event).addConfig("Multiverse-AdventureWorlds - config.yml");
        } else if (event.getEventName().equals("MVVersion") && event instanceof MVVersionRequestEvent) {
            ((MVVersionRequestEvent) event).setPasteBinBuffer(MultiverseAdventureWorlds.getInstance().dumpVersionInfo(((MVVersionRequestEvent) event).getPasteBinBuffer()));
        }
	}
}
