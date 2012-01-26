package com.onarandombox.MultiverseAdventure.listeners;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.listeners.MultiverseCoreListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MVACoreListener implements Listener {
    private MultiverseAdventure plugin = MultiverseAdventure.getInstance();

    @EventHandler
    public void versionRequest(MVVersionEvent event) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("[Multiverse-Adventure] Multiverse-Adventure Version: %s", plugin.getDescription().getVersion())).append('\n');
        buffer.append(String.format("[Multiverse-Adventure] Loaded AdventureWorlds: %d", plugin.getAdventureWorldsManager().getMVAWorlds().size()));
        buffer.append("Multiverse-Adventure] Dumping loaded AdventureWorlds:");
        for (AdventureWorld aw : plugin.getAdventureWorldsManager().getMVAWorlds()) {
            buffer.append(String.format("[Multiverse-Adventure] %s", aw.toString()));
        }
        event.appendVersionInfo(buffer.toString());
    }

    @EventHandler
    public void configReload(MVConfigReloadEvent event) {
        plugin.reloadConfigs();
        event.addConfig("Multiverse-Adventure - config.yml");
    }

}
