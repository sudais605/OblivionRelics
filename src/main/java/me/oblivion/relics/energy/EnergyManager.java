package me.oblivion.relics.energy;

import me.oblivion.relics.data.PlayerDataManager;

import java.util.UUID;

public class EnergyManager {

    public static final int MIN_ENERGY = 0;
    public static final int MAX_ENERGY = 10;
    public static final int STARTING_ENERGY = 3;

    private final PlayerDataManager playerDataManager;

    public EnergyManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public int getEnergy(UUID uuid) {
        return playerDataManager.getEnergy(uuid);
    }

    public void setEnergy(UUID uuid, int amount) {
        int safeAmount = Math.max(MIN_ENERGY, Math.min(MAX_ENERGY, amount));
        playerDataManager.setEnergy(uuid, safeAmount);
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

    public boolean hasEnergy(UUID uuid, int amount) {
        return getEnergy(uuid) >= amount;
    }

    public boolean canUnlockAbilityTwo(UUID uuid) {
        return getEnergy(uuid) >= 8;
    }

    public boolean canUnlockAbilityThree(UUID uuid) {
        return getEnergy(uuid) >= 10;
    }
}
