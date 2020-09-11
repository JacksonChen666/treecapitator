package io.github.jacksonchen666.treecap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class Listener implements org.bukkit.event.Listener {
    public static final Material[] acceptableBlock = {
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };
    private Material chosenBlock;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        if (mainHand == Material.GOLDEN_AXE &&
                CustomItemManager.isCustomItem(player.getInventory().getItemInMainHand(), TreeCapitator.itemName, TreeCapitator.lore) &&
                Arrays.stream(acceptableBlock).anyMatch(l -> l == event.getBlock().getType())) {
            chosenBlock = event.getBlock().getType(); // this could resolve in possible conflict between each players
            breakAroundBlocks(event.getBlock(), player.getInventory().getItemInMainHand());
        }
    }

    public void breakAroundBlocks(final Block block, final ItemStack tool) {
        ArrayList<Block> blocksAround = getBlocks(block, 1);
        for (Block i : blocksAround) {
            if (i.getType() == chosenBlock) {
                i.breakNaturally(tool); // Does not trigger block break event
                //                tool.setDurability((short) (tool.getDurability() + 1)); // deprecated method. also, it's unbreakable tools on hypixel
                breakAroundBlocks(i, tool);
            }
        }
    }

    // https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/
    public ArrayList<Block> getBlocks(Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
            for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
                for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }
}