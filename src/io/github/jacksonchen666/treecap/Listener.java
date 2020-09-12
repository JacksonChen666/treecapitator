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
import java.util.HashMap;
import java.util.Map;

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
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        if (mainHand == Material.GOLDEN_AXE &&
                CustomItemManager.isCustomItem(player.getInventory().getItemInMainHand(), TreeCapitator.itemName, TreeCapitator.lore) &&
                Arrays.stream(acceptableBlock).anyMatch(l -> l == event.getBlock().getType())) {
            amounts.put(player, 0);
            chosenBlocks.put(player, event.getBlock().getType());
            breakAroundBlocks(event.getBlock(), player.getInventory().getItemInMainHand(), player);
        }
    }

    public void breakAroundBlocks(final Block block, final ItemStack tool, final Player player) {
        ArrayList<Block> blocksAround = getBlocks(block, 1);
        Material chosenBlock = chosenBlocks.get(player);
        for (Block i : blocksAround) {
            if (i.getType() == chosenBlock) {
                int amount = amounts.getOrDefault(player, 0);
                if (maximum > amount) {
                    i.breakNaturally(tool); // Does not trigger block break event
                    amounts.put(player, amount + 1);
                    breakAroundBlocks(i, tool, player);
                }
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