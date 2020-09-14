package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.*;

import static io.github.jacksonchen666.treecap.TreeCap.getText;

public class BreakingBlocks extends BukkitRunnable {
    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    protected static final Map<Player, Integer> amounts = new HashMap<>();
    protected static final Map<Player, LocalTime> coolDownTo = new HashMap<>();
    public static int maximum = 32;
    public static int cooldown = 2;
    public static int blocksPerTick = 256; // amount to break in every tick
    public static int searchTimeoutSeconds = 20;
    private static long startTime;
    private final Iterator<Block> its;
    private final ItemStack tool;
    private final Player player;
    private final int blockCount;
    private final List<Long> timeSpentEachTime = new ArrayList<>();

    public BreakingBlocks(List<Block> blocks, ItemStack tool, Player player) {
        its = blocks.iterator();
        blockCount = blocks.size();
        this.tool = tool;
        this.player = player;
    }

    public static List<Block> searchAroundBlocks(final Block target, final Material chosenBlock, final Player player) {
        List<Block> blocksToBreak = new ArrayList<>();
        List<Block> toSearch = new ArrayList<>();
        toSearch.add(target);
        while (maximum > blocksToBreak.size() && toSearch.size() > 0) {
            List<Block> newToSearch = new ArrayList<>();
            for (Block search : toSearch) {
                ArrayList<Block> blocks;
                try {
                    startTime = System.nanoTime();
                    blocks = getBlocks(search, 1);
                }
                catch (SearchTimeoutException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColors.color(getText("search_timeout", getText("prefix")).replace("{seconds}", String.valueOf(searchTimeoutSeconds))));
                    return Collections.emptyList();
                }
                newToSearch.addAll(blocks);
            }
            newToSearch.removeIf(block -> block.getType() != chosenBlock);
            blocksToBreak.addAll(newToSearch);
            toSearch = newToSearch;
        }
        blocksToBreak.removeIf(block -> block.getType() != chosenBlock);
        return blocksToBreak;
    }

    // https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
    public static ArrayList<Block> getBlocks(Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++)
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++)
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                    if ((System.nanoTime() - startTime) / 1E+6 / 1000 > searchTimeoutSeconds) {
                        throw new SearchTimeoutException("Took more than " + searchTimeoutSeconds + " seconds to get all blocks");
                    }
                    blocks.add(new Location(start.getWorld(), x, y, z).getBlock());
                }
        return blocks;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        for (int i = 0; i < (blocksPerTick > blockCount ? blocksPerTick : blockCount / 10); i++) {
            int amount = amounts.getOrDefault(player, 0);
            if (maximum > amount && its.hasNext()) {
                Block block = its.next();
                if (block.breakNaturally(tool)) {
                    amounts.put(player, amount + 1);
                }
            }
            else {
                long end = System.nanoTime();
                timeSpentEachTime.add(end - start);
                long nanoTimeSpent = timeSpentEachTime.stream().mapToLong(nanoTime -> nanoTime).sum();

                Bukkit.getLogger().info("[Treecap] Finished cutting " + amount + " logs for " + player.getName() + ", took " + (nanoTimeSpent / 1E+6) + "ms (average " + (nanoTimeSpent / timeSpentEachTime.size() / 1E+6) + "ms).");
                coolDownTo.put(player, LocalTime.now().plusSeconds(cooldown));
                amounts.remove(player);
                this.cancel();
                return;
            }
        }
        long end = System.nanoTime();
        timeSpentEachTime.add(end - start);
    }
}
