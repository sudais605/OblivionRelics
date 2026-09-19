package me.oblivion.relics.relic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RerollerItem {

    private final NamespacedKey key;

    public RerollerItem(
            JavaPlugin plugin
    ) {
        key = new NamespacedKey(
                plugin,
                "relic_reroller"
        );
    }

    public ItemStack create() {

        /*
         * AMETHYST_SHARD is only the temporary
         * vanilla item.
         *
         * Later the resource pack can replace
         * its texture with the real Reroller.
         */

        ItemStack item =
                new ItemStack(
                        Material.AMETHYST_SHARD
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.LIGHT_PURPLE.toString()
                        + ChatColor.BOLD.toString()
                        + "Relic Reroller"
        );

        List<String> lore =
                new ArrayList<>();

        lore.add(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━"
        );

        lore.add(
                ChatColor.GRAY
                        + "A mysterious relic catalyst."
        );

        lore.add("");

        lore.add(
                ChatColor.WHITE
                        + "Use: "
                        + ChatColor.LIGHT_PURPLE
                        + "Right-Click"
        );

        lore.add(
                ChatColor.GRAY
                        + "Rerolls your current Relic."
        );

        lore.add(
                ChatColor.GRAY
                        + "Your current Relic cannot"
        );

        lore.add(
                ChatColor.GRAY
                        + "be selected again."
        );

        lore.add("");

        lore.add(
                ChatColor.LIGHT_PURPLE
                        + "✦ A new Relic awaits..."
        );

        lore.add(
                ChatColor.DARK_GRAY
                        + "━━━━━━━━━━━━━━━━━━━━"
        );

        meta.setLore(lore);

        meta.getPersistentDataContainer().set(
                key,
                PersistentDataType.BYTE,
                (byte) 1
        );

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isReroller(
            ItemStack item
    ) {

        if (item == null) {
            return false;
        }

        if (item.getType()
                != Material.AMETHYST_SHARD) {

            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        Byte value =
                meta.getPersistentDataContainer()
                        .get(
                                key,
                                PersistentDataType.BYTE
                        );

        return value != null
                && value == (byte) 1;
    }
}
