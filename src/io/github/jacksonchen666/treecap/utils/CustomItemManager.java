package io.github.jacksonchen666.treecap.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

// https://bukkit.org/threads/creating-custom-item.360665/
public class CustomItemManager {
    public static ItemStack customItem(ItemStack item, String displayName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // --Commented out by Inspection START (2020. 09. 15. 21:03:48:797):
    //    public static ItemStack customItem(Player player, ItemStack item, String displayName, List<String> lore) {
    //        ItemMeta meta = item.getItemMeta();
    //        Objects.requireNonNull(meta).setDisplayName(displayName);
    //        meta.setLore(lore);
    //        item.setItemMeta(meta);
    //        player.getInventory().addItem(item);
    //        player.updateInventory();
    //        return item;
    //    }
    // --Commented out by Inspection STOP (2020. 09. 15. 21:03:48:797)

    // --Commented out by Inspection START (2020. 09. 15. 21:04:01:474):
    //    public static boolean isCustomItem(ItemStack item) {
    //        return item.hasItemMeta() && (Objects.requireNonNull(item.getItemMeta()).hasDisplayName() || item.getItemMeta().hasLore());
    //    }
    // --Commented out by Inspection STOP (2020. 09. 15. 21:04:01:474)

    // --Commented out by Inspection START (2020. 09. 15. 21:04:04:565):
    //    public static boolean isCustomItem(ItemStack item, String displayName) {
    //        return item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(displayName);
    //    }
    // --Commented out by Inspection STOP (2020. 09. 15. 21:04:04:565)

    // --Commented out by Inspection START (2020. 09. 15. 21:04:04:550):
    //    public static boolean isCustomItem(ItemStack item, List<String> lore) {
    //        return item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasLore() && Objects.equals(item.getItemMeta().getLore(), lore);
    //    }
    // --Commented out by Inspection STOP (2020. 09. 15. 21:04:04:550)

    public static boolean isCustomItem(ItemStack item, String displayName, List<String> lore) {
        return item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() && item.getItemMeta().hasLore() && item.getItemMeta().getDisplayName().equalsIgnoreCase(displayName) && Objects.equals(item.getItemMeta().getLore(), lore);
    }
}
