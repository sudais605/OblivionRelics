package me.oblivion.relics.listener;

import me.oblivion.relics.relic.RelicItem;
import me.oblivion.relics.relic.RelicManager;
import me.oblivion.relics.relic.RelicType;
import me.oblivion.relics.relic.RerollerItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class RerollerListener implements Listener {

    private final RelicManager relicManager;
    private final RerollerItem rerollerItem;
    private final RelicItem relicItem;

    public RerollerListener(
            RelicManager relicManager,
            RerollerItem rerollerItem,
            RelicItem relicItem
    ) {
        this.relicManager = relicManager;
        this.rerollerItem = rerollerItem;
        this.relicItem = relicItem;
    }

    @EventHandler
    public void onUse(
            PlayerInteractEvent event
    ) {

        Action action =
                event.getAction();

        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {

            return;
        }

        ItemStack held =
                event.getItem();

        if (!rerollerItem.isReroller(held)) {
            return;
        }

        event.setCancelled(true);

        Player player =
                event.getPlayer();

        RelicType current =
                relicManager.getRelic(
                        player.getUniqueId()
                );

        if (current == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "You do not have a Relic to reroll."
            );

            return;
        }

        RelicType newRelic =
                getDifferentRelic(current);

        consumeOne(player);

        Plugin plugin =
                Bukkit.getPluginManager()
                        .getPlugin("OblivionRelics");

        if (plugin == null) {
            return;
        }

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                ticks += 2;

                player.getWorld().spawnParticle(
                        Particle.ENCHANT,
                        player.getLocation()
                                .add(0, 1, 0),
                        12,
                        0.45,
                        0.8,
                        0.45,
                        0.03
                );

                if (ticks % 10 == 0) {

                    player.getWorld().playSound(
                            player.getLocation(),
                            Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                            0.8f,
                            0.7f + ticks * 0.008f
                    );
                }

                if (ticks >= 50) {

                    relicManager.setRelic(
                            player.getUniqueId(),
                            newRelic
                    );

                    removeRelicItems(player);

                    ItemStack item =
                            relicManager.createRelicItem(
                                    newRelic
                            );

                    if (item != null) {
                        player.getInventory()
                                .addItem(item);
                    }

                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            player.getLocation()
                                    .add(0, 1, 0),
                            45,
                            0.7,
                            1.0,
                            0.7,
                            0.04
                    );

                    player.getWorld().playSound(
                            player.getLocation(),
                            Sound.BLOCK_AMETHYST_BLOCK_CHIME,
                            1.4f,
                            1.3f
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    player.sendMessage(
                            "§d§l         RELIC REROLLED"
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            "§7Previous: §c"
                                    + current.getDisplayName()
                    );

                    player.sendMessage(
                            "§7New Relic: §b§l"
                                    + newRelic.getDisplayName()
                    );

                    player.sendMessage("");

                    player.sendMessage(
                            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━"
                    );

                    player.sendTitle(
                            "§b§l"
                                    + newRelic.getDisplayName(),
                            "§7Your Relic has changed",
                            10,
                            45,
                            15
                    );

                    cancel();
                }
            }

        }.runTaskTimer(
                plugin,
                0L,
                2L
        );
    }

    private RelicType getDifferentRelic(
            RelicType current
    ) {

        RelicType[] relics =
                RelicType.values();

        RelicType result;

        do {

            result =
                    relics[
                            ThreadLocalRandom.current()
                                    .nextInt(
                                            relics.length
                                    )
                    ];

        } while (result == current);

        return result;
    }

    private void consumeOne(
            Player player
    ) {

        ItemStack item =
                player.getInventory()
                        .getItemInMainHand();

        if (!rerollerItem.isReroller(item)) {
            return;
        }

        if (item.getAmount() <= 1) {

            player.getInventory()
                    .setItemInMainHand(null);

        } else {

            item.setAmount(
                    item.getAmount() - 1
            );

            player.getInventory()
                    .setItemInMainHand(item);
        }
    }

    private void removeRelicItems(
            Player player
    ) {

        for (int slot = 0;
             slot < player.getInventory().getSize();
             slot++) {

            ItemStack item =
                    player.getInventory()
                            .getItem(slot);

            if (relicItem.isRelic(item)) {

                player.getInventory()
                        .setItem(slot, null);
            }
        }
    }
}
