/*
 * Treecapitator. Cuts down trees for you.
 * Copyright (C) 2020  JacksonChen666
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

    /**
     * Give a player the item
     *
     * @param player The player to give the item to
     */
    public static void giveItem(Player player) {
        player.getInventory().addItem(createItem());
    }

    /**
     * Create the item
     *
     * @return The item
     */
    public static ItemStack createItem() {
        return CustomItemManager.customItem(new ItemStack(Material.GOLDEN_AXE), itemName, lore);
    }
}
