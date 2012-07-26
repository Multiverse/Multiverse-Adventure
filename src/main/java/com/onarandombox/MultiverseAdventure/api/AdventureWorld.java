package com.onarandombox.MultiverseAdventure.api;

import com.onarandombox.MultiverseAdventure.MultiverseAdventure;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;
import java.util.concurrent.Callable;

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
     * Gets whether this world resets when no players are left in the world (as long as world is active)
     *
     * @return true if world should reset when empty
     */
    public abstract boolean shouldResetWhenEmpty();

    /**
     * Sets whether this world resets when no players are left in the world.
     *
     * @param resetWhenEmpty Whether to reset when empty
     */
    public abstract void setResetWhenEmpty(boolean resetWhenEmpty);

    /**
     * Gets the cron reset schedule for this world.
     *
     * @return the cron reset schedule as a string.  Empty string means no schedule.
     */
    public abstract String getCronResetSchedule();

    /**
     * Sets the cron reset schedule for this world.  Pass an empty string to disable
     * cron based resets.
     * Format is (min) (hour) (day of month) (month) (day of week)
     * Example: '0 0 * * 0'  -  once a week, midnight on sunday.
     *
     * @param cronResetSchedule The cron reset schedule as a string.  Empty string disables.
     */
    public abstract void setCronResetSchedule(String cronResetSchedule);

    /**
     * Gets the name of buscript script to run prior to reset.
     *
     * @return the name of buscript script to run prior to reset.
     */
    public String getPreResetScript();

    /**
     * Sets the name of the buscript script to run before reset.
     *
     * @param preResetScript the name of the buscript script to run before reset.
     */
    public void setPreResetScript(String preResetScript);

    /**
     * Gets the name of buscript script to run after reset.
     *
     * @return the name of buscript script to run after reset.
     */
    public String getPostResetScript();

    /**
     * Sets the name of the buscript script to run after reset.
     *
     * @param postResetScript the name of the buscript script to run after reset.
     */
    public void setPostResetScript(String postResetScript);

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

    public static class CronResetTask implements Runnable {

        private final MultiverseAdventure plugin;
        private final String world;

        public CronResetTask(final MultiverseAdventure plugin, final String world) {
            this.plugin = plugin;
            this.world = world;
        }

        public void reset() {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this);
        }

        public void run() {
            plugin.getAdventureWorldsManager().getMVAInfo(world).resetNow();
        }
    }

    public class CronResetJob implements Job
    {
        public void execute(JobExecutionContext context)
                throws JobExecutionException {

            Map dataMap = context.getJobDetail().getJobDataMap();
            CronResetTask task = (CronResetTask)dataMap.get("cronResetTask");
            task.reset();
        }
    }

}