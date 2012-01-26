package com.onarandombox.MultiverseAdventure.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a world is going to be reset. Cancellable.
 *
 * @author main()
 */
public class MVAResetEvent extends Event implements Cancellable {
    private static final long serialVersionUID = -8202789420260485333L;
    private boolean cancelled;

    private final String world;

    public MVAResetEvent(String worldName) {
        super("MVAWReset");
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
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the world that is reset now.
     *
     * @return The world.
     */
    public String getWorld() {
        return world;
    }
}
