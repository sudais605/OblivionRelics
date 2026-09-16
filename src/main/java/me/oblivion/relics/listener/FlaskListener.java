package me.oblivion.relics.listener;

import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class FlaskListener implements Listener {

    private final EchoFlask echoFlask;
    private final EnergyManager energyManager;

    public FlaskListener(
            EchoFlask echoFlask,
            EnergyManager energyManager
    ) {
        this.echoFlask = echoFlask;
        this.energyManager = energyManager;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();

        if (!echoFlask.isEchoFlask(item)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        int energy = energyManager.getEnergy(player.getUniqueId());

        if (energy >= EnergyManager.MAX_ENERGY) {
            player.sendMessage(
                    "§b§lOblivion §8» §cYour Energy is already full."
            );
            return;
        }

        energyManager.addEnergy(player.getUniqueId(), 1);

        if (item != null) {
            if (item.getAmount() <= 1) {
                player.getInventory().setItemInMainHand(null);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        }

        player.sendMessage(
                "§b§lOblivion §8» §b+1 Energy"
        );
    }
}
