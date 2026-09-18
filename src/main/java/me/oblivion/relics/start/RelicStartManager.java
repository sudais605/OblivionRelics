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

        file = new File(plugin.getDataFolder(), "relic-start.yml");

        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    plugin.getLogger().warning("Could not create plugin data folder.");
                }

                if (!file.createNewFile()) {
                    plugin.getLogger().warning("Could not create relic-start.yml.");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create relic-start.yml");
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        started = config.getBoolean("started", false);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;

        config.set("started", started);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save relic-start.yml");
            e.printStackTrace();
        }
    }
}
