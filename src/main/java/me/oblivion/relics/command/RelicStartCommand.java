package me.oblivion.relics.command;

import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.start.RelicStartManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelicStartCommand implements org.bukkit.command.CommandExecutor {

    private final JavaPlugin plugin;
    private final RelicManager relicManager;
    private final RelicStartManager startManager;

    private final Map<Player, ItemDisplay> displays = new HashMap<>();

    public RelicStartCommand(
            JavaPlugin plugin,
            RelicManager relicManager,
            RelicStartManager startManager
    ) {
        this.plugin = plugin;
        this.relicManager = relicManager;
        this.startManager = startManager;
    }

    @Override
    public boolean onCommand(
            org.bukkit.command.CommandSender sender,
            org.bukkit.command.Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player) && !sender.isOp()) {
            if (!sender.isOp()) {
                sender.sendMessage("§cOnly OP players can use this command.");
                return true;
            }
        }

        if (!sender.isOp()) {
            sender.sendMessage("§cOnly OP players can use this command.");
            return true;
        }

        if (startManager.isStarted()) {
            sender.sendMessage("§cThe Relic system has already been started.");
            return true;
        }

        List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());

        if (players.isEmpty()) {
            sender.sendMessage("§cThere are no players online.");
            return true;
        }

        startManager.setStarted(true);

        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        plugin.getServer().broadcastMessage("§b§l             OBLIVION SMP");
        plugin.getServer().broadcastMessage("§7              §lRELIC AWAKENING");
        plugin.getServer().broadcastMessage("");
        plugin.getServer().broadcastMessage("§bThe Relics are awakening...");
        plugin.getServer().broadcastMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        plugin.getServer().broadcastMessage("");

        for (Player player : players) {
            freezePlayer(player);

            RelicType selected = relicManager.giveRandomRelic(
                    player.getUniqueId()
            );

            ItemStack relicItem = relicManager.createRelicItem(selected);

            if (relicItem != null) {
                ItemDisplay display = player.getWorld().spawn(
                        player.getLocation().add(0, 2.2, 0),
                        ItemDisplay.class
                );

                display.setItemStack(relicItem);
                display.setBillboard(ItemDisplay.Billboard.CENTER);

                display.setTransformation(
                        new Transformation(
                                new Vector3f(-0.5f, -0.5f, -0.5f),
                                new AxisAngle4f(),
                                new Vector3f(1.0f, 1.0f, 1.0f),
                                new AxisAngle4f()
                        )
                );

                displays.put(player, display);
            }

            player.getWorld().playSound(
                    player.getLocation(),
                    Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                    1.2f,
                    0.7f
            );
        }

        plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                new Runnable() {

                    int ticks = 0;

                    @Override
                    public void run() {

                        ticks += 2;

                        double progress = ticks / 100.0;

                        for (Player player : players) {

                            if (!player.isOnline()) {
                                continue;
                            }

                            player.setVelocity(new Vector(0, 0, 0));
                            player.setFallDistance(0);

                            Location base = player.getLocation();

                            player.getWorld().spawnParticle(
                                    Particle.END_ROD,
                                    base.clone().add(0, 1, 0),
                                    5,
                                    0.25,
                                    0.5,
                                    0.25,
                                    0.01
                            );

                            ItemDisplay display = displays.get(player);

                            if (display != null && !display.isDead()) {

                                double angle = (ticks * 0.12)
                                        + players.indexOf(player) * 0.7;

                                double x = Math.cos(angle) * 1.15;
                                double z = Math.sin(angle) * 1.15;
                                double y = 2.1 + Math.sin(angle * 1.5) * 0.25;

                                Location orbit = base.clone().add(x, y, z);

                                display.teleport(
                                        orbit,
                                        PlayerTeleportEvent.TeleportCause.PLUGIN
                                );

                                display.setRotation(
                                        (float) Math.toDegrees(angle),
                                        0
                                );
                            }
                        }

                        if (ticks >= 100) {

                            for (Player player : players) {

                                if (!player.isOnline()) {
                                    continue;
                                }

                                ItemDisplay display = displays.remove(player);

                                if (display != null && !display.isDead()) {
                                    display.remove();
                                }

                                player.setGravity(true);
                                player.setVelocity(new Vector(0, 0, 0));
                                player.setFallDistance(0);

                                RelicType relic = relicManager.getRelic(
                                        player.getUniqueId()
                                );

                                if (relic == null) {
                                    relic = relicManager.giveRandomRelic(
                                            player.getUniqueId()
                                    );
                                }

                                removeOldRelicItems(player);

                                ItemStack item = relicManager.createRelicItem(relic);

                                if (item != null) {
                                    player.getInventory().addItem(item);
                                }

                                player.getWorld().spawnParticle(
                                        Particle.END_ROD,
                                        player.getLocation().add(0, 1, 0),
                                        40,
                                        0.7,
                                        1.0,
                                        0.7,
                                        0.03
                                );

                                player.getWorld().playSound(
                                        player.getLocation(),
                                        Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                                        1.4f,
                                        1.2f
                                );

                                player.sendTitle(
                                        "§b§l" + relic.getDisplayName(),
                                        "§7Your Relic has awakened",
                                        10,
                                        50,
                                        15
                                );
                            }

                            plugin.getServer().broadcastMessage("");
                            plugin.getServer().broadcastMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            plugin.getServer().broadcastMessage("§b§l            RELICS AWAKENED");
                            plugin.getServer().broadcastMessage("§7Every player has received a Relic.");
                            plugin.getServer().broadcastMessage("§bGood luck.");
                            plugin.getServer().broadcastMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            plugin.getServer().broadcastMessage("");

                            throw new StopAnimationException();
                        }
                    }
                },
                2L,
                2L
        );

        return true;
    }

    private void freezePlayer(Player player) {
        player.setGravity(false);
        player.setVelocity(new Vector(0, 0, 0));
        player.setFallDistance(0);
    }

    private void removeOldRelicItems(Player player) {
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {

            ItemStack item = player.getInventory().getItem(slot);

            if (item == null) {
                continue;
            }

            if (relicManager.isRelicItem(item)) {
                player.getInventory().setItem(slot, null);
            }
        }
    }

    private static class StopAnimationException extends RuntimeException {
    }
}
