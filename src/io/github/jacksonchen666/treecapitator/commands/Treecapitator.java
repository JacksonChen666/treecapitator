package io.github.jacksonchen666.treecapitator.commands;

import io.github.jacksonchen666.treecapitator.utils.CustomItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Treecapitator {
    public static final String itemName = "Treecapitator";
    public static final List<String> lore = Arrays.asList(
            ChatColor.DARK_PURPLE.toString() + "Treecapitator",
            ChatColor.GRAY.toString() + "A forceful Gold Axe which can",
            ChatColor.GRAY.toString() + "break a large amount of logs in",
            ChatColor.GRAY.toString() + "a single hit!",
            ChatColor.DARK_GRAY.toString() + "Cooldown: " + ChatColor.GREEN.toString() + "2s",
            "",
            ChatColor.BOLD.toString() + ChatColor.DARK_PURPLE.toString() + "EPIC AXE"
    );

    public static void giveItem(Player player) {
        ItemStack item = createItem();
        player.getInventory().addItem(item);
    }

    public static ItemStack createItem() {
        return CustomItemManager.customItem(new ItemStack(Material.GOLDEN_AXE), itemName, lore);
    }
}
