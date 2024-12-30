package com.github.groundbreakingmc.essentialswarps.listeners;

import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpCreateEvent;
import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiFunction;

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
    private final Set<String> setwarpCommands = ImmutableSet.of("/setwarp", "/esetwarp", "/createwarp", "/ecreatewarp");

    /**
     * A set of command that trigger warp deletion in Essentials.
     */
    private final Set<String> delwarpCommands = ImmutableSet.of("/delwarp", "/edelwarp", "/remwarp", "/eremwarp", "/rmwarp", "/ermwarp");

    /**
     * Handles the PlayerCommandPreprocessEvent to detect and trigger warp-related events.
     * <p>
     * This method processes player commands to determine if they correspond to either warp creation
     * or deletion, and triggers the appropriate custom event based on the command.
     *
     * @param commandEvent The PlayerCommandPreprocessEvent triggered by a player's command input.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent commandEvent) {
        final String buffer = commandEvent.getMessage();
        final String input = this.getCommand(buffer);

        if (this.tryCallCreateEvent(commandEvent, input, buffer)) {
            return;
        }

        this.tryCallDeleteEvent(commandEvent, input, buffer);
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

    /**
     * Attempts to call a warp deletion event based on the player's command.
     *
     * @param commandEvent   The player's command preprocess event.
     * @param input          The separated command input by the player (e.g., "/setwarp" from "/setwarp example").
     * @param buffer         Full command (with args) entered by a player.
     * @return true if the event was successfully triggered or processing stopped, false otherwise.
     */
    private boolean tryCallCreateEvent(final PlayerCommandPreprocessEvent commandEvent,
                                       final String input,
                                       final String buffer) {
        return this.tryCallEvent(
                commandEvent,
                input,
                "essentials.setwarp",
                this.setwarpCommands,
                buffer,
                EssentialsWarpCreateEvent::new
        );
    }

    /**
     * Attempts to call a warp deletion event based on the player's command.
     *
     * @param commandEvent   The player's command preprocess event.
     * @param input          The separated command input by the player (e.g., "/delwarp" from "/delwarp example").
     * @param buffer         Full command (with args) entered by a player.
     * @return true if the event was successfully triggered or processing stopped, false otherwise.
     */
    private boolean tryCallDeleteEvent(final PlayerCommandPreprocessEvent commandEvent,
                                       final String input,
                                       final String buffer) {
        return this.tryCallEvent(
                commandEvent,
                input,
                "essentials.delwarp",
                this.delwarpCommands,
                buffer,
                EssentialsWarpDeleteEvent::new
        );
    }

    /**
     * Attempts to call an event associated with a player's command.
     *
     * @param commandEvent   The player's command preprocess event.
     * @param input          The separated command input by the player (e.g., "/delwarp" from "/delwarp example").
     * @param usePermission  The required permission to use command.
     * @param commands       The set of allowed commands.
     * @param buffer         Full command (with args) entered by a player.
     * @param function       A function to create the event, given the command event and warp name.
     * @return true if the command was handled (event triggered or processing stopped), false otherwise.
     */
    private boolean tryCallEvent(final PlayerCommandPreprocessEvent commandEvent,
                                 final String input,
                                 final String usePermission,
                                 final Set<String> commands,
                                 final String buffer,
                                 final BiFunction<PlayerCommandPreprocessEvent, String, PlayerEvent> function) {
        if (!commands.contains(input)) {
            return false;
        }

        final Player player = commandEvent.getPlayer();
        if (!player.hasPermission(usePermission)) {
            return true;
        }

        final String warpName = this.getWarp(buffer);
        if (warpName == null) {
            return true;
        }

        final PlayerEvent event = function.apply(commandEvent, warpName);
        event.callEvent();
        return true;
    }
}
