package com.onarandombox.MultiverseAdventure.listeners;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseAdventure.api.AdventureWorld;
import com.onarandombox.MultiverseCore.event.MVConfigReloadEvent;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.listeners.MultiverseCoreListener;

public class MVACoreListener extends MultiverseCoreListener {
    private MultiverseAdventure plugin = MultiverseAdventure.getInstance();

    public void onVersionRequest(MVVersionEvent event) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("[Multiverse-Adventure] Multiverse-Adventure Version: %s", plugin.getDescription().getVersion())).append('\n');
        buffer.append(String.format("[Multiverse-Adventure] Loaded AdventureWorlds: %d", plugin.getAdventureWorldsManager().getMVAWorlds().size()));
        buffer.append("Multiverse-Adventure] Dumping loaded AdventureWorlds:");
        for (AdventureWorld aw : plugin.getAdventureWorldsManager().getMVAWorlds()) {
            buffer.append(String.format("[Multiverse-Adventure] %s", aw.toString()));
        }
        event.appendVersionInfo(buffer.toString());
    }

    public void onMVConfigReload(MVConfigReloadEvent event) {
        plugin.reloadConfigs();
        event.addConfig("Multiverse-Adventure - config.yml");
    }

}
