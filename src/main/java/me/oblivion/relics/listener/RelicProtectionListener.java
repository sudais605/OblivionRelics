package me.oblivion.relics.listener;

import me.oblivion.relics.relic.RelicManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class RelicProtectionListener implements Listener {

    private final RelicManager relicManager;

    public RelicProtectionListener(
            RelicManager relicManager
    ) {
        this.relicManager = relicManager;
    }

    @EventHandler
    public void onDrop(
            PlayerDropItemEvent event
    ) {

        if (relicManager.isRelicItem(
                event.getItemDrop().getItemStack()
        )) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(
            PlayerDeathEvent event
    ) {

        event.getDrops().removeIf(
                relicManager::isRelicItem
        );
    }

    @EventHandler
    public void onRespawn(
            PlayerRespawnEvent event
    ) {

        Player player =
                event.getPlayer();

        if (!relicManager.hasRelic(player)) {
            return;
        }

        var plugin =
                Bukkit.getPluginManager()
                        .getPlugin("OblivionRelics");

        if (plugin == null) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {

                    if (!player.isOnline()
                            || !relicManager.hasRelic(player)) {
                        return;
                    }

                    for (ItemStack item :
                            player.getInventory()
                                    .getContents()) {

                        if (relicManager.isRelicItem(item)) {
                            return;
                        }
                    }

                    ItemStack relic =
                            relicManager.createRelicItem(
                                    player.getUniqueId()
                            );

                    if (relic != null) {
                        player.getInventory()
                                .addItem(relic);
                    }
                },
                1L
        );
    }

    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        ItemStack current =
                event.getCurrentItem();

        ItemStack cursor =
                event.getCursor();

        boolean currentIsRelic =
                relicManager.isRelicItem(current);

        boolean cursorIsRelic =
                relicManager.isRelicItem(cursor);

        int topSize =
                event.getView()
                        .getTopInventory()
                        .getSize();

        boolean clickedTop =
                event.getRawSlot() >= 0
                        && event.getRawSlot() < topSize;

        // Prevent putting a Relic into
        // any open container.
        if (clickedTop && cursorIsRelic) {

            event.setCancelled(true);
            return;
        }

        // Prevent shift-clicking a Relic
        // from player inventory into a container.
        if (!clickedTop
                && currentIsRelic
                && event.getAction()
                == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

            event.setCancelled(true);
            return;
        }

        // Prevent hotbar-swapping a Relic
        // into a container.
        if (clickedTop
                && event.getHotbarButton() >= 0) {

            Player player =
                    (Player) event.getWhoClicked();

            ItemStack hotbar =
                    player.getInventory()
                            .getItem(
                                    event.getHotbarButton()
                            );

            if (relicManager.isRelicItem(hotbar)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {

        if (!relicManager.isRelicItem(
                event.getOldCursor()
        )) {
            return;
        }

        int topSize =
                event.getView()
                        .getTopInventory()
                        .getSize();

        for (int rawSlot :
                event.getRawSlots()) {

            if (rawSlot >= 0
                    && rawSlot < topSize) {

                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryMove(
            InventoryMoveItemEvent event
    ) {

        if (relicManager.isRelicItem(
                event.getItem()
        )) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFrame(
            PlayerInteractEntityEvent event
    ) {

        if (!(event.getRightClicked()
                instanceof ItemFrame)
                && !(event.getRightClicked()
                instanceof GlowItemFrame)) {

            return;
        }

        Player player =
                event.getPlayer();

        if (relicManager.isRelicItem(
                player.getInventory()
                        .getItemInMainHand()
        )
                || relicManager.isRelicItem(
                player.getInventory()
                        .getItemInOffHand()
        )) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUnexpectedItemSpawn(
            ItemSpawnEvent event
    ) {

        if (relicManager.isRelicItem(
                event.getEntity()
                        .getItemStack()
        )) {

            event.setCancelled(true);
        }
    }
}
