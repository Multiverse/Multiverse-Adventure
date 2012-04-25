package com.onarandombox.MultiverseAdventure.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a world reset finishes reloading the world with Multiverse-Core.
 */
public class MVAWorldFinishedReloadingEvent extends Event {
    private static final long serialVersionUID = 2342869911259148651L;

    private final String world;

    public MVAWorldFinishedReloadingEvent(String worldName) {
        world = worldName;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
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
