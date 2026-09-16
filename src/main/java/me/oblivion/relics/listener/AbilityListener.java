package me.oblivion.relics.listener;

import me.oblivion.relics.ability.RiftAbilities;
import me.oblivion.relics.relic.RelicType;
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

        event.setCancelled(true);

        if (event.getPlayer().isSneaking()) {
            return;
        }

        // Actual Relic check will be added here.
        // For the first test we will connect it through the main class.
    }
}
