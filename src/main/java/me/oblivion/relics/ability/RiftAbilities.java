package me.oblivion.relics.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RiftAbilities {

    private final AbilityManager abilityManager;

    public RiftAbilities(AbilityManager abilityManager) {
        this.abilityManager = abilityManager;
    }

    public void riftPull(Player player) {

        String ability = "RIFT_PULL";

        if (abilityManager.isOnCooldown(player, ability)) {
            return;
        }

        Location center = player.getLocation();

        for (Player target : player.getWorld().getPlayers()) {

            if (target.equals(player)) {
                continue;
            }

            if (target.getLocation().distanceSquared(center) > 8 * 8) {
                continue;
            }

            if (abilityManager.isTrusted(player, target)) {
                continue;
            }

            Vector pull = center.toVector()
                    .subtract(target.getLocation().toVector())
                    .normalize()
                    .multiply(1.15);

            pull.setY(0.35);

            target.setVelocity(pull);

            target.getWorld().spawnParticle(
                    Particle.PORTAL,
                    target.getLocation().add(0, 1, 0),
                    25,
                    0.35,
                    0.6,
                    0.35,
                    0.08
            );
        }

        center.getWorld().spawnParticle(
                Particle.PORTAL,
                center.clone().add(0, 1, 0),
                45,
                1.0,
                0.8,
                1.0,
                0.12
        );

        player.getWorld().playSound(
                player.getLocation(),
                Sound.BLOCK_PORTAL_AMBIENT,
                0.8f,
                1.5f
        );

        abilityManager.startCooldown(
                player,
                ability,
                8000
        );
    }
}
