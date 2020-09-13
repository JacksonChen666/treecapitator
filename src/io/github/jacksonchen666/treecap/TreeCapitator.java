package io.github.jacksonchen666.treecap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TreeCapitator {
    public static final String itemName = "Tree Capitator";
    public static final List<String> lore = Arrays.asList("What do i do here", "uwu");

    public static void giveItem(Player player) {
        ItemStack item = createItem();
        player.getInventory().addItem(item);
    }

    public static ItemStack createItem() {
        return CustomItemManager.customItem(new ItemStack(Material.GOLDEN_AXE), itemName, lore);
    }
}
