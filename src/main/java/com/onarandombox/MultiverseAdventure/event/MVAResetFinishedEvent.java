package com.onarandombox.MultiverseAdventure.event;

import org.bukkit.event.Event;

/**
 * Called when a world-reset is finished. Not cancellable, obviously.
 * 
 * @author main()
 */
public class MVAResetFinishedEvent extends Event {
    private static final long serialVersionUID = 442869919859148651L;

    private final String world;

    public MVAResetFinishedEvent(String worldName) {
        super("MVAWResetFinished");
        world = worldName;
    }

    /**
     * Gets the world that was reset now.
     * 
     * @return The world.
     */
    public String getWorld() {
        return world;
    }
}
