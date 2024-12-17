package com.github.groundbreakingmc.essentialswarps.listeners;

import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class CommandListener implements Listener {

    private final Set<String> delwarpCommands = ImmutableSet.of("/delwarp", "/edelwarp", "/remwarp", "/eremwarp", "/rmwarp", "/ermwarp");

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

    public String getCommand(final String buffer) {
        final int spaceIndex = buffer.indexOf(' ');
        return spaceIndex == -1 ? buffer : buffer.substring(0, spaceIndex);
    }

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
