package me.main__.MultiverseAdventure.listeners;

import me.main__.MultiverseAdventure.MultiverseAdventure;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.event.MVVersionRequestEvent;

public class MVAConfigReloadListener extends CustomEventListener {
	@Override
	public void onCustomEvent(Event event) {
		if (event.getEventName().equals("MVConfigReload") && event instanceof MVConfigReloadEvent) {
            MultiverseAdventure.getInstance().reloadConfigs();
            ((MVConfigReloadEvent) event).addConfig("Multiverse-Adventure - config.yml");
        } else if (event.getEventName().equals("MVVersion") && event instanceof MVVersionRequestEvent) {
            ((MVVersionRequestEvent) event).setPasteBinBuffer(MultiverseAdventure.getInstance().dumpVersionInfo(((MVVersionRequestEvent) event).getPasteBinBuffer()));
        }
	}
}
