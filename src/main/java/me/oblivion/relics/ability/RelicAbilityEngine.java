package me.oblivion.relics.ability;

import me.oblivion.relics.energy.EnergyManager;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.trust.TrustManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RelicAbilityEngine {

    private final JavaPlugin plugin;
    private final RelicManager relicManager;
    private final EnergyManager energyManager;
    private final TrustManager trustManager;

    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> autoCrits = new HashMap<>();
    private final Set<UUID> soulMarked = new HashSet<>();
    private final Map<UUID, Location> previousLocations = new HashMap<>();

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

        plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        previousLocations.put(
                                player.getUniqueId(),
                                player.getLocation().clone()
                        );
                    }
                },
                1L,
                5L
        );
    }

    public boolean useAbility(Player player, int slot) {
        if (player == null || slot < 1 || slot > 3) return false;

        RelicType relic = relicManager.getRelic(player.getUniqueId());
        if (relic == null) {
            player.sendActionBar("§cYou do not have a Relic.");
            return false;
        }

        if (!isHoldingRelic(player)) {
            player.sendActionBar("§7Hold your §b" + relic.getDisplayName() + " §7in your main hand.");
            return false;
        }

        if (!isAbilityUnlocked(player, slot)) {
            int required = slot == 2 ? 8 : 10;
            player.sendActionBar("§cAbility " + roman(slot) + " requires §f" + required + " Energy§c.");
            return false;
        }

        if (isOnCooldown(player, slot)) {
            player.sendActionBar(
                    "§c" + getAbilityName(relic, slot) + " §7is on cooldown: §f"
                            + remainingCooldown(player, slot) + "s"
            );
            return false;
        }

        boolean used = switch (relic) {
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
        };

        if (used) {
            startCooldown(player, slot, cooldownSeconds(relic, slot));
        }
        return used;
    }

    public boolean isHoldingRelic(Player player) {
        RelicType stored = relicManager.getRelic(player.getUniqueId());
        if (stored == null) return false;

        ItemStack held = player.getInventory().getItemInMainHand();
        RelicType itemRelic = relicManager.getRelicFromItem(held);
        return itemRelic == stored;
    }

    public boolean isAbilityUnlocked(Player player, int slot) {
        if (slot == 1) return true;
        int energy = energyManager.getEnergy(player.getUniqueId());
        return slot == 2 ? energy >= 8 : energy >= 10;
    }

    public int remainingCooldown(Player player, int slot) {
        long remaining = getRemainingCooldownMillis(player, slot);
        return (int) Math.ceil(remaining / 1000.0);
    }

    public boolean isOnCooldown(Player player, int slot) {
        return getRemainingCooldownMillis(player, slot) > 0;
    }

    public boolean isTrusted(Player owner, Player target) {
        return owner.equals(target)
                || trustManager.isTrusted(owner.getUniqueId(), target.getUniqueId());
    }

    public boolean consumeAutoCrit(Player player) {
        Long expires = autoCrits.get(player.getUniqueId());
        if (expires == null) return false;
        if (System.currentTimeMillis() >= expires) {
            autoCrits.remove(player.getUniqueId());
            return false;
        }
        autoCrits.remove(player.getUniqueId());
        return true;
    }

    public long getAutoCritRemaining(Player player) {
        Long expires = autoCrits.get(player.getUniqueId());
        if (expires == null) return 0L;

        long remaining = expires - System.currentTimeMillis();
        if (remaining <= 0) {
            autoCrits.remove(player.getUniqueId());
            return 0L;
        }
        return remaining;
    }

    public String getAbilityName(RelicType relic, int slot) {
        return switch (relic) {
            case RIFT -> switch (slot) {
                case 1 -> "Rift Pull";
                case 2 -> "Rift Dash";
                default -> "Rift Break";
            };
            case GRAVITY -> switch (slot) {
                case 1 -> "Gravity Well";
                case 2 -> "Gravity Lift";
                default -> "Gravity Crush";
            };
            case VOID -> switch (slot) {
                case 1 -> "Void Step";
                case 2 -> "Void Snare";
                default -> "Void Collapse";
            };
            case STORM -> switch (slot) {
                case 1 -> "Storm Pierce";
                case 2 -> "Tempest Dash";
                default -> "Storm Cage";
            };
            case FROST -> switch (slot) {
                case 1 -> "Frost Lance";
                case 2 -> "Ice Prison";
                default -> "Frozen Ground";
            };
            case INFERNO -> switch (slot) {
                case 1 -> "Flame Burst";
                case 2 -> "Ember Shot";
                default -> "Inferno Ring";
            };
            case SHADOW -> switch (slot) {
                case 1 -> "Shadow Blink";
                case 2 -> "Shadow Pierce";
                default -> "Blackout";
            };
            case TIME -> switch (slot) {
                case 1 -> "Time Acceleration";
                case 2 -> "Rewind";
                default -> "Time Lock";
            };
            case PHANTOM -> switch (slot) {
                case 1 -> "Phantom Rush";
                case 2 -> "Phase Shift";
                default -> "Phantom Strike";
            };
            case SOUL -> switch (slot) {
                case 1 -> "Soul Pull";
                case 2 -> "Soul Mark";
                default -> "Soul Burst";
            };
            case CHAOS -> switch (slot) {
                case 1 -> "Chaos Bolt";
                case 2 -> "Chaos Swap";
                default -> "Chaos Field";
            };
            case CELESTIAL -> switch (slot) {
                case 1 -> "Celestial Spear";
                case 2 -> "Celestial Guard";
                default -> "Starfall";
            };
            case AEGIS -> switch (slot) {
                case 1 -> "Aegis Bash";
                case 2 -> "AutoCrit";
                default -> "Aegis Break";
            };
            case FORCE -> switch (slot) {
                case 1 -> "Force Push";
                case 2 -> "Force Launch";
                default -> "Force Wave";
            };
            case ARCANE -> switch (slot) {
                case 1 -> "Arcane Bolt";
                case 2 -> "Arcane Rift";
                default -> "Arcane Burst";
            };
        };
    }

    private boolean useRift(Player p, int slot) {
        return switch (slot) {
            case 1 -> pull(p, 8, 1.05, Particle.PORTAL, Sound.ENTITY_ENDERMAN_TELEPORT);
            case 2 -> dashThrough(p, 10, 7.0, Particle.PORTAL, Sound.ENTITY_PLAYER_ATTACK_STRONG);
            default -> areaDamage(p, 6, 10.0, 1.4, Particle.PORTAL, Sound.ENTITY_GENERIC_EXPLODE);
        };
    }

    private boolean useGravity(Player p, int slot) {
        return switch (slot) {
            case 1 -> gravityWell(p);
            case 2 -> gravityLift(p);
            default -> gravityCrush(p);
        };
    }

    private boolean useVoid(Player p, int slot) {
        return switch (slot) {
            case 1 -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false, true));
                burst(p.getLocation().add(0, 1, 0), Particle.PORTAL, 30, 0.5);
                play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
                yield true;
            }
            case 2 -> voidSnare(p);
            default -> areaDamage(p, 6, 10.0, 0.8, Particle.PORTAL, Sound.ENTITY_GENERIC_EXPLODE);
        };
    }

    private boolean useStorm(Player p, int slot) {
        return switch (slot) {
            case 1 -> directStrike(p, 12, 9.0, Particle.ELECTRIC_SPARK);
            case 2 -> dashThrough(p, 11, 6.0, Particle.ELECTRIC_SPARK, Sound.ENTITY_PLAYER_ATTACK_STRONG);
            default -> stormCage(p);
        };
    }

    private boolean useFrost(Player p, int slot) {
        return switch (slot) {
            case 1 -> frostLance(p);
            case 2 -> icePrison(p);
            default -> frozenGround(p);
        };
    }

    private boolean useInferno(Player p, int slot) {
        return switch (slot) {
            case 1 -> flameBurst(p);
            case 2 -> emberShot(p);
            default -> infernoRing(p);
        };
    }

    private boolean useShadow(Player p, int slot) {
        return switch (slot) {
            case 1 -> blink(p, 10, Particle.SMOKE);
            case 2 -> shadowPierce(p);
            default -> blackout(p);
        };
    }

    private boolean useTime(Player p, int slot) {
        return switch (slot) {
            case 1 -> timeAcceleration(p);
            case 2 -> rewind(p);
            default -> timeLock(p);
        };
    }

    private boolean usePhantom(Player p, int slot) {
        return switch (slot) {
            case 1 -> dashThrough(p, 9, 8.0, Particle.SOUL_FIRE_FLAME, Sound.ENTITY_ENDERMAN_TELEPORT);
            case 2 -> phaseShift(p);
            default -> phantomStrike(p);
        };
    }

    private boolean useSoul(Player p, int slot) {
        return switch (slot) {
            case 1 -> pull(p, 9, 1.1, Particle.HEART, Sound.BLOCK_AMETHYST_BLOCK_CHIME);
            case 2 -> soulMark(p);
            default -> soulBurst(p);
        };
    }

    private boolean useChaos(Player p, int slot) {
        return switch (slot) {
            case 1 -> chaosBolt(p);
            case 2 -> chaosSwap(p);
            default -> chaosField(p);
        };
    }

    private boolean useCelestial(Player p, int slot) {
        return switch (slot) {
            case 1 -> directStrike(p, 14, 10.0, Particle.END_ROD);
            case 2 -> celestialGuard(p);
            default -> starfall(p);
        };
    }

    private boolean useAegis(Player p, int slot) {
        return switch (slot) {
            case 1 -> aegisBash(p);
            case 2 -> {
                autoCrits.put(p.getUniqueId(), System.currentTimeMillis() + 8000L);
                p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, false, false, true));
                burst(p.getLocation().add(0, 1, 0), Particle.CRIT, 28, 0.7);
                play(p, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 0.8f);
                p.sendActionBar("§b§lAutoCrit §7active for §f8s");
                yield true;
            }
            default -> areaDamage(p, 5, 10.0, 1.1, Particle.CRIT, Sound.ENTITY_GENERIC_EXPLODE);
        };
    }

    private boolean useForce(Player p, int slot) {
        return switch (slot) {
            case 1 -> forcePush(p);
            case 2 -> forceLaunch(p);
            default -> forceWave(p);
        };
    }

    private boolean useArcane(Player p, int slot) {
        return switch (slot) {
            case 1 -> directStrike(p, 12, 9.0, Particle.ENCHANT);
            case 2 -> arcaneRift(p);
            default -> areaDamage(p, 6, 10.0, 1.3, Particle.ENCHANT, Sound.BLOCK_AMETHYST_BLOCK_CHIME);
        };
    }

    private boolean pull(Player p, double radius, double power, Particle particle, Sound sound) {
        int affected = 0;
        for (LivingEntity target : nearbyLiving(p, radius)) {
            if (blocked(p, target)) continue;
            Vector direction = p.getLocation().toVector().subtract(target.getLocation().toVector());
            if (direction.lengthSquared() < 0.01) continue;
            target.setVelocity(direction.normalize().multiply(power).setY(0.25));
            burst(target.getLocation().add(0, 1, 0), particle, 12, 0.35);
            affected++;
            if (target instanceof Player) target.damage(2.0, p);
        }
        if (affected == 0) {
            p.sendActionBar("§7No target in range.");
            return false;
        }
        play(p, sound, 1.0f, 0.9f);
        return true;
    }

    private boolean dashThrough(Player p, double distance, double damage, Particle particle, Sound sound) {
        Location start = p.getLocation().clone();
        Vector direction = start.getDirection().normalize();
        Location destination = start.clone().add(direction.clone().multiply(distance));

        for (int i = 1; i <= 16; i++) {
            Location point = start.clone().add(direction.clone().multiply(distance * i / 16.0));
            if (point.getBlock().isSolid() || point.clone().add(0, 1, 0).getBlock().isSolid()) {
                destination = point.subtract(direction.clone().multiply(1.0));
                break;
            }
            point.getWorld().spawnParticle(particle, point.add(0, 1, 0), 5, 0.16, 0.16, 0.16, 0.01);
        }

        for (LivingEntity target : nearbyLivingLine(p, start, destination, 1.3)) {
            if (blocked(p, target)) continue;
            target.damage(damage, p);
            target.setVelocity(direction.clone().multiply(0.85).setY(0.3));
        }

        p.teleport(destination);
        burst(destination.clone().add(0, 1, 0), particle, 20, 0.45);
        play(p, sound, 1.0f, 1.05f);
        return true;
    }

    private boolean areaDamage(Player p, double radius, double damage, double knockback, Particle particle, Sound sound) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, radius)) {
            if (blocked(p, target)) continue;
            Vector away = target.getLocation().toVector().subtract(p.getLocation().toVector());
            if (away.lengthSquared() < 0.01) away = new Vector(0, 1, 0);
            target.damage(damage, p);
            target.setVelocity(away.normalize().multiply(knockback).setY(0.4));
            burst(target.getLocation().add(0, 1, 0), particle, 15, 0.35);
            hit++;
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        burst(p.getLocation().add(0, 1, 0), particle, 35, 1.0);
        play(p, sound, 0.9f, 0.9f);
        return true;
    }

    private boolean gravityWell(Player p) {
        return pull(p, 7, 1.15, Particle.CLOUD, Sound.BLOCK_AMETHYST_BLOCK_CHIME);
    }

    private boolean gravityLift(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 6)) {
            if (blocked(p, target)) continue;
            target.setVelocity(new Vector(0, 1.45, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2, false, false, true));
            burst(target.getLocation(), Particle.CLOUD, 14, 0.35);
            hit++;
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.7f);
        return true;
    }

    private boolean gravityCrush(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 6)) {
            if (blocked(p, target)) continue;
            target.setVelocity(new Vector(0, 1.0, 0));
            hit++;
            LivingEntity finalTarget = target;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!finalTarget.isDead() && !blocked(p, finalTarget)) {
                    finalTarget.damage(10.0, p);
                    finalTarget.setVelocity(new Vector(0, -1.1, 0));
                    burst(finalTarget.getLocation(), Particle.CLOUD, 20, 0.4);
                }
            }, 12L);
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        play(p, Sound.ENTITY_GENERIC_EXPLODE, 0.55f, 1.25f);
        return true;
    }

    private boolean voidSnare(Player p) {
        LivingEntity target = frontTarget(p, 11, 0.9);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 6, false, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1, false, false, true));
        burst(target.getLocation().add(0, 1, 0), Particle.PORTAL, 25, 0.4);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.6f);
        return true;
    }

    private boolean directStrike(Player p, double range, double damage, Particle particle) {
        LivingEntity target = frontTarget(p, range, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.damage(damage, p);
        trailBetween(p.getEyeLocation(), target.getLocation().add(0, 1, 0), particle, 14);
        burst(target.getLocation().add(0, 1, 0), particle, 20, 0.35);
        play(p, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
        return true;
    }

    private boolean frostLance(Player p) {
        LivingEntity target = frontTarget(p, 13, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.damage(8.0, p);
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2, false, false, true));
        trailBetween(p.getEyeLocation(), target.getLocation().add(0, 1, 0), Particle.SNOWFLAKE, 18);
        burst(target.getLocation().add(0, 1, 0), Particle.SNOWFLAKE, 24, 0.45);
        play(p, Sound.BLOCK_GLASS_BREAK, 0.9f, 1.1f);
        return true;
    }

    private boolean icePrison(Player p) {
        LivingEntity target = frontTarget(p, 11, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.setVelocity(new Vector(0, 0, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 8, false, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2, false, false, true));
        burst(target.getLocation().add(0, 1, 0), Particle.SNOWFLAKE, 35, 0.7);
        play(p, Sound.BLOCK_GLASS_BREAK, 1.0f, 0.7f);
        return true;
    }

    private boolean frozenGround(Player p) {
        return areaDamage(p, 5, 6.0, 0.35, Particle.SNOWFLAKE, Sound.BLOCK_GLASS_BREAK);
    }

    private boolean flameBurst(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 5)) {
            if (blocked(p, target)) continue;
            target.damage(7.0, p);
            target.setFireTicks(60);
            burst(target.getLocation().add(0, 1, 0), Particle.FLAME, 20, 0.4);
            hit++;
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        play(p, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 0.8f);
        return true;
    }

    private boolean emberShot(Player p) {
        LivingEntity target = frontTarget(p, 14, 0.75);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.damage(9.0, p);
        target.setFireTicks(80);
        trailBetween(p.getEyeLocation(), target.getLocation().add(0, 1, 0), Particle.FLAME, 20);
        burst(target.getLocation().add(0, 1, 0), Particle.FLAME, 25, 0.45);
        play(p, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 0.85f);
        return true;
    }

    private boolean infernoRing(Player p) {
        return areaDamage(p, 6, 9.0, 1.0, Particle.FLAME, Sound.ENTITY_GENERIC_EXPLODE);
    }

    private boolean blink(Player p, double distance, Particle particle) {
        Location start = p.getLocation().clone();
        Vector direction = start.getDirection().normalize();
        Location destination = start.clone();
        for (int i = 1; i <= distance; i++) {
            Location test = start.clone().add(direction.clone().multiply(i));
            if (test.getBlock().isSolid() || test.clone().add(0, 1, 0).getBlock().isSolid()) break;
            destination = test;
        }
        if (destination.distanceSquared(start) < 1.0) {
            p.sendActionBar("§cThere is not enough space to teleport.");
            return false;
        }
        burst(start.add(0, 1, 0), particle, 20, 0.45);
        p.teleport(destination);
        burst(destination.clone().add(0, 1, 0), particle, 25, 0.45);
        play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.15f);
        return true;
    }

    private boolean shadowPierce(Player p) {
        LivingEntity target = frontTarget(p, 12, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        Vector fromTarget = p.getLocation().toVector().subtract(target.getLocation().toVector());
        if (fromTarget.lengthSquared() < 0.01) fromTarget = target.getLocation().getDirection();
        Location behind = target.getLocation().clone().add(fromTarget.normalize().multiply(1.4));
        p.teleport(behind);
        target.damage(10.0, p);
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1, false, false, true));
        burst(target.getLocation().add(0, 1, 0), Particle.SMOKE, 30, 0.45);
        play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        return true;
    }

    private boolean blackout(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 6)) {
            if (blocked(p, target)) continue;
            target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0, false, false, true));
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1, false, false, true));
            burst(target.getLocation().add(0, 1, 0), Particle.SMOKE, 20, 0.45);
            hit++;
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.5f);
        return true;
    }

    private boolean timeAcceleration(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 2, false, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 120, 2, false, false, true));
        burst(p.getLocation().add(0, 1, 0), Particle.END_ROD, 35, 0.7);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.3f);
        return true;
    }

    private boolean rewind(Player p) {
        Location old = previousLocations.get(p.getUniqueId());
        if (old == null || old.getWorld() == null) {
            p.sendActionBar("§cNo rewind location is available yet.");
            return false;
        }
        Location current = p.getLocation().clone();
        burst(current.add(0, 1, 0), Particle.END_ROD, 25, 0.5);
        p.teleport(old.clone());
        burst(old.clone().add(0, 1, 0), Particle.END_ROD, 25, 0.5);
        play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.65f);
        return true;
    }

    private boolean timeLock(Player p) {
        LivingEntity target = frontTarget(p, 12, 0.85);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.setVelocity(new Vector(0, 0, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 8, false, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 2, false, false, true));
        burst(target.getLocation().add(0, 1, 0), Particle.END_ROD, 25, 0.5);
        LivingEntity finalTarget = target;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (finalTarget.isDead() || blocked(p, finalTarget)) return;
            finalTarget.damage(10.0, p);
            burst(finalTarget.getLocation().add(0, 1, 0), Particle.END_ROD, 30, 0.5);
        }, 40L);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.7f);
        return true;
    }

    private boolean phaseShift(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 0, false, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 160, 1, false, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1, false, false, true));
        burst(p.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 40, 0.7);
        play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.6f);
        p.sendActionBar("§b§lPhase Shift §7active for §f8s");
        return true;
    }

    private boolean phantomStrike(Player p) {
        LivingEntity target = frontTarget(p, 13, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.damage(11.0, p);
        burst(target.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 28, 0.4);
        play(p, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 0.9f);
        return true;
    }

    private boolean soulMark(Player p) {
        LivingEntity target = frontTarget(p, 12, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        if (target instanceof Player targetPlayer) soulMarked.add(targetPlayer.getUniqueId());
        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 160, 0, false, false, true));
        burst(target.getLocation().add(0, 1, 0), Particle.HEART, 20, 0.35);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.0f);
        return true;
    }

    private boolean soulBurst(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 7)) {
            if (blocked(p, target)) continue;
            double damage = 7.0;
            if (target instanceof Player targetPlayer && soulMarked.remove(targetPlayer.getUniqueId())) damage = 14.0;
            target.damage(damage, p);
            burst(target.getLocation().add(0, 1, 0), Particle.HEART, 18, 0.35);
            hit++;
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.3f);
        return true;
    }

    private boolean chaosBolt(Player p) {
        LivingEntity target = frontTarget(p, 13, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        int mode = ThreadLocalRandom.current().nextInt(3);
        switch (mode) {
            case 0 -> target.damage(10.0, p);
            case 1 -> target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 4, false, false, true));
            default -> target.setVelocity(p.getLocation().getDirection().normalize().multiply(1.2).setY(0.7));
        }
        burst(target.getLocation().add(0, 1, 0), Particle.ENCHANT, 24, 0.45);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.8f + mode * 0.2f);
        return true;
    }

    private boolean chaosSwap(Player p) {
        Player target = nearestPlayer(p, 10);
        if (target == null || isTrusted(p, target)) {
            p.sendActionBar("§7No valid untrusted player nearby.");
            return false;
        }
        Location own = p.getLocation().clone();
        Location other = target.getLocation().clone();
        p.teleport(other);
        target.teleport(own);
        burst(p.getLocation().add(0, 1, 0), Particle.ENCHANT, 25, 0.45);
        burst(target.getLocation().add(0, 1, 0), Particle.ENCHANT, 25, 0.45);
        play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
        return true;
    }

    private boolean chaosField(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 6)) {
            if (blocked(p, target)) continue;
            int mode = ThreadLocalRandom.current().nextInt(3);
            if (mode == 0) {
                target.damage(6.0, p);
            } else if (mode == 1) {
                Vector pull = p.getLocation().toVector().subtract(target.getLocation().toVector());
                if (pull.lengthSquared() > 0.01) target.setVelocity(pull.normalize().multiply(0.8).setY(0.45));
            } else {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 70, 3, false, false, true));
            }
            burst(target.getLocation().add(0, 1, 0), Particle.ENCHANT, 14, 0.35);
            hit++;
        }
        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 0.6f);
        return true;
    }

    private boolean celestialGuard(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 120, 1, false, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1, false, false, true));
        burst(p.getLocation().add(0, 1, 0), Particle.END_ROD, 40, 0.8);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.4f);
        return true;
    }

    private boolean starfall(Player p) {
        LivingEntity target = frontTarget(p, 16, 0.8);
        Location center;
        if (target != null && !blocked(p, target)) {
            center = target.getLocation().clone();
        } else {
            center = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(6));
        }

        for (int i = 0; i < 5; i++) {
            Location point = center.clone().add(
                    ThreadLocalRandom.current().nextDouble(-2.0, 2.0),
                    8 + i,
                    ThreadLocalRandom.current().nextDouble(-2.0, 2.0)
            );
            Location finalPoint = point.clone();
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> trail(finalPoint, Particle.END_ROD, 10), i * 3L);
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (LivingEntity entity : nearbyLivingAt(center, 4.5)) {
                if (blocked(p, entity)) continue;
                entity.damage(12.0, p);
                burst(entity.getLocation().add(0, 1, 0), Particle.END_ROD, 22, 0.4);
            }
            burst(center, Particle.END_ROD, 50, 1.7);
            play(p, Sound.ENTITY_GENERIC_EXPLODE, 0.65f, 1.45f);
        }, 15L);

        play(p, Sound.ITEM_TRIDENT_RETURN, 0.8f, 1.5f);
        return true;
    }

    private boolean aegisBash(Player p) {
        LivingEntity target = frontTarget(p, 6, 0.9);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        Vector direction = target.getLocation().toVector().subtract(p.getLocation().toVector());
        if (direction.lengthSquared() < 0.01) direction = p.getLocation().getDirection();
        target.damage(8.0, p);
        target.setVelocity(direction.normalize().multiply(1.7).setY(0.45));
        burst(target.getLocation().add(0, 1, 0), Particle.CRIT, 22, 0.4);
        play(p, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 0.7f);
        return true;
    }

    private boolean forcePush(Player p) {
        LivingEntity target = frontTarget(p, 9, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        Vector direction = target.getLocation().toVector().subtract(p.getLocation().toVector());
        if (direction.lengthSquared() < 0.01) return false;
        target.setVelocity(direction.normalize().multiply(2.0).setY(0.65));
        burst(target.getLocation().add(0, 1, 0), Particle.CLOUD, 22, 0.4);
        play(p, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 0.9f);
        return true;
    }

    private boolean forceLaunch(Player p) {
        LivingEntity target = frontTarget(p, 9, 0.8);
        if (target == null || blocked(p, target)) {
            p.sendActionBar("§7No target in front.");
            return false;
        }
        target.setVelocity(new Vector(0, 1.7, 0));
        target.damage(4.0, p);
        burst(target.getLocation().add(0, 1, 0), Particle.CLOUD, 24, 0.45);
        play(p, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.2f);
        return true;
    }

    private boolean forceWave(Player p) {
        return areaDamage(p, 7, 6.0, 1.5, Particle.CLOUD, Sound.ENTITY_GENERIC_EXPLODE);
    }

    private boolean arcaneRift(Player p) {
        LivingEntity target = frontTarget(p, 14, 0.8);
        if (target != null && !blocked(p, target)) {
            Location destination = target.getLocation().clone().add(target.getLocation().getDirection().normalize().multiply(2.0));
            if (destination.getBlock().isSolid() || destination.clone().add(0, 1, 0).getBlock().isSolid()) {
                return blink(p, 10, Particle.ENCHANT);
            }
            p.teleport(destination);
        } else {
            return blink(p, 10, Particle.ENCHANT);
        }
        burst(p.getLocation().add(0, 1, 0), Particle.ENCHANT, 30, 0.55);
        play(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        return true;
    }

    private boolean stormCage(Player p) {
        int hit = 0;
        for (LivingEntity target : nearbyLiving(p, 5)) {
            if (blocked(p, target)) continue;

            target.damage(6.0, p);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3, false, false, true));
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1, false, false, true));

            Location center = target.getLocation().add(0, 1, 0);
            burst(center, Particle.ELECTRIC_SPARK, 22, 0.5);
            playAt(center, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.7f, 1.6f);
            hit++;
        }

        if (hit == 0) {
            p.sendActionBar("§7No targets in range.");
            return false;
        }

        burst(p.getLocation().add(0, 1, 0), Particle.ELECTRIC_SPARK, 40, 1.2);
        play(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.9f, 1.7f);
        return true;
    }

    private LivingEntity frontTarget(Player p, double range, double width) {
        Location eye = p.getEyeLocation();
        Vector direction = eye.getDirection().normalize();
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Entity entity : eye.getWorld().getNearbyEntities(eye, range, range, range)) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (living.equals(p) || living.isDead()) continue;

            Vector to = living.getEyeLocation().toVector().subtract(eye.toVector());
            double distance = to.length();
            if (distance > range || distance <= 0.01) continue;

            double dot = direction.dot(to.normalize());
            if (dot < 0.84) continue;

            Vector projected = direction.clone().multiply(to.dot(direction));
            double perpendicular = to.clone().subtract(projected).length();
            if (perpendicular > width) continue;

            if (distance < bestDistance) {
                best = living;
                bestDistance = distance;
            }
        }
        return best;
    }

    private Player nearestPlayer(Player owner, double radius) {
        Player best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (target.equals(owner) || isTrusted(owner, target)) continue;
            double distance = target.getLocation().distanceSquared(owner.getLocation());
            if (distance <= radius * radius && distance < bestDistance) {
                best = target;
                bestDistance = distance;
            }
        }
        return best;
    }

    private Set<LivingEntity> nearbyLiving(Player p, double radius) {
        return nearbyLivingAt(p.getLocation(), radius, p);
    }

    private Set<LivingEntity> nearbyLivingAt(Location center, double radius) {
        return nearbyLivingAt(center, radius, null);
    }

    private Set<LivingEntity> nearbyLivingAt(Location center, double radius, Player ignored) {
        Set<LivingEntity> result = new HashSet<>();
        World world = center.getWorld();
        if (world == null) return result;

        for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (living.isDead()) continue;
            if (ignored != null && living.equals(ignored)) continue;
            result.add(living);
        }
        return result;
    }

    private Set<LivingEntity> nearbyLivingLine(Player p, Location start, Location end, double width) {
        Set<LivingEntity> result = new HashSet<>();
        World world = start.getWorld();
        if (world == null) return result;

        for (int i = 0; i <= 16; i++) {
            double progress = i / 16.0;
            Location point = start.clone().add(
                    end.toVector().subtract(start.toVector()).multiply(progress)
            );
            for (Entity entity : world.getNearbyEntities(point, width, width, width)) {
                if (!(entity instanceof LivingEntity living)) continue;
                if (living.isDead() || living.equals(p)) continue;
                result.add(living);
            }
        }
        return result;
    }

    private boolean blocked(Player owner, LivingEntity target) {
        return target instanceof Player player && isTrusted(owner, player);
    }

    private void startCooldown(Player player, int slot, int seconds) {
        if (seconds <= 0) return;
        cooldowns.put(key(player, slot), System.currentTimeMillis() + seconds * 1000L);
    }

    private long getRemainingCooldownMillis(Player player, int slot) {
        Long end = cooldowns.get(key(player, slot));
        if (end == null) return 0L;

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0) {
            cooldowns.remove(key(player, slot));
            return 0L;
        }
        return remaining;
    }

    private String key(Player player, int slot) {
        return player.getUniqueId() + ":" + slot;
    }

    private int cooldownSeconds(RelicType relic, int slot) {
        return switch (relic) {
            case RIFT -> switch (slot) { case 1 -> 8; case 2 -> 10; default -> 18; };
            case GRAVITY -> switch (slot) { case 1 -> 9; case 2 -> 12; default -> 20; };
            case VOID -> switch (slot) { case 1 -> 10; case 2 -> 12; default -> 20; };
            case STORM -> switch (slot) { case 1 -> 7; case 2 -> 10; default -> 18; };
            case FROST -> switch (slot) { case 1 -> 8; case 2 -> 12; default -> 18; };
            case INFERNO -> switch (slot) { case 1 -> 8; case 2 -> 9; default -> 18; };
            case SHADOW -> switch (slot) { case 1 -> 10; case 2 -> 12; default -> 20; };
            case TIME -> switch (slot) { case 1 -> 12; case 2 -> 18; default -> 22; };
            case PHANTOM -> switch (slot) { case 1 -> 10; case 2 -> 18; default -> 18; };
            case SOUL -> switch (slot) { case 1 -> 8; case 2 -> 12; default -> 18; };
            case CHAOS -> switch (slot) { case 1 -> 7; case 2 -> 14; default -> 18; };
            case CELESTIAL -> switch (slot) { case 1 -> 8; case 2 -> 16; default -> 22; };
            case AEGIS -> switch (slot) { case 1 -> 7; case 2 -> 16; default -> 18; };
            case FORCE -> switch (slot) { case 1 -> 7; case 2 -> 8; default -> 18; };
            case ARCANE -> switch (slot) { case 1 -> 7; case 2 -> 12; default -> 18; };
        };
    }

    private String roman(int slot) {
        return switch (slot) {
            case 1 -> "I";
            case 2 -> "II";
            default -> "III";
        };
    }

    private void burst(Location location, Particle particle, int amount, double spread) {
        World world = location.getWorld();
        if (world == null) return;
        world.spawnParticle(particle, location, amount, spread, spread, spread, 0.01);
    }

    private void trail(Location start, Particle particle, int points) {
        World world = start.getWorld();
        if (world == null) return;
        for (int i = 0; i < points; i++) {
            Location point = start.clone().add(0, i * 0.25, 0);
            world.spawnParticle(particle, point, 4, 0.08, 0.08, 0.08, 0.01);
        }
    }

    private void trailBetween(Location start, Location end, Particle particle, int points) {
        World world = start.getWorld();
        if (world == null || end.getWorld() == null || !world.equals(end.getWorld())) return;

        Vector difference = end.toVector().subtract(start.toVector());
        for (int i = 0; i <= points; i++) {
            double progress = i / (double) points;
            Location point = start.clone().add(difference.clone().multiply(progress));
            world.spawnParticle(particle, point, 3, 0.06, 0.06, 0.06, 0.01);
        }
    }

    private void play(Player player, Sound sound, float volume, float pitch) {
        player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
    }

    private void playAt(Location location, Sound sound, float volume, float pitch) {
        World world = location.getWorld();
        if (world != null) world.playSound(location, sound, volume, pitch);
    }
}
