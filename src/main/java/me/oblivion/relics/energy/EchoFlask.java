package me.oblivion.relics.energy;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EchoFlask {

    private final NamespacedKey flaskKey;

    public EchoFlask(JavaPlugin plugin) {
        this.flaskKey = new NamespacedKey(plugin, "echo_flask");
    }

    public ItemStack create() {
        ItemStack flask = new ItemStack(Material.PAPER);

        ItemMeta meta = flask.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§b§lEcho Flask");

            meta.setLore(java.util.List.of(
                    "§7Restores §b1 Energy",
                    "",
                    "§8Right-click to use"
            ));

            meta.getPersistentDataContainer().set(
                    flaskKey,
                    org.bukkit.persistence.PersistentDataType.BYTE,
                    (byte) 1
            );

            flask.setItemMeta(meta);
        }

        return flask;
    }

    public boolean isEchoFlask(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        Byte value = meta.getPersistentDataContainer().get(
                flaskKey,
                org.bukkit.persistence.PersistentDataType.BYTE
        );

        return value != null && value == (byte) 1;
    }
}
