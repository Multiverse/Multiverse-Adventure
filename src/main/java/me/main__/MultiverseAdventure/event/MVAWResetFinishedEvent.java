package me.main__.MultiverseAdventure.event;

import org.bukkit.event.Event;

/**
 * Called when a world-reset is finished. Not cancellable, obviously.
 * @author main()
 */
public class MVAWResetFinishedEvent extends Event {
	private static final long serialVersionUID = 442869919859148651L;
	
	private final String world;
	
	public MVAWResetFinishedEvent(String worldName) {
		super("MVAWResetFinished");
		world = worldName;
	}

	/**
	 * Gets the world that was reset now.
	 * @return The world.
	 */
	public String getWorld() {
		return world;
	}
}
