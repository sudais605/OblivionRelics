package me.oblivion.relics.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {

    private final JavaPlugin plugin;
    private final File dataFolder;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;

        dataFolder = new File(plugin.getDataFolder(), "players");

        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().warning(
                    "Could not create player data folder!"
            );
        }
    }

    // =========================================
    // PLAYER FILE
    // =========================================

    private File getPlayerFile(UUID uuid) {
        return new File(dataFolder, uuid + ".yml");
    }

    private YamlConfiguration loadPlayerFile(UUID uuid) {
        File file = getPlayerFile(uuid);

        if (!file.exists()) {
            return new YamlConfiguration();
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    private void savePlayerFile(UUID uuid, YamlConfiguration config) {
        File file = getPlayerFile(uuid);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe(
                    "Could not save data for player " + uuid
            );
            e.printStackTrace();
        }
    }

    // =========================================
    // ENERGY
    // =========================================

    public int getEnergy(UUID uuid) {
        YamlConfiguration config = loadPlayerFile(uuid);
        return config.getInt("energy", 3);
    }

    public void setEnergy(UUID uuid, int energy) {
        energy = Math.max(0, Math.min(10, energy));

        YamlConfiguration config = loadPlayerFile(uuid);
        config.set("energy", energy);

        savePlayerFile(uuid, config);
    }

    public void addEnergy(UUID uuid, int amount) {
        if (amount <= 0) {
            return;
        }

        setEnergy(uuid, getEnergy(uuid) + amount);
    }

    public void removeEnergy(UUID uuid, int amount) {
        if (amount <= 0) {
            return;
        }

        setEnergy(uuid, getEnergy(uuid) - amount);
    }

    // =========================================
    // RELIC
    // =========================================

    public String getRelic(UUID uuid) {
        YamlConfiguration config = loadPlayerFile(uuid);
        return config.getString("relic", "");
    }

    public void setRelic(UUID uuid, String relic) {
        YamlConfiguration config = loadPlayerFile(uuid);

        config.set("relic", relic);

        savePlayerFile(uuid, config);
    }

    public boolean hasRelic(UUID uuid) {
        String relic = getRelic(uuid);

        return relic != null && !relic.isBlank();
    }

    // =========================================
    // TRUST SYSTEM
    // =========================================

    public List<String> getTrustedPlayers(UUID uuid) {
        YamlConfiguration config = loadPlayerFile(uuid);

        return new ArrayList<>(
                config.getStringList("trusted-players")
        );
    }

    public boolean isTrusted(UUID uuid, UUID targetUuid) {

        if (uuid.equals(targetUuid)) {
            return true;
        }

        return getTrustedPlayers(uuid)
                .contains(targetUuid.toString());
    }

    public boolean addTrustedPlayer(
            UUID uuid,
            UUID targetUuid
    ) {
        if (uuid.equals(targetUuid)) {
            return false;
        }

        YamlConfiguration config = loadPlayerFile(uuid);

        List<String> trusted = new ArrayList<>(
                config.getStringList("trusted-players")
        );

        String target = targetUuid.toString();

        if (trusted.contains(target)) {
            return false;
        }

        trusted.add(target);

        config.set("trusted-players", trusted);

        savePlayerFile(uuid, config);

        return true;
    }

    public boolean removeTrustedPlayer(
            UUID uuid,
            UUID targetUuid
    ) {
        YamlConfiguration config = loadPlayerFile(uuid);

        List<String> trusted = new ArrayList<>(
                config.getStringList("trusted-players")
        );

        String target = targetUuid.toString();

        if (!trusted.remove(target)) {
            return false;
        }

        config.set("trusted-players", trusted);

        savePlayerFile(uuid, config);

        return true;
    }

    // =========================================
    // RESET
    // =========================================

    public void resetPlayer(UUID uuid) {
        File file = getPlayerFile(uuid);

        if (file.exists() && !file.delete()) {
            plugin.getLogger().warning(
                    "Could not reset data for player " + uuid
            );
        }
    }
}
