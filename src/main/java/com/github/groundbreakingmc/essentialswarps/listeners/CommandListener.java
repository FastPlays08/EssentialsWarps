package com.github.groundbreakingmc.essentialswarps.listeners;

import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A listener for handling player commands related to warp deletion.
 * <p>
 * This class intercepts player commands to detect specific warp deletion commands
 * associated with the Essentials plugin. When such a command is executed, it triggers
 * a custom EssentialsWarpDeleteEvent, allowing other plugins or modules to respond to the event.
 */
public final class CommandListener implements Listener {

    /**
     * A set of command that trigger warp deletion in Essentials.
     */
    private final Set<String> delwarpCommands = ImmutableSet.of("/delwarp", "/edelwarp", "/remwarp", "/eremwarp", "/rmwarp", "/ermwarp");

    /**
     * Handles the PlayerCommandPreprocessEvent to detect and trigger a warp delete event
     * when a player executes a warp deletion command.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().hasPermission("essentials.delwarp")) {
            return;
        }

        final String buffer = event.getMessage();
        final String input = this.getCommand(buffer);
        if (!this.delwarpCommands.contains(input)) {
            return;
        }

        final String warpName = this.getWarp(buffer);
        new EssentialsWarpDeleteEvent(event, warpName).callEvent();
    }

    /**
     * Extracts the command portion from the full player input. (with slash)
     *
     * @param buffer The complete input string entered by the player, including all arguments.
     * @return The command with a leading slash, or the entire input if no arguments are present.
     */
    public String getCommand(final String buffer) {
        final int spaceIndex = buffer.indexOf(' ');
        return spaceIndex == -1 ? buffer : buffer.substring(0, spaceIndex);
    }

    /**
     * Extracts the warp name from the player's input.
     *
     * @param buffer The complete input string entered by the player, including the command and arguments.
     * @return The warp name if specified; otherwise, null if no warp name is found.
     */
    @Nullable
    public String getWarp(final String buffer) {
        final int spaceIndex = buffer.indexOf(' ');
        if (spaceIndex == -1) {
            return null;
        }

        final int spaceLastIndex = buffer.lastIndexOf(' ');
        if (spaceLastIndex == spaceIndex) {
            return buffer.substring(spaceIndex + 1);
        }

        return buffer.substring(spaceIndex + 1, spaceLastIndex);
    }
}
