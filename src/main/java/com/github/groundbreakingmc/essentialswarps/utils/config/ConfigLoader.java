package com.github.groundbreakingmc.essentialswarps.utils.config;

import com.github.groundbreakingmc.essentialswarps.EssentialsWarps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class ConfigLoader {

    private final EssentialsWarps plugin;

    public ConfigLoader(final EssentialsWarps plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration get() {
        final File file = new File(this.plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.plugin.saveResource("config.yml", false);
        }

        return YamlConfiguration.loadConfiguration(file);
    }
}
