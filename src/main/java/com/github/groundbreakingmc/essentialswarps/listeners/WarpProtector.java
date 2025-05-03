package com.github.groundbreakingmc.essentialswarps.listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.github.groundbreakingmc.essentialswarps.EssentialsWarps;
import com.github.groundbreakingmc.essentialswarps.events.EssentialsWarpDeleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener to restrict players from deleting warps they do not own.
 * <p>
 * This class ensures that only the owner of a warp or a player with specific permissions
 * can delete a warp. If the player lacks ownership or permissions, the deletion is cancelled,
 * and an error message is sent to the player.
 */
public final class WarpProtector implements Listener {

    private final EssentialsWarps plugin;
    private final Essentials essentials;

    /**
     * Constructs a new WarpProtector listener.
     *
     * @param plugin The instance of the EssentialsWarps plugin.
     */
    public WarpProtector(final EssentialsWarps plugin) {
        this.plugin = plugin;
        this.essentials = plugin.getEssentials();
    }

    /**
     * Handles the EssentialsWarpDeleteEvent to prevent not allowed warp deletion.
     * <p>
     * If the player attempting to delete a warp is not its owner and lacks the required
     * permissions, the event is cancelled, and an error message is sent to the player.
     *
     * @param event The EssentialsWarpDeleteEvent triggered when a warp deletion is attempted.
     * */
    @EventHandler(ignoreCancelled = true)
    public void onDelete(final EssentialsWarpDeleteEvent event) {
        final Player sender = event.getPlayer();

        if (sender.hasPermission("essentialsWarps.delwarp.all")) {
            return;
        }

        try {
            if (event.getLastOwner(this.essentials).equals(sender.getUniqueId())) {
                return;
            }
        } catch (final WarpNotFoundException ignored) {
            return;
        }

        event.setCancelled(true);
        sender.sendMessage(this.plugin.getErrorMessage());
    }
}
