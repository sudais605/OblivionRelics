package me.oblivion.relics.listener;

import me.oblivion.relics.ability.RiftAbilities;
import me.oblivion.relics.relic.RelicType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class AbilityListener implements Listener {

    private final RiftAbilities riftAbilities;

    public AbilityListener(RiftAbilities riftAbilities) {
        this.riftAbilities = riftAbilities;
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {

        Player player = event.getPlayer();

        /*
         * F is detected through PlayerSwapHandItemsEvent.
         *
         * Sneaking + F will be used later for Ability II.
         */
        boolean sneaking = player.isSneaking();

        /*
         * Only interfere with F if the player currently
         * has the Rift Relic.
         */
        if (!hasRiftRelic(player)) {
            return;
        }

        /*
         * Stop the normal off-hand swap for Relic users.
         */
        event.setCancelled(true);

        /*
         * Ability II is Shift + F.
         * We will connect Rift Dash here after Ability II
         * has been implemented.
         */
        if (sneaking) {
            return;
        }

        /*
         * Ability I
         */
        riftAbilities.riftPull(player);
    }

    private boolean hasRiftRelic(Player player) {
        /*
         * The RiftAbilities class handles the actual ability.
         * For now this listener checks the player's held Relic
         * through the player's stored Relic item.
         */
        for (org.bukkit.inventory.ItemStack item
                : player.getInventory().getContents()) {

            if (item == null) {
                continue;
            }

            org.bukkit.inventory.meta.ItemMeta meta =
                    item.getItemMeta();

            if (meta == null) {
                continue;
            }

            if (!item.getType().equals(
                    org.bukkit.Material.ECHO_SHARD)) {
                continue;
            }

            String displayName = meta.getDisplayName();

            if (displayName != null
                    && displayName.contains(
                    RelicType.RIFT.getDisplayName())) {
                return true;
            }
        }

        return false;
    }
}
