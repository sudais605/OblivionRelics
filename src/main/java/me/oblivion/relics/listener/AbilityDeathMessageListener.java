package me.oblivion.relics.listener;

import me.oblivion.relics.ability.RelicAbilityEngine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AbilityDeathMessageListener implements Listener {

    private final RelicAbilityEngine engine;

    public AbilityDeathMessageListener(
            RelicAbilityEngine engine
    ) {
        this.engine = engine;
    }

    @EventHandler
    public void onDeath(
            PlayerDeathEvent event
    ) {

        Player victim =
                event.getEntity();

        if (!(victim.getLastDamageCause()
                instanceof EntityDamageByEntityEvent damageEvent)) {

            return;
        }

        if (!(damageEvent.getDamager()
                instanceof Player killer)) {

            return;
        }

        if (engine.isTrusted(
                killer,
                victim
        )) {

            return;
        }

        RelicAbilityEngine.AbilityRecord record =
                engine.getRecentAbilityUse(killer);

        if (record == null) {
            return;
        }

        String ability =
                record.getAbilityName();

        event.setDeathMessage(
                ChatColor.GRAY
                        + victim.getName()
                        + getPhrase(ability)
                        + killer.getName()
                        + "'s "
                        + ability
                        + "."
        );
    }

    private String getPhrase(
            String ability
    ) {

        return switch (ability) {

            case "Rift Dash" ->
                    " was torn through by ";

            case "Storm Pierce" ->
                    " was struck down by ";

            case "Frost Lance" ->
                    " was frozen by ";

            case "Ember Shot" ->
                    " was burned by ";

            case "Shadow Pierce" ->
                    " was pierced by ";

            case "Time Lock" ->
                    " was stopped by ";

            case "Phantom Strike" ->
                    " was struck by ";

            case "Soul Burst" ->
                    " had their soul shattered by ";

            case "Celestial Spear" ->
                    " was pierced by ";

            case "Aegis Bash" ->
                    " was crushed by ";

            case "Force Push" ->
                    " was blasted away by ";

            case "Arcane Bolt" ->
                    " was hit by ";

            default ->
                    " was defeated by ";
        };
    }
}
