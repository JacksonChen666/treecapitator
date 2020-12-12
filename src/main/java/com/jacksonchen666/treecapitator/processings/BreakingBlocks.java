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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalTime;
import java.util.*;

public class BreakingBlocks {
    protected static final Map<Player, LocalTime> cooldownTo = new HashMap<>();
    private static final Map<Material, List<Material>> acceptableItemAndBlock = new HashMap<>();
    public static int maxLogs = 32;
    public static int cooldown = 2;

    public static void putBlock(Material item, Material block) {
        List<Material> temp = acceptableItemAndBlock.get(item);
        temp.add(block);
        acceptableItemAndBlock.put(item, temp);
    }

    public static void putItem(Material item) {
        putItem(item, new ArrayList<>());
    }

    public static void putItem(Material item, List<Material> blocks) {
        acceptableItemAndBlock.put(item, blocks);
    }

    public static void removeBlock(Material item, Material block) {
        List<Material> temp = acceptableItemAndBlock.get(item);
        temp.remove(block);
        acceptableItemAndBlock.put(item, temp);
    }

    public static void removeItem(Material item) {
        acceptableItemAndBlock.remove(item);
    }

    public static Map<Material, List<Material>> getAcceptableItemAndBlock() {
        return acceptableItemAndBlock;
    }

    /**
     * Gives a list of blocks from the starting point and a radius around the starting point.
     * https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
     *
     * @param start  The starting point
     * @param radius The radius
     * @return List of blocks from the start and around the blocks
     */
    // https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
    public static List<Block> getBlocks(Block start, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++)
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++)
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++)
                    blocks.add(new Location(start.getWorld(), x, y, z).getBlock());
        return blocks;
    }

    /**
     * Check if the block is acceptable
     *
     * @param item  The material of the item
     * @param block The material of the block
     * @return Acceptable
     */
    public static boolean acceptableItemAndBlock(Material item, Material block) {
        for (Material material : acceptableItemAndBlock.get(item)) {
            if (block == material) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the block is acceptable
     *
     * @param item  The item
     * @param block The block
     * @return Acceptable
     */
    public static boolean acceptableItemAndBlock(ItemStack item, Block block) {
        return acceptableItemAndBlock(item.getType(), block.getType());
    }

    /**
     * Check if the block is acceptable
     *
     * @param item  The material of the item
     * @param block The block
     * @return Acceptable
     */
    public static boolean acceptableItemAndBlock(Material item, Block block) {
        return acceptableItemAndBlock(item, block.getType());
    }

    /**
     * Break the blocks around a block with the same material
     *
     * @param player       The player
     * @param blockToBreak The block to break
     */
    public static void breakBlocks(Player player, Block blockToBreak) { // 9261 blocks from 500ms (not near the cutting) to 1200ms (near the cutting)
        final List<Block> thisBreak = new ArrayList<>();
        final List<Block> lastBreak = new ArrayList<>(Collections.singletonList(blockToBreak));
        int amount = 0;
        Bukkit.getLogger().info("Started chopping down logs");
        long start = System.nanoTime();
        while (lastBreak.size() != 0) {
            for (Block block : lastBreak) {
                if (maxLogs > amount && acceptableItemAndBlock(player.getInventory().getItemInMainHand(), block) && block.breakNaturally()) {
                    thisBreak.addAll(getBlocks(block, 1));
                    amount++;
                }
            }
            lastBreak.clear();
            lastBreak.addAll(thisBreak);
            thisBreak.clear();
        }
        long end = System.nanoTime();
        Bukkit.getLogger().info("Finished in " + (end - start) / 1E+6 + "ms");
    }
}
