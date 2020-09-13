package io.github.jacksonchen666.treecap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.*;

public class BreakingBlocks extends BukkitRunnable {
    private final Iterator<Block> its;
    private final int blocksAmount;
    private final ItemStack tool;
    private final Player player;

    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    static final Map<Player, Integer> amounts = new HashMap<>();
    static final Map<Player, Material> chosenBlocks = new HashMap<>();
    static final Map<Player, LocalTime> coolDownTo = new HashMap<>();
    public static int maximum = 32;
    public static int cooldown = 2;

    public BreakingBlocks(JavaPlugin plugin, List<Block> blocks, ItemStack tool, Player player) {
        this.its = blocks.iterator();
        blocksAmount = blocks.size();
        this.tool = tool;
        this.player = player;
        maximum = plugin.getConfig().getInt("maxLogs");
        cooldown = plugin.getConfig().getInt("cooldown");
    }

    @Override
    public void run() {
        for (int i = 0; i < blocksAmount / 5; i++) {
            if (its.hasNext()) {
                int amount = amounts.getOrDefault(player, 0);
                Block block = its.next();
                if (block.breakNaturally(tool)) {
                    amounts.put(player, amount);
                }
            }
            else {
                its.forEachRemaining(block -> {});
                coolDownTo.put(player, LocalTime.now().plusSeconds(cooldown));
                this.cancel();
                break;
            }
        }
    }

    public static List<Block> searchAroundBlocks(final Block target, final Player player) {
        int amount = amounts.getOrDefault(player, 0);
        Material chosenBlock = chosenBlocks.get(player);
        List<Block> blocksToBreak = new ArrayList<>();
        List<Block> toSearch = new ArrayList<>();
        toSearch.add(target);
        while (maximum > amount && toSearch.size() > 0) {
            List<Block> newToSearch = new ArrayList<>();
            for (Block search : toSearch) {
                ArrayList<Block> blocks = getBlocks(search, 1);
                blocks.removeIf(block -> block.getType() != chosenBlock);
                newToSearch.addAll(blocks);
            }
            amount += newToSearch.size();
            blocksToBreak.addAll(newToSearch);
            toSearch = newToSearch;
        }
        return blocksToBreak.size() > maximum ? blocksToBreak.subList(0, maximum) : blocksToBreak;
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
}
