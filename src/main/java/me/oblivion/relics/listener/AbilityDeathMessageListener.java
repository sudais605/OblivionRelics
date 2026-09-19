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
    public void onDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        if (!(victim.getLastDamageCause()
                instanceof EntityDamageByEntityEvent damageEvent)) {
            return;
        }

        if (!(damageEvent.getDamager()
                instanceof Player killer)) {
            return;
        }

        if (engine.isTrusted(killer, victim)) {
            return;
        }

        RelicAbilityEngine.AbilityRecord record =
                engine.getRecentAbilityUse(killer);

        if (record == null) {
            return;
        }

        String ability =
                record.getAbilityName();

        String message =
                ChatColor.GRAY
                        + victim.getName()
                        + getDeathPhrase(ability)
                        + killer.getName()
                        + "'s "
                        + ability
                        + ".";

        event.setDeathMessage(message);
    }

    private String getDeathPhrase(
            String ability
    ) {

        return switch (ability) {

            case "Rift Pull" ->
                    " was pulled apart by ";

            case "Rift Dash" ->
                    " was torn through by ";

            case "Rift Break" ->
                    " was shattered by ";

            case "Gravity Well" ->
                    " was dragged down by ";

            case "Gravity Lift" ->
                    " was crushed by ";

            case "Gravity Crush" ->
                    " was crushed by ";

            case "Void Step" ->
                    " was consumed by ";

            case "Void Snare" ->
                    " was trapped by ";

            case "Void Collapse" ->
                    " was erased by ";

            case "Storm Pierce" ->
                    " was struck down by ";

            case "Tempest Dash" ->
                    " was torn apart by ";

            case "Storm Cage" ->
                    " was overwhelmed by ";

            case "Frost Lance" ->
                    " was frozen by ";

            case "Ice Prison" ->
                    " was trapped in ice by ";

            case "Frozen Ground" ->
                    " was frozen by ";

            case "Flame Burst" ->
                    " was burned by ";

            case "Ember Shot" ->
                    " was burned by ";

            case "Inferno Ring" ->
                    " was engulfed by ";

            case "Shadow Blink" ->
                    " was caught by ";

            case "Shadow Pierce" ->
                    " was pierced by ";

            case "Blackout" ->
                    " was swallowed by darkness from ";

            case "Time Acceleration" ->
                    " was overwhelmed by ";

            case "Rewind" ->
                    " was outplayed by ";

            case "Time Lock" ->
                    " was stopped by ";

            case "Phantom Rush" ->
                    " was struck by ";

            case "Phase Shift" ->
                    " was overwhelmed by ";

            case "Phantom Strike" ->
                    " was struck by ";

            case "Soul Pull" ->
                    " had their soul pulled by ";

            case "Soul Mark" ->
                    " was marked by ";

            case "Soul Burst" ->
                    " had their soul shattered by ";

            case "Chaos Bolt" ->
                    " was destroyed by ";

            case "Chaos Swap" ->
                    " was outplayed by ";

            case "Chaos Field" ->
                    " was overwhelmed by ";

            case "Celestial Spear" ->
                    " was pierced by ";

            case "Celestial Guard" ->
                    " was overwhelmed by ";

            case "Starfall" ->
                    " was crushed beneath ";

            case "Aegis Bash" ->
                    " was crushed by ";

            case "AutoCrit" ->
                    " was critically struck by ";

            case "Aegis Break" ->
                    " was shattered by ";

            case "Force Push" ->
                    " was blasted away by ";

            case "Force Launch" ->
                    " was launched by ";

            case "Force Wave" ->
                    " was hit by ";

            case "Arcane Bolt" ->
                    " was struck by ";

            case "Arcane Rift" ->
                    " was overwhelmed by ";

            case "Arcane Burst" ->
                    " was destroyed by ";

            default ->
                    " was defeated by ";
        };
    }
}
