package com.onarandombox.MultiverseAdventure.api;

import java.util.concurrent.Callable;

import org.bukkit.configuration.ConfigurationSection;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public interface AdventureWorld {

    /**
     * Saves this to a ConfigurationSection
     */
    public abstract void saveTo(ConfigurationSection config);

    /**
     * @return Whether this AdventureWorld is "active" (contains players that have changed it)
     */
    public abstract boolean isActive();

    /**
     * @return Whether this AdventureWorld is activating (contains players that have changed it but the activationDelay hasn't passed)
     */
    public abstract boolean isActivating();

    /**
     * Sets the activation state DIRECTLY. This is DEPRECATED!
     * 
     * @param active The new activation state.
     * @deprecated Use {@link #scheduleActivation()} instead.
     */
    @Deprecated
    public abstract void setActive(boolean active);

    /**
     * @return Whether this AdventureWorld is resetting (awaiting the reset after the resetDelay has passed)
     */
    public abstract boolean isResetting();

    /**
     * @return The MultiverseWorld that this AdventureWorld represents
     */
    public abstract MultiverseWorld getMVWorld();

    /**
     * @return The name of this AdventureWorld's template-folder
     */
    public abstract String getTemplate();

    /**
     * Sets the name of this AdventureWorld's template-folder. Note: "NAME" is replaced with this world's name.
     * 
     * @param template The new name of the template-folder.
     */
    public abstract void setTemplate(String template);

    /**
     * @return This AdventureWorld's activationDelay
     */
    public abstract int getActivationDelay();

    /**
     * Sets this AdventureWorlds' activationDelay
     * 
     * @param activationDelay The new activationDelay
     */
    public abstract void setActivationDelay(int activationDelay);

    /**
     * @return This AdventureWorld's resetDelay
     */
    public abstract int getResetDelay();

    /**
     * Sets this AdventureWorld's resetDelay
     * 
     * @param resetDelay The new resetDelay
     */
    public abstract void setResetDelay(int resetDelay);

    /**
     * Gets whether this world resets on server restarts or not
     *
     * @return true if world should reset on restart
     */
    public abstract boolean shouldResetOnRestart();

    /**
     * Sets whether this world resets on server restarts
     *
     * @param resetOnRestart Whether to reset on restart.
     */
    public abstract void setResetOnRestart(boolean resetOnRestart);

    /**
     * Schedules a reset
     * 
     * @return If the reset was successfully scheduled
     */
    public abstract boolean scheduleReset();

    /**
     * Resets the AdventureWorld immediately, ignoring the resetdelay.
     */
    public abstract void resetNow();

    /**
     * Cancels a scheduled reset
     */
    public abstract void cancelReset();

    /**
     * Schedules the activation of this AdventureWorld
     */
    public abstract void scheduleActivation();

    /**
     * Cancels the activation of this AdventureWorld
     */
    public abstract void cancelActivation();

    /**
     * This is a convenience method that can be used instead of {@link #getMVWorld()}.getName()
     * 
     * @return The name of the world as a String
     */
    public abstract String getName();

    /**
     * Writes the current state of the world to the template. Useful for initializing.
     * 
     * @return True if success, false if failed.
     */
    public abstract boolean scheduleWriteTemplate();

    /**
     * Writes the current state of the world to the template. Useful for initializing.
     * 
     * @param onFinish A Callable<Void> that's executed on the main thread after the work is done.
     * @return True if success, false if failed.
     * @deprecated Use {@link #scheduleWriteTemplate(Callable<Void>,Callable<Void>)} instead
     */
    @Deprecated
    public abstract boolean scheduleWriteTemplate(Callable<Void> onFinish);

    /**
     * Writes the current state of the world to the template. Useful for initializing.
     * 
     * @param onFinish A Callable<Void> that's executed on the main thread after the work is done.
     * @param onFail A Callable<Void> that's executed on the main thread when something went wrong.
     * @return True if success, false if failed.
     */
    public abstract boolean scheduleWriteTemplate(Callable<Void> onFinish, Callable<Void> onFail);

}