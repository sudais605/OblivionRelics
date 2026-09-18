package me.oblivion.relics.listener;

import me.oblivion.relics.energy.EchoFlask;
import me.oblivion.relics.energy.EnergyManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathEnergyListener implements Listener {

    private final EnergyManager energyManager;
    private final EchoFlask echoFlask;

    public DeathEnergyListener(
            EnergyManager energyManager,
            EchoFlask echoFlask
    ) {
        this.energyManager = energyManager;
        this.echoFlask = echoFlask;
    }

    @EventHandler
    public void onDeath(
            PlayerDeathEvent event
    ) {

        Player victim =
                event.getEntity();

        int energy =
                energyManager.getEnergy(
                        victim.getUniqueId()
                );

        if (energy > 0) {

            ItemStack flask =
                    echoFlask.create();

            flask.setAmount(
                    energy
            );

            event.getDrops().add(
                    flask
            );

            energyManager.setEnergy(
                    victim.getUniqueId(),
                    0
            );

        } else {

            Player killer =
                    victim.getKiller();

            if (killer != null) {

                killer.sendMessage(
                        ChatColor.GRAY
                                + "The player you killed "
                                + "didn't have any Echo Flask."
                );
            }
        }
    }
}
