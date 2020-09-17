package io.github.jacksonchen666.treecapitator.processings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.*;

public class BreakingBlocks {
    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    protected static final Map<Player, Integer> amounts = new HashMap<>();
    protected static final Map<Player, LocalTime> cooldownTo = new HashMap<>();
    private static final List<Block> lastBreak = new ArrayList<>();
    private static final List<Block> thisBreak = new ArrayList<>();
    public static int maxLogs = 32;
    public static int cooldown = 2;

    // https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
    public static ArrayList<Block> getBlocks(Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++)
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++)
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++)
                    blocks.add(new Location(start.getWorld(), x, y, z).getBlock());
        return blocks;
    }

    public static boolean acceptableBlock(Material material) {
        return Arrays.stream(acceptableBlock).anyMatch(material1 -> material == material1);
    }

    public static boolean acceptableBlock(Block block) {
        return acceptableBlock(block.getType());
    }

    public static void breakBlocks(Block brokenBlock, Player player) { // 19000 blocks in 1.2 seconds
        lastBreak.add(brokenBlock);
        amounts.put(player, 0);
        Bukkit.getLogger().info("Started chopping down logs");
        long start = System.nanoTime();
        while (lastBreak.size() != 0) {
            for (Block block : lastBreak) {
                int amount = amounts.getOrDefault(player, 0);
                if (maxLogs > amount) {
                    if (acceptableBlock(block) && block.breakNaturally()) {
                        thisBreak.addAll(getBlocks(block, 1));
                        amounts.put(player, amount + 1);
                    }
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
