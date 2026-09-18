package me.oblivion.relics.start;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class RelicStartManager {

    private final JavaPlugin plugin;
    private final File file;

    private YamlConfiguration config;
    private boolean started;

    public RelicStartManager(JavaPlugin plugin) {

        this.plugin = plugin;

        file = new File(
                plugin.getDataFolder(),
                "relic-start.yml"
        );

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe(
                        "Could not create relic-start.yml"
                );
                e.printStackTrace();
            }
        }

        config =
                YamlConfiguration.loadConfiguration(file);

        started =
                config.getBoolean(
                        "started",
                        false
                );
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {

        this.started = started;

        config.set(
                "started",
                started
        );

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe(
                    "Could not save relic-start.yml"
            );
            e.printStackTrace();
        }
    }
}
