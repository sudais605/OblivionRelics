package me.oblivion.relics.trust;

import me.oblivion.relics.data.PlayerDataManager;

import java.util.List;
import java.util.UUID;

public class TrustManager {

    private final PlayerDataManager playerDataManager;

    public TrustManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public boolean isTrusted(UUID owner, UUID target) {
        if (owner.equals(target)) {
            return true;
        }

        return playerDataManager.isTrusted(owner, target);
    }

    public boolean addTrust(UUID owner, UUID target) {
        return playerDataManager.addTrustedPlayer(owner, target);
    }

    public boolean removeTrust(UUID owner, UUID target) {
        return playerDataManager.removeTrustedPlayer(owner, target);
    }

    public List<String> getTrustedPlayers(UUID owner) {
        return playerDataManager.getTrustedPlayers(owner);
    }
}
