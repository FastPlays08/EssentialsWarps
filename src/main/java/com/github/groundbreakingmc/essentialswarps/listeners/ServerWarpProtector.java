package com.github.groundbreakingmc.essentialswarps.listeners;

import com.github.groundbreakingmc.essentialswarps.EssentialsWarps;
import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener to prevent players from deleting server-owned warps.
 * <p>
 * This listener ensures that warps designated as server-owned cannot
 * be deleted by regular players, unless they have specific permissions.
 */
public final class ServerWarpProtector implements Listener {

    private final EssentialsWarps plugin;

    /**
     * Constructs a new ServerWarpProtector listener.
     *
     * @param plugin The instance of the EssentialsWarps plugin.
     */
    public ServerWarpProtector(final EssentialsWarps plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the EssentialsWarpDeleteEvent to prevent the deletion of server-owned warps.
     * If the warp is specified as server warp in config.yml and the player lacks the required permissions,
     * the event is cancelled, and an error message is sent to the player.
     *
     * @param event The EssentialsWarpDeleteEvent triggered when a warp deletion is attempted.
     */
    @EventHandler
    public void onDelete(final EssentialsWarpDeleteEvent event) {
        final Player sender = event.getPlayer();
        if (sender.hasPermission("essentils.delwarp.server")
                || !this.plugin.getServerWarps().contains(event.getWarpName())) {
            return;
        }

        event.setCancelled(true);
        sender.sendMessage(this.plugin.getErrorMessageServer());
    }
}
