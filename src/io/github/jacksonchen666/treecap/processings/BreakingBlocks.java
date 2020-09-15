package io.github.jacksonchen666.treecap.processings;

import io.github.jacksonchen666.treecap.exceptions.SearchTimeoutException;
import io.github.jacksonchen666.treecap.utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.*;

import static io.github.jacksonchen666.treecap.commands.TreeCap.getText;

public class BreakingBlocks extends BukkitRunnable {
    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    protected static final Map<Player, Integer> amounts = new HashMap<>();
    protected static final Map<Player, LocalTime> coolDownTo = new HashMap<>();
    public static int maxLogs = 32;
    public static int cooldown = 2;
    public static int blocksPerTick = 256; // amount to break in every tick
    public static int searchTimeout = 20;
    private static long startTime;
    private static Material chosenBlock;
    private final Player player;
    private final int blockCount;
    private final List<Long> timeSpentEachTime = new ArrayList<>();
    private Iterator<Block> its;
    private Iterator<Block> itsOld = Collections.emptyIterator();
    private List<Block> listThing;
    private List<Block> oldListThing;

    public BreakingBlocks(List<Block> blocks, Player player) {
        its = blocks.iterator();
        listThing = blocks;
        blockCount = blocks.size();
        this.player = player;
    }

    public static List<Block> searchAroundBlocks(final Block target, final Player player, boolean changeChosenBlock) {
        return searchAroundBlocks(target, player, amounts.getOrDefault(player, 0), changeChosenBlock);
    }

    public static List<Block> searchAroundBlocks(final Block target, final Player player, final int amount, boolean changeChosenBlock) {
        Bukkit.getLogger().info("[TreeCap] " + player.getName() + " cut a tree with a maximum limit of " + maxLogs + ". Searching...");
        long start = System.nanoTime();
        List<Block> blocksToBreak = new ArrayList<>();
        List<Block> toSearch = new ArrayList<>();
        toSearch.add(target);
        if (changeChosenBlock) {
            chosenBlock = target.getType();
        }
        while (maxLogs > blocksToBreak.size() + amount && toSearch.size() > 0) {
            List<Block> newToSearch = new ArrayList<>();
            for (Block search : toSearch) {
                List<Block> blocks;
                startTime = System.nanoTime();
                try {
                    blocks = getBlocks(search, 1);
                }
                catch (SearchTimeoutException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColors.color(getText("search_timeout", getText("prefix")).replace("{seconds}", String.valueOf(searchTimeout))));
                    return Collections.emptyList();
                }
                newToSearch.addAll(blocks);
            }
            blocksToBreak.addAll(newToSearch);
            toSearch = newToSearch;
        }
        blocksToBreak.removeIf(block -> block.getType() != chosenBlock);
        long end = System.nanoTime();
        Bukkit.getLogger().info("[TreeCap] Finished searching for " + player.getName() + " in " + (end - start) / 1e+6 + "ms, breaking " + maxLogs + "/" + blocksToBreak.size() + " logs...");
        return blocksToBreak;
    }

    // https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
    public static ArrayList<Block> getBlocks(Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++)
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++)
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                    if ((System.nanoTime() - startTime) / 1E+6 / 1000 > searchTimeout) {
                        throw new SearchTimeoutException("Took more than " + searchTimeout + " seconds to get all blocks");
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
            if (its.hasNext()) {
                if (maxLogs > amount) {
                    Block block = its.next();
                    if (block.getType() == chosenBlock && block.breakNaturally()) {
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
            else {
                long end = System.nanoTime();
                timeSpentEachTime.add(end - start);
                if (maxLogs > amount && listThing.size() > 0 && itsOld.hasNext()) {
                    if (!its.hasNext()) {
                        oldListThing = listThing;
                        itsOld = oldListThing.iterator();
                    }
                    listThing = searchAroundBlocks(itsOld.next(), player, false); // this thing searches for air instead of the intended block type
                    its = listThing.iterator();
                }
                else if (listThing.size() == 0) {
                    //noinspection UnusedAssignment because it will continue looping every tick
                    amount = maxLogs;
                }
            }
        }
        long end = System.nanoTime();
        timeSpentEachTime.add(end - start);
    }
}
