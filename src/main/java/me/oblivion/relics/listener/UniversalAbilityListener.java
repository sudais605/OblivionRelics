package me.oblivion.relics.listener;

import me.oblivion.relics.ability.RelicAbilityEngine;
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

    public UniversalAbilityListener(
            RelicAbilityEngine engine
    ) {
        this.engine = engine;
    }

    @EventHandler
    public void onSwapHand(
            PlayerSwapHandItemsEvent event
    ) {

        Player player =
                event.getPlayer();

        event.setCancelled(true);

        // SHIFT + F = Ability II
        if (player.isSneaking()) {
            engine.useAbility(player, 2);
            return;
        }

        // Normal F / double-tap F
        long now =
                System.currentTimeMillis();

        Long previous =
                lastFPress.get(
                        player.getUniqueId()
                );

        if (previous != null
                && now - previous <= 350) {

            // Double-tap F = Ability III
            lastFPress.remove(
                    player.getUniqueId()
            );

            engine.useAbility(player, 3);
            return;
        }

        lastFPress.put(
                player.getUniqueId(),
                now
        );

        // Ability I
        engine.useAbility(player, 1);
    }

    @EventHandler
    public void onMeleeAttack(
            EntityDamageByEntityEvent event
    ) {

        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!engine.consumeAutoCrit(attacker)) {
            return;
        }

        event.setDamage(
                event.getDamage() * 1.75
        );

        attacker.getWorld().spawnParticle(
                org.bukkit.Particle.CRIT,
                attacker.getLocation().add(0, 1, 0),
                35,
                0.3,
                0.4,
                0.3,
                0.08
        );

        attacker.getWorld().playSound(
                attacker.getLocation(),
                org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT,
                1.0f,
                1.5f
        );
    }
}
