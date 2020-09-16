package io.github.jacksonchen666.treecapitator.processings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.*;

public class BreakingBlocks extends BukkitRunnable {
    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    protected static final Map<Player, Integer> amounts = new HashMap<>();
    protected static final Map<Player, LocalTime> cooldownTo = new HashMap<>();
    public static int maxLogs = 32;
    public static int cooldown = 2;
    public static int blocksPerTick = 128;
    private final Player player;
    private final List<Block> lastBreak = new ArrayList<>();
    private final List<Block> thisBreak = new ArrayList<>();
    private final List<Block> nextBreak = new ArrayList<>();
    private final List<Long> times = new ArrayList<>();
    private int currentBlocks = 0;

    public BreakingBlocks(Block brokenBlock, Player player) {
        this.player = player;
        lastBreak.add(brokenBlock);
    }

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

    @Override
    public void run() {
        // TODO test without scheduling then fix and then with scheduling
        long start = System.nanoTime();
        if (lastBreak.size() == 0) {
            long end = System.nanoTime();
            times.add(end - start);
            this.cancel();
            return;
        }
        currentBlocks = 0;
        if (nextBreak.size() != 0) {
            try {
                for (Block block : nextBreak) {
                    processBlock(block);
                }
            }
            catch (ConcurrentModificationException ignored) {

            }
        }
        for (Block block : lastBreak) {
            processBlock(block);
        }
        lastBreak.clear();
        lastBreak.addAll(thisBreak);
        thisBreak.clear();
        long end = System.nanoTime();
        times.add(end - start);
    }

    public void breakBlocks() { // All in one tick
        amounts.put(player, 0);
        Bukkit.getLogger().info("Started chopping down logs");
        long start = System.nanoTime();
        while (lastBreak.size() != 0) {
            for (Block block : lastBreak) {
                processBlock2(block);
            }
            lastBreak.clear();
            lastBreak.addAll(thisBreak);
            thisBreak.clear();
        }
        long end = System.nanoTime();
        Bukkit.getLogger().info("Finished in " + (end - start) / 1E+6 + "ms");
    }

    private void processBlock(Block block) {
        int amount = amounts.getOrDefault(player, 0);
        if (maxLogs > amount) {
            if (blocksPerTick > currentBlocks) {
                if (acceptableBlock(block) && block.breakNaturally()) {
                    thisBreak.addAll(getBlocks(block, 1));
                    currentBlocks++;
                    amounts.put(player, amount + 1);
                }
            }
            else {
                nextBreak.add(block);
            }
        }
        else {
            this.cancel();
        }
    }

    private void processBlock2(Block block) {
        int amount = amounts.getOrDefault(player, 0);
        if (maxLogs > amount) {
            if (acceptableBlock(block) && block.breakNaturally()) {
                thisBreak.addAll(getBlocks(block, 1));
                amounts.put(player, amount + 1);
            }
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        cooldownTo.put(player, LocalTime.now().plusSeconds(cooldown));
        long time = times.stream().mapToLong(time1 -> time1).sum();
        Bukkit.getLogger().info("Finished chopping down " + amounts.get(player) + " logs in " + time / 1E+6 + "ms (average " + time / times.size() / 1E+6 + "ms)");
        amounts.remove(player);
        super.cancel();
    }
}
