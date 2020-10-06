package io.github.jacksonchen666.treecapitator.commands;

import io.github.jacksonchen666.treecapitator.utils.CustomItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TreecapitatorItem {
    public static final String itemName = ChatColor.DARK_PURPLE.toString() + "Treecapitator";
    public static final List<String> lore = Arrays.asList(
            ChatColor.GRAY.toString() + "A forceful Gold Axe which can",
            ChatColor.GRAY.toString() + "break a large amount of logs in",
            ChatColor.GRAY.toString() + "a single hit!",
            ChatColor.DARK_GRAY.toString() + "Default cooldown: " + ChatColor.GREEN.toString() + "2s",
            "",
            ChatColor.DARK_PURPLE.toString() + "§lEPIC AXE"
    );

    public static void giveItem(Player player) {
        player.getInventory().addItem(createItem());
        // TODO check if the inventory updates after adding item
        //        player.updateInventory();
    }

    public static ItemStack createItem() {
        return CustomItemManager.customItem(new ItemStack(Material.GOLDEN_AXE), itemName, lore);
    }
}