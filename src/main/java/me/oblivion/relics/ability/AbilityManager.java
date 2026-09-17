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

    /*
     * Key format:
     * player-uuid:ability-name
     *
     * Example:
     * 550e8400-e29b-41d4-a716-446655440000:RIFT_PULL
     */
    private final Map<String, Long> cooldowns = new HashMap<>();

    public AbilityManager(
            RelicManager relicManager,
            TrustManager trustManager
    ) {
        this.relicManager = relicManager;
        this.trustManager = trustManager;
    }

    public boolean isTrusted(
            Player owner,
            Player target
    ) {
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

    private String createCooldownKey(
            Player player,
            String ability
    ) {
        return player.getUniqueId()
                .toString()
                + ":"
                + ability.toUpperCase();
    }

    public boolean isOnCooldown(
            Player player,
            String ability
    ) {
        String key = createCooldownKey(
                player,
                ability
        );

        Long endTime = cooldowns.get(key);

        if (endTime == null) {
            return false;
        }

        if (System.currentTimeMillis() >= endTime) {
            cooldowns.remove(key);
            return false;
        }

        return true;
    }

    public long getRemainingCooldown(
            Player player,
            String ability
    ) {
        String key = createCooldownKey(
                player,
                ability
        );

        Long endTime = cooldowns.get(key);

        if (endTime == null) {
            return 0L;
        }

        long remaining =
                endTime - System.currentTimeMillis();

        if (remaining <= 0) {
            cooldowns.remove(key);
            return 0L;
        }

        return remaining;
    }

    public void startCooldown(
            Player player,
            String ability,
            long milliseconds
    ) {
        if (milliseconds <= 0) {
            return;
        }

        String key = createCooldownKey(
                player,
                ability
        );

        cooldowns.put(
                key,
                System.currentTimeMillis()
                        + milliseconds
        );
    }

    public void clearCooldowns(UUID uuid) {
        String prefix = uuid.toString() + ":";

        cooldowns.keySet().removeIf(
                key -> key.startsWith(prefix)
        );
    }

    public void clearCooldown(
            Player player,
            String ability
    ) {
        String key = createCooldownKey(
                player,
                ability
        );

        cooldowns.remove(key);
    }
}
