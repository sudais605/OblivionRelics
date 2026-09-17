package me.oblivion.relics.ability;

import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.trust.TrustManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RelicAbilityEngine {

    private final JavaPlugin plugin;
    private final RelicManager relicManager;
    private final EnergyManager energyManager;
    private final TrustManager trustManager;

    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> autoCrit = new HashMap<>();
    private final Map<UUID, UUID> soulMarks = new HashMap<>();
    private final Map<UUID, Deque<Location>> history = new HashMap<>();

    public RelicAbilityEngine(
            JavaPlugin plugin,
            RelicManager relicManager,
            EnergyManager energyManager,
            TrustManager trustManager
    ) {
        this.plugin = plugin;
        this.relicManager = relicManager;
        this.energyManager = energyManager;
        this.trustManager = trustManager;

        startHistoryTracker();
    }

    // =========================================================
    // MAIN ABILITY ENTRY
    // =========================================================

    public boolean useAbility(Player player, int slot) {

        RelicType relic =
                relicManager.getRelic(player.getUniqueId());

        if (relic == null) {
            player.sendMessage(
                    "§b§lOblivion §8» §cYou don't have a Relic."
            );
            return false;
        }

        if (slot < 1 || slot > 3) {
            return false;
        }

        if (slot == 2
                && energyManager.getEnergy(
                player.getUniqueId()) < 8) {

            player.sendMessage(
                    "§b§lOblivion §8» §cAbility II requires 8 Energy."
            );
            return false;
        }

        if (slot == 3
                && energyManager.getEnergy(
                player.getUniqueId()) < 10) {

            player.sendMessage(
                    "§b§lOblivion §8» §cAbility III requires 10 Energy."
            );
            return false;
        }

        String id =
                relic.name() + "_" + slot;

        if (isOnCooldown(player, id)) {
            return false;
        }

        switch (relic) {

            case RIFT -> useRift(player, slot);
            case GRAVITY -> useGravity(player, slot);
            case VOID -> useVoid(player, slot);
            case STORM -> useStorm(player, slot);
            case FROST -> useFrost(player, slot);
            case INFERNO -> useInferno(player, slot);
            case SHADOW -> useShadow(player, slot);
            case TIME -> useTime(player, slot);
            case PHANTOM -> usePhantom(player, slot);
            case SOUL -> useSoul(player, slot);
            case CHAOS -> useChaos(player, slot);
            case CELESTIAL -> useCelestial(player, slot);
            case AEGIS -> useAegis(player, slot);
            case FORCE -> useForce(player, slot);
            case ARCANE -> useArcane(player, slot);
        }

        return true;
    }

    // =========================================================
    // RIFT
    // =========================================================

    private void useRift(Player p, int slot) {

        if (slot == 1) {
            List<Player> targets = nearby(p, 8);

            for (Player target : targets) {
                pull(target, p.getLocation(), 1.15, 0.30);
            }

            particles(p.getLocation(), Particle.PORTAL, 55, 1.2);
            sound(p, Sound.BLOCK_PORTAL_AMBIENT, 0.8f, 1.6f);
            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {
            dashThrough(
                    p,
                    6.0,
                    3.5,
                    4.0,
                    Particle.PORTAL
            );

            sound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.4f);
            cooldown(p, 2, 14);
            return;
        }

        Location center = targetLocation(p, 10);

        if (center == null) {
            center = p.getLocation().add(
                    p.getLocation().getDirection().normalize().multiply(6)
            );
        }

        ring(center, Particle.PORTAL, 2.0, 45);

        for (Player target : playersNear(center, 5)) {
            pull(target, center, 1.2, 0.35);
            damage(p, target, 5.0);
        }

        particles(center, Particle.EXPLOSION, 3, 0.3);
        sound(p, Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.4f);
        cooldown(p, 3, 24);
    }

    // =========================================================
    // GRAVITY
    // =========================================================

    private void useGravity(Player p, int slot) {

        if (slot == 1) {
            Location center = p.getLocation();

            for (Player target : nearby(p, 9)) {
                pull(target, center, 0.95, 0.55);
                damage(p, target, 2.5);
            }

            ring(center, Particle.END_ROD, 3.0, 35);
            sound(p, Sound.BLOCK_BEACON_ACTIVATE, 0.7f, 0.7f);
            cooldown(p, 1, 9);
            return;
        }

        if (slot == 2) {
            for (Player target : nearby(p, 7)) {
                if (trusted(p, target)) {
                    continue;
                }

                Vector v = target.getVelocity();
                v.setY(Math.max(1.15, v.getY() + 1.0));
                target.setVelocity(v);

                particles(
                        target.getLocation(),
                        Particle.END_ROD,
                        20,
                        0.25
                );
            }

            sound(p, Sound.BLOCK_BEACON_ACTIVATE, 0.9f, 1.4f);
            cooldown(p, 2, 15);
            return;
        }

        for (Player target : nearby(p, 6)) {

            if (trusted(p, target)) {
                continue;
            }

            target.setVelocity(
                    target.getVelocity().setY(-1.2)
            );

            damage(p, target, 4.5);
        }

        ring(p.getLocation(), Particle.CLOUD, 3.5, 55);
        sound(p, Sound.ENTITY_GENERIC_EXPLODE, 0.65f, 0.65f);
        cooldown(p, 3, 25);
    }

    // =========================================================
    // VOID
    // =========================================================

    private void useVoid(Player p, int slot) {

        if (slot == 1) {
            blinkForward(p, 6);
            particles(p.getLocation(), Particle.REVERSE_PORTAL, 35, 0.6);
            sound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.7f);
            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {
            Player target = targetPlayer(p, 10);

            if (target == null) {
                return;
            }

            if (!trusted(p, target)) {
                target.setVelocity(new Vector(0, 0, 0));
                target.addPotionEffect(
                        new PotionEffect(
                                PotionEffectType.SLOWNESS,
                                50,
                                5,
                                false,
                                false,
                                true
                        )
                );

                particles(
                        target.getLocation(),
                        Particle.REVERSE_PORTAL,
                        35,
                        0.35
                );
            }

            sound(p, Sound.BLOCK_PORTAL_AMBIENT, 0.8f, 0.5f);
            cooldown(p, 2, 16);
            return;
        }

        Location center = targetLocation(p, 12);

        if (center == null) {
            center = p.getLocation().add(
                    p.getLocation().getDirection().normalize().multiply(6)
            );
        }

        for (Player target : playersNear(center, 5)) {
            pull(target, center, 1.3, 0.25);
        }

        particles(center, Particle.REVERSE_PORTAL, 100, 1.0);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player target : playersNear(center, 5)) {
                    damage(p, target, 6.0);
                }

                particles(center, Particle.EXPLOSION, 4, 0.25);
                sound(p, Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 0.55f);
            }
        }.runTaskLater(plugin, 10L);

        cooldown(p, 3, 28);
    }

    // =========================================================
    // STORM
    // =========================================================

    private void useStorm(Player p, int slot) {

        if (slot == 1) {
            Player target = targetPlayer(p, 14);

            if (target != null && !trusted(p, target)) {
                damage(p, target, 4.0);

                target.getWorld().strikeLightningEffect(
                        target.getLocation()
                );

                particles(
                        target.getLocation(),
                        Particle.ELECTRIC_SPARK,
                        45,
                        0.5
                );
            }

            sound(
                    p,
                    Sound.ENTITY_LIGHTNING_BOLT_IMPACT,
                    0.6f,
                    1.7f
            );

            cooldown(p, 1, 9);
            return;
        }

        if (slot == 2) {
            dashThrough(
                    p,
                    7.0,
                    3.0,
                    3.5,
                    Particle.ELECTRIC_SPARK
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_STRONG,
                    0.8f,
                    1.8f
            );

            cooldown(p, 2, 14);
            return;
        }

        Location center = p.getLocation();

        ring(center, Particle.ELECTRIC_SPARK, 4.5, 70);

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks++ >= 30) {
                    cancel();
                    return;
                }

                for (Player target : playersNear(center, 5)) {

                    if (trusted(p, target)) {
                        continue;
                    }

                    damage(p, target, 0.6);
                    pushAway(target, center, 0.35, 0.12);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);

        sound(
                p,
                Sound.ENTITY_LIGHTNING_BOLT_IMPACT,
                0.7f,
                0.8f
        );

        cooldown(p, 3, 27);
    }

    // =========================================================
    // FROST
    // =========================================================

    private void useFrost(Player p, int slot) {

        if (slot == 1) {
            Player target = targetPlayer(p, 12);

            if (target != null && !trusted(p, target)) {

                damage(p, target, 3.5);

                target.addPotionEffect(
                        new PotionEffect(
                                PotionEffectType.SLOWNESS,
                                60,
                                3,
                                false,
                                false,
                                true
                        )
                );

                particles(
                        target.getLocation(),
                        Particle.SNOWFLAKE,
                        45,
                        0.5
                );
            }

            sound(
                    p,
                    Sound.BLOCK_SNOW_BREAK,
                    0.8f,
                    1.5f
            );

            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {
            dashThrough(
                    p,
                    6.0,
                    3.0,
                    3.0,
                    Particle.SNOWFLAKE
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_SWEEP,
                    0.8f,
                    1.8f
            );

            cooldown(p, 2, 13);
            return;
        }

        Location center = p.getLocation();

        ring(center, Particle.SNOWFLAKE, 4.5, 80);

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks++ >= 30) {
                    cancel();
                    return;
                }

                for (Player target : playersNear(center, 5)) {

                    if (trusted(p, target)) {
                        continue;
                    }

                    target.setVelocity(
                            target.getVelocity().multiply(0.15)
                    );

                    damage(p, target, 0.7);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);

        sound(
                p,
                Sound.BLOCK_AMETHYST_BLOCK_RESONATE,
                0.8f,
                0.7f
        );

        cooldown(p, 3, 25);
    }

    // =========================================================
    // INFERNO
    // =========================================================

    private void useInferno(Player p, int slot) {

        if (slot == 1) {
            dashThrough(
                    p,
                    6.0,
                    3.0,
                    3.5,
                    Particle.FLAME
            );

            sound(
                    p,
                    Sound.ENTITY_BLAZE_SHOOT,
                    0.8f,
                    1.2f
            );

            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {

            Player target = targetPlayer(p, 14);

            if (target != null && !trusted(p, target)) {

                damage(p, target, 4.0);

                target.setFireTicks(
                        Math.max(target.getFireTicks(), 50)
                );

                particles(
                        target.getLocation(),
                        Particle.FLAME,
                        50,
                        0.4
                );
            }

            sound(
                    p,
                    Sound.ENTITY_BLAZE_SHOOT,
                    0.8f,
                    0.8f
            );

            cooldown(p, 2, 14);
            return;
        }

        Location center = p.getLocation();

        ring(center, Particle.FLAME, 4.0, 90);

        for (Player target : playersNear(center, 4.5)) {

            pushAway(target, center, 1.2, 0.4);
            damage(p, target, 4.5);

            if (!trusted(p, target)) {
                target.setFireTicks(40);
            }
        }

        sound(
                p,
                Sound.ENTITY_GENERIC_EXPLODE,
                0.8f,
                1.3f
        );

        cooldown(p, 3, 25);
    }

    // =========================================================
    // SHADOW
    // =========================================================

    private void useShadow(Player p, int slot) {

        if (slot == 1) {
            blinkForward(p, 5);

            particles(
                    p.getLocation(),
                    Particle.WITCH,
                    40,
                    0.5
            );

            sound(
                    p,
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    0.7f,
                    0.5f
            );

            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {

            dashThrough(
                    p,
                    6.0,
                    4.0,
                    4.0,
                    Particle.WITCH
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_CRIT,
                    0.8f,
                    0.7f
            );

            cooldown(p, 2, 14);
            return;
        }

        for (Player target : nearby(p, 7)) {

            if (trusted(p, target)) {
                continue;
            }

            damage(p, target, 1.5);

            target.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.DARKNESS,
                            60,
                            0,
                            false,
                            false,
                            true
                    )
            );
        }

        particles(
                p.getLocation(),
                Particle.WITCH,
                100,
                1.2
        );

        sound(
                p,
                Sound.BLOCK_PORTAL_AMBIENT,
                0.9f,
                0.45f
        );

        cooldown(p, 3, 24);
    }

    // =========================================================
    // TIME
    // =========================================================

    private void useTime(Player p, int slot) {

        if (slot == 1) {
            blinkForward(p, 5);

            particles(
                    p.getLocation(),
                    Particle.END_ROD,
                    40,
                    0.5
            );

            sound(
                    p,
                    Sound.BLOCK_AMETHYST_BLOCK_RESONATE,
                    0.8f,
                    1.8f
            );

            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {

            Deque<Location> positions =
                    history.get(p.getUniqueId());

            if (positions == null || positions.isEmpty()) {
                return;
            }

            Location rewind =
                    positions.peekLast();

            if (rewind != null) {

                p.teleport(rewind.clone());

                particles(
                        rewind,
                        Particle.END_ROD,
                        70,
                        0.7
                );

                sound(
                        p,
                        Sound.ENTITY_ENDERMAN_TELEPORT,
                        0.7f,
                        1.9f
                );
            }

            cooldown(p, 2, 18);
            return;
        }

        Player target = targetPlayer(p, 12);

        if (target == null || trusted(p, target)) {
            return;
        }

        final Location locked =
                target.getLocation().clone();

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks++ >= 40
                        || !target.isOnline()
                        || target.isDead()) {

                    cancel();
                    return;
                }

                target.teleport(locked);

                particles(
                        locked,
                        Particle.END_ROD,
                        10,
                        0.2
                );
            }
        }.runTaskTimer(plugin, 0L, 1L);

        sound(
                p,
                Sound.BLOCK_AMETHYST_BLOCK_RESONATE,
                0.8f,
                0.5f
        );

        cooldown(p, 3, 27);
    }

    // =========================================================
    // PHANTOM
    // =========================================================

    private void usePhantom(Player p, int slot) {

        if (slot == 1) {

            dashThrough(
                    p,
                    6.0,
                    2.5,
                    3.5,
                    Particle.SOUL
            );

            sound(
                    p,
                    Sound.ENTITY_PHANTOM_FLAP,
                    0.7f,
                    1.5f
            );

            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {

            p.setInvulnerable(true);

            particles(
                    p.getLocation(),
                    Particle.SOUL,
                    60,
                    0.6
            );

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (p.isOnline()) {
                        p.setInvulnerable(false);
                    }
                }
            }.runTaskLater(plugin, 40L);

            sound(
                    p,
                    Sound.ENTITY_PHANTOM_FLAP,
                    0.8f,
                    0.7f
            );

            cooldown(p, 2, 18);
            return;
        }

        Player target = targetPlayer(p, 12);

        if (target == null || trusted(p, target)) {
            return;
        }

        Location behind =
                target.getLocation().clone()
                        .subtract(
                                target.getLocation()
                                        .getDirection()
                                        .normalize()
                                        .multiply(1.7)
                        );

        safeTeleport(p, behind);
        damage(p, target, 5.0);

        particles(
                target.getLocation(),
                Particle.SOUL,
                70,
                0.5
        );

        sound(
                p,
                Sound.ENTITY_PLAYER_ATTACK_CRIT,
                0.8f,
                0.6f
        );

        cooldown(p, 3, 24);
    }

    // =========================================================
    // SOUL
    // =========================================================

    private void useSoul(Player p, int slot) {

        if (slot == 1) {

            for (Player target : nearby(p, 9)) {
                pull(target, p.getLocation(), 1.0, 0.3);
                damage(p, target, 2.0);
            }

            particles(
                    p.getLocation(),
                    Particle.SOUL,
                    70,
                    1.0
            );

            sound(
                    p,
                    Sound.ENTITY_EVOKER_CAST_SPELL,
                    0.8f,
                    0.7f
            );

            cooldown(p, 1, 9);
            return;
        }

        if (slot == 2) {

            Player target = targetPlayer(p, 12);

            if (target == null || trusted(p, target)) {
                return;
            }

            soulMarks.put(
                    p.getUniqueId(),
                    target.getUniqueId()
            );

            particles(
                    target.getLocation(),
                    Particle.SOUL,
                    65,
                    0.45
            );

            sound(
                    p,
                    Sound.ENTITY_EVOKER_CAST_SPELL,
                    0.7f,
                    1.5f
            );

            cooldown(p, 2, 15);
            return;
        }

        UUID marked =
                soulMarks.get(p.getUniqueId());

        if (marked != null) {

            Player target =
                    p.getServer().getPlayer(marked);

            if (target != null
                    && target.isOnline()
                    && !trusted(p, target)) {

                damage(p, target, 7.0);

                particles(
                        target.getLocation(),
                        Particle.SOUL,
                        120,
                        0.9
                );
            }

            soulMarks.remove(
                    p.getUniqueId()
            );

        } else {

            for (Player target : nearby(p, 5)) {
                damage(p, target, 4.0);
                particles(
                        target.getLocation(),
                        Particle.SOUL,
                        35,
                        0.3
                );
            }
        }

        sound(
                p,
                Sound.ENTITY_GENERIC_EXPLODE,
                0.7f,
                0.8f
        );

        cooldown(p, 3, 26);
    }

    // =========================================================
    // CHAOS
    // =========================================================

    private void useChaos(Player p, int slot) {

        if (slot == 1) {

            Player target = targetPlayer(p, 12);

            if (target == null || trusted(p, target)) {
                return;
            }

            damage(p, target, 3.0);

            int mode =
                    plugin.getRandom().nextInt(3);

            if (mode == 0) {
                pushAway(
                        target,
                        p.getLocation(),
                        1.6,
                        0.5
                );
            } else if (mode == 1) {
                pull(
                        target,
                        p.getLocation(),
                        1.8,
                        0.6
                );
            } else {
                target.setVelocity(
                        new Vector(
                                0,
                                1.2,
                                0
                        )
                );
            }

            particles(
                    target.getLocation(),
                    Particle.WITCH,
                    70,
                    0.5
            );

            sound(
                    p,
                    Sound.BLOCK_AMETHYST_BLOCK_RESONATE,
                    0.8f,
                    1.1f
            );

            cooldown(p, 1, 9);
            return;
        }

        if (slot == 2) {

            Player target = targetPlayer(p, 14);

            if (target == null || trusted(p, target)) {
                return;
            }

            Location a =
                    p.getLocation().clone();

            Location b =
                    target.getLocation().clone();

            safeTeleport(p, b);
            safeTeleport(target, a);

            particles(
                    a,
                    Particle.WITCH,
                    50,
                    0.5
            );

            particles(
                    b,
                    Particle.WITCH,
                    50,
                    0.5
            );

            sound(
                    p,
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    0.9f,
                    1.0f
            );

            cooldown(p, 2, 16);
            return;
        }

        Location center =
                p.getLocation().clone();

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (ticks++ >= 35) {
                    cancel();
                    return;
                }

                for (Player target : playersNear(center, 5)) {

                    if (trusted(p, target)) {
                        continue;
                    }

                    damage(p, target, 0.8);

                    if (plugin.getRandom().nextBoolean()) {
                        pushAway(
                                target,
                                center,
                                0.7,
                                0.2
                        );
                    } else {
                        pull(
                                target,
                                center,
                                0.7,
                                0.15
                        );
                    }
                }

                particles(
                        center,
                        Particle.WITCH,
                        12,
                        0.3
                );
            }
        }.runTaskTimer(plugin, 0L, 3L);

        cooldown(p, 3, 29);
    }

    // =========================================================
    // CELESTIAL
    // =========================================================

    private void useCelestial(Player p, int slot) {

        if (slot == 1) {

            Player target =
                    targetPlayer(p, 16);

            if (target == null || trusted(p, target)) {
                return;
            }

            damage(p, target, 4.5);

            particles(
                    target.getLocation(),
                    Particle.END_ROD,
                    80,
                    0.5
            );

            sound(
                    p,
                    Sound.BLOCK_BEACON_ACTIVATE,
                    0.8f,
                    2.0f
            );

            cooldown(p, 1, 10);
            return;
        }

        if (slot == 2) {

            dashThrough(
                    p,
                    7.0,
                    3.5,
                    4.0,
                    Particle.END_ROD
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_CRIT,
                    0.8f,
                    2.0f
            );

            cooldown(p, 2, 15);
            return;
        }

        Location center =
                targetLocation(p, 16);

        if (center == null) {
            center =
                    p.getLocation().add(
                            p.getLocation()
                                    .getDirection()
                                    .normalize()
                                    .multiply(7)
                    );
        }

        final Location finalCenter =
                center.clone();

        for (int i = 0; i < 3; i++) {

            final int index = i;

            new BukkitRunnable() {
                @Override
                public void run() {

                    Location strike =
                            finalCenter.clone().add(
                                    Math.cos(index * 2.1) * 2.2,
                                    0,
                                    Math.sin(index * 2.1) * 2.2
                            );

                    particles(
                            strike,
                            Particle.END_ROD,
                            55,
                            0.35
                    );

                    for (Player target :
                            playersNear(strike, 2.0)) {

                        damage(p, target, 3.5);
                    }

                    sound(
                            p,
                            Sound.BLOCK_BEACON_ACTIVATE,
                            0.7f,
                            1.6f
                    );
                }
            }.runTaskLater(plugin, i * 6L);
        }

        cooldown(p, 3, 30);
    }

    // =========================================================
    // AEGIS
    // =========================================================

    private void useAegis(Player p, int slot) {

        if (slot == 1) {

            Vector direction =
                    p.getLocation()
                            .getDirection()
                            .normalize();

            for (Player target :
                    playersInCone(p, 6, 0.55)) {

                if (trusted(p, target)) {
                    continue;
                }

                target.setVelocity(
                        direction.clone()
                                .multiply(1.6)
                                .setY(0.45)
                );

                damage(p, target, 2.5);
            }

            particles(
                    p.getLocation().add(
                            direction.clone().multiply(2)
                    ),
                    Particle.CRIT,
                    80,
                    0.5
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_STRONG,
                    0.8f,
                    0.7f
            );

            cooldown(p, 1, 10);
            return;
        }

        if (slot == 2) {

            autoCrit.put(
                    p.getUniqueId(),
                    System.currentTimeMillis() + 6000
            );

            particles(
                    p.getLocation(),
                    Particle.CRIT,
                    70,
                    0.5
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_CRIT,
                    1.0f,
                    1.4f
            );

            p.sendMessage(
                    "§b§lOblivion §8» §aAutoCrit armed for your next melee hit."
            );

            cooldown(p, 2, 20);
            return;
        }

        for (Player target : nearby(p, 5)) {

            if (trusted(p, target)) {
                continue;
            }

            damage(p, target, 4.5);

            pushAway(
                    target,
                    p.getLocation(),
                    1.5,
                    0.5
            );
        }

        ring(
                p.getLocation(),
                Particle.END_ROD,
                4.5,
                100
        );

        sound(
                p,
                Sound.ENTITY_GENERIC_EXPLODE,
                0.7f,
                1.8f
        );

        cooldown(p, 3, 25);
    }

    // =========================================================
    // FORCE
    // =========================================================

    private void useForce(Player p, int slot) {

        if (slot == 1) {

            for (Player target : nearby(p, 7)) {

                if (trusted(p, target)) {
                    continue;
                }

                pushAway(
                        target,
                        p.getLocation(),
                        2.0,
                        0.5
                );

                damage(p, target, 2.5);
            }

            particles(
                    p.getLocation(),
                    Particle.CLOUD,
                    80,
                    1.0
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_STRONG,
                    0.8f,
                    1.9f
            );

            cooldown(p, 1, 8);
            return;
        }

        if (slot == 2) {

            Player target =
                    targetPlayer(p, 12);

            if (target == null || trusted(p, target)) {
                return;
            }

            Vector launch =
                    p.getLocation()
                            .getDirection()
                            .normalize()
                            .multiply(1.1);

            launch.setY(1.35);

            target.setVelocity(launch);
            damage(p, target, 3.5);

            particles(
                    target.getLocation(),
                    Particle.CLOUD,
                    60,
                    0.4
            );

            sound(
                    p,
                    Sound.ENTITY_PLAYER_ATTACK_STRONG,
                    0.8f,
                    1.2f
            );

            cooldown(p, 2, 14);
            return;
        }

        Vector direction =
                p.getLocation()
                        .getDirection()
                        .normalize();

        for (int i = 1; i <= 7; i++) {

            Location point =
                    p.getLocation()
                            .clone()
                            .add(direction.clone().multiply(i));

            particles(
                    point,
                    Particle.CLOUD,
                    16,
                    0.15
            );

            for (Player target :
                    playersNear(point, 1.6)) {

                if (trusted(p, target)) {
                    continue;
                }

                pushAway(
                        target,
                        p.getLocation(),
                        1.4,
                        0.3
                );

                damage(p, target, 3.8);
            }
        }

        sound(
                p,
                Sound.ENTITY_PLAYER_ATTACK_STRONG,
                0.9f,
                1.5f
        );

        cooldown(p, 3, 24);
    }

    // =========================================================
    // ARCANE
    // =========================================================

    private void useArcane(Player p, int slot) {

        if (slot == 1) {

            Player target =
                    targetPlayer(p, 14);

            if (target == null || trusted(p, target)) {
                return;
            }

            damage(p, target, 4.0);

            particles(
                    target.getLocation(),
                    Particle.ENCHANT,
                    85,
                    0.5
            );

            sound(
                    p,
                    Sound.ENTITY_EVOKER_CAST_SPELL,
                    0.8f,
                    1.6f
            );

            cooldown(p, 1, 9);
            return;
        }

        if (slot == 2) {

            Player target =
                    targetPlayer(p, 14);

            if (target == null || trusted(p, target)) {
                return;
            }

            Location side =
                    target.getLocation()
                            .clone()
                            .add(
                                    target.getLocation()
                                            .getDirection()
                                            .normalize()
                                            .multiply(-2)
                            );

            safeTeleport(p, side);

            particles(
                    p.getLocation(),
                    Particle.ENCHANT,
                    75,
                    0.5
            );

            sound(
                    p,
                    Sound.ENTITY_ENDERMAN_TELEPORT,
                    0.8f,
                    1.8f
            );

            cooldown(p, 2, 16);
            return;
        }

        Location center =
                targetLocation(p, 12);

        if (center == null) {
            center =
                    p.getLocation().add(
                            p.getLocation()
                                    .getDirection()
                                    .normalize()
                                    .multiply(6)
                    );
        }

        for (Player target :
                playersNear(center, 5)) {

            if (trusted(p, target)) {
                continue;
            }

            damage(p, target, 5.5);

            pushAway(
                    target,
                    center,
                    1.4,
                    0.35
            );
        }

        particles(
                center,
                Particle.ENCHANT,
                130,
                1.0
        );

        sound(
                p,
                Sound.ENTITY_GENERIC_EXPLODE,
                0.7f,
                1.7f
        );

        cooldown(p, 3, 27);
    }

    // =========================================================
    // COOLDOWNS
    // =========================================================

    private boolean isOnCooldown(
            Player player,
            String id
    ) {
        Long end = cooldowns.get(
                player.getUniqueId() + ":" + id
        );

        if (end == null) {
            return false;
        }

        if (System.currentTimeMillis() >= end) {
            cooldowns.remove(
                    player.getUniqueId() + ":" + id
            );
            return false;
        }

        return true;
    }

    private void cooldown(
            Player player,
            int slot,
            int seconds
    ) {
        RelicType relic =
                relicManager.getRelic(
                        player.getUniqueId()
                );

        if (relic == null) {
            return;
        }

        String id =
                relic.name() + "_" + slot;

        cooldowns.put(
                player.getUniqueId() + ":" + id,
                System.currentTimeMillis()
                        + seconds * 1000L
        );
    }

    public long remainingCooldown(
            Player player,
            int slot
    ) {
        RelicType relic =
                relicManager.getRelic(
                        player.getUniqueId()
                );

        if (relic == null) {
            return 0L;
        }

        String id =
                relic.name() + "_" + slot;

        Long end =
                cooldowns.get(
                        player.getUniqueId() + ":" + id
                );

        if (end == null) {
            return 0L;
        }

        return Math.max(
                0L,
                end - System.currentTimeMillis()
        );
    }

    public boolean isAbilityUnlocked(
            Player player,
            int slot
    ) {
        if (slot == 1) {
            return true;
        }

        int energy =
                energyManager.getEnergy(
                        player.getUniqueId()
                );

        return slot == 2
                ? energy >= 8
                : energy >= 10;
    }

    // =========================================================
    // AUT0CRIT
    // =========================================================

    public boolean consumeAutoCrit(Player player) {

        Long end =
                autoCrit.get(
                        player.getUniqueId()
                );

        if (end == null) {
            return false;
        }

        if (System.currentTimeMillis() > end) {
            autoCrit.remove(
                    player.getUniqueId()
            );
            return false;
        }

        autoCrit.remove(
                player.getUniqueId()
        );

        return true;
    }

    // =========================================================
    // ABILITY NAMES
    // =========================================================

    public String getAbilityName(
            RelicType relic,
            int slot
    ) {

        return switch (relic) {

            case RIFT ->
                    slot == 1 ? "Rift Pull"
                            : slot == 2 ? "Rift Dash"
                            : "Rift Break";

            case GRAVITY ->
                    slot == 1 ? "Gravity Well"
                            : slot == 2 ? "Gravity Shift"
                            : "Gravity Crush";

            case VOID ->
                    slot == 1 ? "Void Step"
                            : slot == 2 ? "Void Snare"
                            : "Void Collapse";

            case STORM ->
                    slot == 1 ? "Storm Pierce"
                            : slot == 2 ? "Tempest Dash"
                            : "Storm Cage";

            case FROST ->
                    slot == 1 ? "Frost Lance"
                            : slot == 2 ? "Ice Slide"
                            : "Frozen Ground";

            case INFERNO ->
                    slot == 1 ? "Flame Rush"
                            : slot == 2 ? "Ember Shot"
                            : "Inferno Ring";

            case SHADOW ->
                    slot == 1 ? "Shadow Blink"
                            : slot == 2 ? "Shadow Pierce"
                            : "Blackout";

            case TIME ->
                    slot == 1 ? "Time Dash"
                            : slot == 2 ? "Rewind"
                            : "Time Lock";

            case PHANTOM ->
                    slot == 1 ? "Phantom Rush"
                            : slot == 2 ? "Phase Shift"
                            : "Phantom Strike";

            case SOUL ->
                    slot == 1 ? "Soul Pull"
                            : slot == 2 ? "Soul Mark"
                            : "Soul Burst";

            case CHAOS ->
                    slot == 1 ? "Chaos Bolt"
                            : slot == 2 ? "Chaos Swap"
                            : "Chaos Field";

            case CELESTIAL ->
                    slot == 1 ? "Celestial Spear"
                            : slot == 2 ? "Celestial Dash"
                            : "Starfall";

            case AEGIS ->
                    slot == 1 ? "Aegis Bash"
                            : slot == 2 ? "AutoCrit"
                            : "Aegis Break";

            case FORCE ->
                    slot == 1 ? "Force Push"
                            : slot == 2 ? "Force Launch"
                            : "Force Wave";

            case ARCANE ->
                    slot == 1 ? "Arcane Bolt"
                            : slot == 2 ? "Arcane Rift"
                            : "Arcane Burst";
        };
    }

    // =========================================================
    // TARGET / TRUST
    // =========================================================

    private boolean trusted(
            Player owner,
            Player target
    ) {
        return trustManager.isTrusted(
                owner.getUniqueId(),
                target.getUniqueId()
        );
    }

    private void damage(
            Player attacker,
            Player target,
            double amount
    ) {
        if (attacker.equals(target)) {
            return;
        }

        if (trusted(attacker, target)) {
            return;
        }

        target.damage(amount, attacker);
    }

    private Player targetPlayer(
            Player player,
            double range
    ) {
        Player best = null;
        double bestDistance = Double.MAX_VALUE;

        Vector direction =
                player.getLocation()
                        .getDirection()
                        .normalize();

        Location eyes =
                player.getEyeLocation();

        for (Player target :
                player.getWorld().getPlayers()) {

            if (target.equals(player)) {
                continue;
            }

            if (trusted(player, target)) {
                continue;
            }

            Location targetLocation =
                    target.getEyeLocation();

            Vector to =
                    targetLocation.toVector()
                            .subtract(
                                    eyes.toVector()
                            );

            double distance =
                    to.length();

            if (distance > range || distance <= 0) {
                continue;
            }

            double dot =
                    direction.dot(
                            to.clone().normalize()
                    );

            if (dot < 0.82) {
                continue;
            }

            if (distance < bestDistance) {
                bestDistance = distance;
                best = target;
            }
        }

        return best;
    }

    private Location targetLocation(
            Player player,
            double range
    ) {
        Player target =
                targetPlayer(player, range);

        return target == null
                ? null
                : target.getLocation();
    }

    private List<Player> nearby(
            Player player,
            double radius
    ) {
        return playersNear(
                player.getLocation(),
                radius
        );
    }

    private List<Player> playersNear(
            Location center,
            double radius
    ) {
        List<Player> result =
                new ArrayList<>();

        double max =
                radius * radius;

        for (Player player :
                center.getWorld().getPlayers()) {

            if (player.getLocation()
                    .distanceSquared(center) <= max) {

                result.add(player);
            }
        }

        return result;
    }

    private List<Player> playersInCone(
            Player player,
            double range,
            double minDot
    ) {
        List<Player> result =
                new ArrayList<>();

        Vector direction =
                player.getLocation()
                        .getDirection()
                        .normalize();

        Location eyes =
                player.getEyeLocation();

        for (Player target :
                player.getWorld().getPlayers()) {

            if (target.equals(player)) {
                continue;
            }

            if (trusted(player, target)) {
                continue;
            }

            Vector to =
                    target.getEyeLocation()
                            .toVector()
                            .subtract(
                                    eyes.toVector()
                            );

            if (to.length() > range) {
                continue;
            }

            if (direction.dot(
                    to.clone().normalize()
            ) >= minDot) {

                result.add(target);
            }
        }

        return result;
    }

    // =========================================================
    // MOVEMENT
    // =========================================================

    private void pull(
            Player target,
            Location center,
            double power,
            double y
    ) {
        Vector velocity =
                center.toVector()
                        .subtract(
                                target.getLocation()
                                        .toVector()
                        );

        if (velocity.lengthSquared() == 0) {
            return;
        }

        velocity.normalize()
                .multiply(power);

        velocity.setY(y);

        target.setVelocity(velocity);
    }

    private void pushAway(
            Player target,
            Location center,
            double power,
            double y
    ) {
        Vector velocity =
                target.getLocation()
                        .toVector()
                        .subtract(
                                center.toVector()
                        );

        if (velocity.lengthSquared() == 0) {
            return;
        }

        velocity.normalize()
                .multiply(power);

        velocity.setY(y);

        target.setVelocity(velocity);
    }

    private void blinkForward(
            Player player,
            double distance
    ) {
        Vector direction =
                player.getLocation()
                        .getDirection()
                        .normalize()
                        .multiply(distance);

        safeTeleport(
                player,
                player.getLocation()
                        .clone()
                        .add(direction)
        );
    }

    private void dashThrough(
            Player player,
            double distance,
            double stepDamage,
            double knockback,
            Particle particle
    ) {
        Location start =
                player.getLocation()
                        .clone();

        Vector direction =
                start.getDirection()
                        .normalize();

        Set<UUID> hit =
                new HashSet<>();

        Location lastSafe =
                start.clone();

        for (int step = 1; step <= 10; step++) {

            double current =
                    distance
                            * step
                            / 10.0;

            Location point =
                    start.clone()
                            .add(
                                    direction.clone()
                                            .multiply(current)
                            );

            if (!point.getBlock().isPassable()
                    || !point.clone()
                    .add(0, 1, 0)
                    .getBlock()
                    .isPassable()) {
                break;
            }

            lastSafe = point;

            particles(
                    point,
                    particle,
                    8,
                    0.12
            );

            for (Player target :
                    playersNear(point, 1.25)) {

                if (target.equals(player)) {
                    continue;
                }

                if (trusted(player, target)) {
                    continue;
                }

                if (hit.add(
                        target.getUniqueId()
                )) {
                    damage(
                            player,
                            target,
                            stepDamage
                    );

                    Vector knock =
                            direction.clone()
                                    .multiply(knockback);

                    knock.setY(0.35);

                    target.setVelocity(knock);
                }
            }
        }

        safeTeleport(player, lastSafe);

        particles(
                player.getLocation(),
                particle,
                50,
                0.35
        );
    }

    private void safeTeleport(
            Player player,
            Location destination
    ) {
        Location safe =
                destination.clone();

        safe.setYaw(
                player.getLocation().getYaw()
        );

        safe.setPitch(
                player.getLocation().getPitch()
        );

        if (safe.getBlock().isPassable()
                && safe.clone()
                .add(0, 1, 0)
                .getBlock()
                .isPassable()) {

            player.teleport(safe);
            return;
        }

        Location fallback =
                player.getLocation()
                        .clone();

        player.teleport(fallback);
    }

    // =========================================================
    // VFX / SOUND
    // =========================================================

    private void particles(
            Location location,
            Particle particle,
            int count,
            double spread
    ) {
        World world =
                location.getWorld();

        if (world == null) {
            return;
        }

        world.spawnParticle(
                particle,
                location,
                count,
                spread,
                spread,
                spread,
                0.03
        );
    }

    private void ring(
            Location center,
            Particle particle,
            double radius,
            int count
    ) {
        World world =
                center.getWorld();

        if (world == null) {
            return;
        }

        for (int i = 0; i < count; i++) {

            double angle =
                    (Math.PI * 2)
                            * i
                            / count;

            double x =
                    Math.cos(angle)
                            * radius;

            double z =
                    Math.sin(angle)
                            * radius;

            Location point =
                    center.clone()
                            .add(x, 0.1, z);

            world.spawnParticle(
                    particle,
                    point,
                    2,
                    0,
                    0,
                    0,
                    0
            );
        }
    }

    private void sound(
            Player player,
            Sound sound,
            float volume,
            float pitch
    ) {
        player.getWorld().playSound(
                player.getLocation(),
                sound,
                volume,
                pitch
        );
    }

    // =========================================================
    // TIME HISTORY
    // =========================================================

    private void startHistoryTracker() {

        new BukkitRunnable() {

            @Override
            public void run() {

                for (Player player :
                        plugin.getServer()
                                .getOnlinePlayers()) {

                    Deque<Location> positions =
                            history.computeIfAbsent(
                                    player.getUniqueId(),
                                    key -> new ArrayDeque<>()
                            );

                    positions.addFirst(
                            player.getLocation().clone()
                    );

                    while (positions.size() > 4) {
                        positions.removeLast();
                    }
                }
            }
        }.runTaskTimer(
                plugin,
                0L,
                20L
        );
    }
}
