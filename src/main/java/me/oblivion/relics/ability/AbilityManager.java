package me.oblivion.relics.ability;

import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.trust.TrustManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityManager {

    private final RelicManager relicManager;
    private final TrustManager trustManager;

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public AbilityManager(
            RelicManager relicManager,
            TrustManager trustManager
    ) {
        this.relicManager = relicManager;
        this.trustManager = trustManager;
    }

    public boolean isTrusted(Player owner, Player target) {
        return trustManager.isTrusted(
                owner.getUniqueId(),
                target.getUniqueId()
        );
    }

    public RelicType getRelic(Player player) {
        return relicManager.getRelic(
                player.getUniqueId()
        );
    }

    public boolean isOnCooldown(
            Player player,
            String ability
    ) {
        String key =
                player.getUniqueId() + ":" + ability;

        Long end = cooldowns.get(key);

        if (end == null) {
            return false;
        }

        if (System.currentTimeMillis() >= end) {
            cooldowns.remove(key);
            return false;
        }

        return true;
    }

    public long getRemainingCooldown(
            Player player,
            String ability
    ) {
        String key =
                player.getUniqueId() + ":" + ability;

        Long end = cooldowns.get(key);

        if (end == null) {
            return 0;
        }

        long remaining =
                end - System.currentTimeMillis();

        return Math.max(0, remaining);
    }

    public void startCooldown(
            Player player,
            String ability,
            long milliseconds
    ) {
        String key =
                player.getUniqueId() + ":" + ability;

        cooldowns.put(
                key,
                System.currentTimeMillis() + milliseconds
        );
    }

    public void clearCooldowns(UUID uuid) {
        cooldowns.keySet().removeIf(
                key -> key.startsWith(uuid.toString())
        );
    }
}
