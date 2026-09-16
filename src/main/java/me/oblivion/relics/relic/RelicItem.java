package me.oblivion.relics.relic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RelicItem {

    private final NamespacedKey relicKey;

    public RelicItem(JavaPlugin plugin) {
        this.relicKey = new NamespacedKey(plugin, "relic_type");
    }

    public ItemStack create(RelicType relicType) {

        ItemStack item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.AQUA + "" + ChatColor.BOLD
                        + relicType.getDisplayName()
        );

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "Abilities");
        lore.add("");

        lore.add(
                ChatColor.WHITE + "F "
                        + ChatColor.DARK_GRAY + "— "
                        + ChatColor.AQUA
                        + getAbilityOneName(relicType)
        );

        lore.add(
                ChatColor.WHITE + "SHIFT + F "
                        + ChatColor.DARK_GRAY + "— "
                        + ChatColor.AQUA
                        + getAbilityTwoName(relicType)
        );

        lore.add(
                ChatColor.WHITE + "CTRL + F "
                        + ChatColor.DARK_GRAY + "— "
                        + ChatColor.AQUA
                        + getAbilityThreeName(relicType)
        );

        lore.add("");

        lore.add(
                ChatColor.GRAY + "Ability II unlocks at "
                        + ChatColor.AQUA + "8 Energy"
        );

        lore.add(
                ChatColor.GRAY + "Ability III unlocks at "
                        + ChatColor.AQUA + "10 Energy"
        );

        meta.setLore(lore);

        meta.getPersistentDataContainer().set(
                relicKey,
                PersistentDataType.STRING,
                relicType.name()
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isRelic(ItemStack item) {

        if (item == null
                || item.getType() != Material.ECHO_SHARD) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(
                relicKey,
                PersistentDataType.STRING
        );
    }

    public RelicType getRelicType(ItemStack item) {

        if (!isRelic(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        String value = meta.getPersistentDataContainer().get(
                relicKey,
                PersistentDataType.STRING
        );

        if (value == null) {
            return null;
        }

        try {
            return RelicType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String getAbilityOneName(RelicType relic) {

        return switch (relic) {
            case RIFT -> "Rift Pull";
            case GRAVITY -> "Gravity Pull";
            case VOID -> "Void Step";
            case STORM -> "Storm Pierce";
            case FROST -> "Frost Lance";
            case INFERNO -> "Flame Rush";
            case SHADOW -> "Shadow Blink";
            case TIME -> "Time Dash";
            case PHANTOM -> "Phantom Rush";
            case SOUL -> "Soul Pull";
            case CHAOS -> "Chaos Bolt";
            case CELESTIAL -> "Celestial Spear";
            case AEGIS -> "Aegis Bash";
            case FORCE -> "Force Push";
            case ARCANE -> "Arcane Bolt";
        };
    }

    private String getAbilityTwoName(RelicType relic) {

        return switch (relic) {
            case RIFT -> "Rift Dash";
            case GRAVITY -> "Gravity Shift";
            case VOID -> "Void Gate";
            case STORM -> "Tempest Dash";
            case FROST -> "Ice Slide";
            case INFERNO -> "Ember Shot";
            case SHADOW -> "Shadow Pierce";
            case TIME -> "Rewind";
            case PHANTOM -> "Phase Shift";
            case SOUL -> "Soul Mark";
            case CHAOS -> "Chaos Swap";
            case CELESTIAL -> "Celestial Dash";
            case AEGIS -> "AutoCrit";
            case FORCE -> "Force Launch";
            case ARCANE -> "Arcane Rift";
        };
    }

    private String getAbilityThreeName(RelicType relic) {

        return switch (relic) {
            case RIFT -> "Rift Break";
            case GRAVITY -> "Gravity Crush";
            case VOID -> "Void Collapse";
            case STORM -> "Storm Cage";
            case FROST -> "Frozen Ground";
            case INFERNO -> "Inferno Ring";
            case SHADOW -> "Blackout";
            case TIME -> "Time Lock";
            case PHANTOM -> "Phantom Strike";
            case SOUL -> "Soul Burst";
            case CHAOS -> "Chaos Field";
            case CELESTIAL -> "Starfall";
            case AEGIS -> "Aegis Break";
            case FORCE -> "Force Wave";
            case ARCANE -> "Arcane Burst";
        };
    }
}
