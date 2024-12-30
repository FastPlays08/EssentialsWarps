package com.github.groundbreakingmc.essentialswarps.listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.github.groundbreakingmc.essentialswarps.EssentialsWarps;
import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpCreateEvent;
import com.github.groundbreakingmc.essentialswarps.utils.colorizer.Colorizer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * Manages the limits for warp creation per player group.
 * <p>
 * This listener ensures that players cannot create more warps than their group's configured limit.
 * It dynamically adjusts event registration based on configuration and uses caching for performance.
 */
public final class LimitManager implements Listener {

    private final EssentialsWarps plugin;
    private final Essentials essentials;
    private boolean registered; // Indicates whether the listener is currently registered

    // Message sent to the player when they reach the warp limit
    private String reachedLimitMessage;
    // Default warp limit for groups not explicitly defined in the configuration
    private int defaultWarpLimit;
    // Stores warp limits for each group (group name -> limit)
    private final Object2IntOpenHashMap<String> limits = new Object2IntOpenHashMap<>();

    /**
     * Constructs a LimitManager instance.
     *
     * @param plugin The main plugin instance.
     */
    public LimitManager(final EssentialsWarps plugin) {
        this.plugin = plugin;
        this.essentials = plugin.getEssentials();
    }

    /**
     * Handles the EssentialsWarpCreateEvent to enforce warp creation limits.
     * <p>
     * Cancels the event if the player exceeds the allowed number of warps based on their group.
     *
     * @param event The EssentialsWarpCreateEvent triggered when a player attempts to create a warp.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCreate(final EssentialsWarpCreateEvent event) {
        final Player sender = event.getPlayer();
        if (sender.hasPermission("essentials.setwarp.bypasslimit")) {
            return;
        }

        final UUID senderUUID = sender.getUniqueId();
        int playerWarpCount = 0;

        try {
            for (final String warpName : this.essentials.getWarps().getList()) {
                final UUID lastOwnerUUID = essentials.getWarps().getLastOwner(warpName);
                if (lastOwnerUUID.equals(senderUUID)) {
                    playerWarpCount++;
                }
            }
        } catch (final WarpNotFoundException ignored) {}

        if (playerWarpCount == 0) {
            return;
        }

        final String group = this.plugin.getPerms().getPrimaryGroup(sender);
        final int limit = this.limits.getOrDefault(group, defaultWarpLimit);

        if (playerWarpCount >= limit) {
            event.setCancelled(true);
            sender.sendMessage(this.reachedLimitMessage.replace("{limit}", String.valueOf(limit)));
        }
    }

    /**
     * Configures the LimitManager based on the plugin's configuration file.
     * <p>
     * Dynamically registers or unregisters the listener based on the "limits.enable" setting.
     * Also initializes group-specific warp limits and the default limit.
     *
     * @param config The configuration file to load settings from.
     * @param colorizer Utility to apply color codes to messages.
     */
    public void setupValues(final FileConfiguration config, final Colorizer colorizer) {
        final ConfigurationSection limitsSection = config.getConfigurationSection("limits");
        if (!limitsSection.getBoolean("enable")) {
            if (registered) {
                HandlerList.unregisterAll(this);
                registered = false;
            }
            return;
        }

        if (!registered) {
            this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
            registered = true;
        }

        this.reachedLimitMessage = colorizer.colorize(limitsSection.getString("reached-limit"));
        this.defaultWarpLimit = limitsSection.getInt("default");
        limitsSection.getConfigurationSection("groups").getValues(false).forEach((key, value) -> {
            this.limits.put(key, (int) value);
        });
    }
}
