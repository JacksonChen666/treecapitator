package io.github.jacksonchen666.treecap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Listener implements org.bukkit.event.Listener {
    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    static final Map<Player, Integer> amounts = new HashMap<>();
    static final Map<Player, Material> chosenBlocks = new HashMap<>();
    public static int maximum = 32;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.GOLDEN_AXE &&
                CustomItemManager.isCustomItem(mainHand, TreeCapitator.itemName, TreeCapitator.lore) &&
                Arrays.stream(acceptableBlock).anyMatch(l -> l == event.getBlock().getType())) {
            amounts.put(player, 0);
            chosenBlocks.put(player, event.getBlock().getType());
            breakAroundBlocks(event.getBlock(), mainHand, player);
        }
    }

    public void breakAroundBlocks(final Block target, final ItemStack tool, final Player player) {
        List<Block> blocksAround = getBlocks(target, 1);
        Material chosenBlock = chosenBlocks.get(player);
        blocksAround.removeIf(block -> block.getType() != chosenBlock);
        for (Block i : Collections.unmodifiableList(blocksAround)) {
            int amount = amounts.getOrDefault(player, 0);
            if (maximum >= amount && i.breakNaturally(tool)) { // Does not trigger block break event and only returns true if block is broken
                amounts.put(player, amount + 1);
            }
            else {
                break;
            }
        }
        breakAroundBlocks(blocksAround, tool, player);
    }

    public void breakAroundBlocks(final List<Block> blocks, final ItemStack tool, final Player player) {
        blocks.forEach(block -> breakAroundBlocks(block, tool, player));
    }

    // https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
    public ArrayList<Block> getBlocks(Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getX() - radius; x <= start.getX() + radius; x++)
            for (double y = start.getY() - radius; y <= start.getY() + radius; y++)
                for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++)
                    blocks.add(new Location(start.getWorld(), x, y, z).getBlock());
        return blocks;
    }
}