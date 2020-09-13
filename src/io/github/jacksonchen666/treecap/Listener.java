package io.github.jacksonchen666.treecap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalTime;
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
    static final Map<Player, LocalTime> coolDownTo = new HashMap<>();
    public static int maximum = 32;
    public static int coolDownSeconds = 2;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        if (LocalTime.now().isAfter(coolDownTo.getOrDefault(player, LocalTime.now().minusSeconds(1))) &&
                mainHand.getType() == Material.GOLDEN_AXE &&
                CustomItemManager.isCustomItem(mainHand, TreeCapitator.itemName, TreeCapitator.lore) &&
                Arrays.stream(acceptableBlock).anyMatch(l -> l == block.getType())) {
            chosenBlocks.put(player, block.getType());
            breakAroundBlocks(block, mainHand, player);
            coolDownTo.put(player, LocalTime.now().plusSeconds(coolDownSeconds));
            chosenBlocks.remove(player);
            amounts.remove(player);
        }
    }

    public void breakAroundBlocks(final Block target, final ItemStack tool, final Player player) {
        Material chosenBlock = chosenBlocks.get(player);
        List<Block> blocksAround = Collections.singletonList(target);
        while (maximum > amounts.getOrDefault(player, 0) && blocksAround.size() > 0) { // the "didn't break enough" loop
            List<Block> blocksAroundBlocksAround = new ArrayList<>();
            blocksAround.stream().map(block -> getBlocks(block, 1)).forEach(blocksAroundBlocksAround::addAll);
            blocksAroundBlocksAround.removeIf(block -> block.getType() != chosenBlock);
            blocksAroundBlocksAround.forEach(block -> {
                int amount = amounts.getOrDefault(player, 0);
                if (maximum > amount && block.breakNaturally(tool)) {
                    amounts.put(player, amount + 1);
                }
            });
            blocksAround = blocksAroundBlocksAround;
        }
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