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
package com.jacksonchen666.treecapitator.processings;

import com.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import com.jacksonchen666.treecapitator.utils.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.jacksonchen666.treecapitator.processings.BreakingBlocks.*;

public class Listener implements org.bukkit.event.Listener {
    // TODO allow add or remove materials from this list
    public static final Material[] acceptedMaterials = new Material[] {Material.GOLDEN_AXE};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> handsItem = Arrays.asList(player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand());
        Block block = event.getBlock();
        if (handsItem.stream().anyMatch(hand ->
                acceptableBlock(block) && // check broken block
                        Arrays.stream(acceptedMaterials).anyMatch(material -> hand != null && hand.getType() == material) && // match material of item
                        LocalTime.now().isAfter(cooldownTo.getOrDefault(player, LocalTime.now().minusSeconds(1))) && // check cooldown
                        CustomItemManager.isCustomItem(hand, TreecapitatorItem.itemName, TreecapitatorItem.lore) // match custom item
        )) {
            BreakingBlocks.breakBlocks(block);
            cooldownTo.put(player, LocalTime.now().plusSeconds(cooldown));
        }
    }
}