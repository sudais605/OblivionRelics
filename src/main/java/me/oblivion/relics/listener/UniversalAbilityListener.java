package me.oblivion.relics.listener;

import me.oblivion.relics.ability.RelicAbilityEngine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UniversalAbilityListener implements Listener {

    private final RelicAbilityEngine engine;

    private final Map<UUID, Long> lastFPress =
            new HashMap<>();

    private static final long DOUBLE_TAP_TIME = 350L;

    public UniversalAbilityListener(
            RelicAbilityEngine engine
    ) {
        this.engine = engine;
    }

    @EventHandler
    public void onF(PlayerSwapHandItemsEvent event) {

        Player player =
                event.getPlayer();

        event.setCancelled(true);

        long now =
                System.currentTimeMillis();

        long previous =
                lastFPress.getOrDefault(
                        player.getUniqueId(),
                        0L
                );

        if (player.isSneaking()) {

            lastFPress.remove(
                    player.getUniqueId()
            );

            engine.useAbility(
                    player,
                    2
            );

            return;
        }

        if (now - previous <= DOUBLE_TAP_TIME) {

            lastFPress.remove(
                    player.getUniqueId()
            );

            engine.useAbility(
                    player,
                    3
            );

            return;
        }

        lastFPress.put(
                player.getUniqueId(),
                now
        );

        engine.useAbility(
                player,
                1
        );
    }

    @EventHandler
    public void onMeleeAttack(
            EntityDamageByEntityEvent event
    ) {

        Entity damager =
                event.getDamager();

        if (!(damager instanceof Player attacker)) {
            return;
        }

        if (!(event.getEntity()
                instanceof LivingEntity target)) {
            return;
        }

        if (target instanceof Player targetPlayer
                && engine.isTrusted(
                attacker,
                targetPlayer
        )) {
            return;
        }

        if (!engine.isHoldingRelic(attacker)) {
            return;
        }

        if (engine.consumeAutoCrit(attacker)) {

            event.setDamage(
                    event.getDamage() * 1.75
            );

            attacker.getWorld().spawnParticle(
                    org.bukkit.Particle.CRIT,
                    target.getLocation().add(0, 1, 0),
                    22,
                    0.3,
                    0.5,
                    0.3,
                    0.15
            );

            attacker.getWorld().playSound(
                    attacker.getLocation(),
                    org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT,
                    1.0f,
                    1.1f
            );
        }
    }
}
