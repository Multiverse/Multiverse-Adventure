package me.main__.MultiverseAdventureWorlds.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Called when a world is going to be reset. Cancellable.
 * @author main()
 */
public class MVAWResetEvent extends Event implements Cancellable {
	private static final long serialVersionUID = -8202789420260485333L;
	private boolean cancelled;
	
	private final String world;
	
	public MVAWResetEvent(String worldName) {
		super("MVAWReset");
		world = worldName;
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
	 * @return The world.
	 */
	public String getWorld() {
		return world;
	}
}
