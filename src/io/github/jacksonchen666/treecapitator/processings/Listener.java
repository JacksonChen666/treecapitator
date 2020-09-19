package io.github.jacksonchen666.treecapitator.processings;

import io.github.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import io.github.jacksonchen666.treecapitator.utils.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.acceptableBlock;
import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.cooldownTo;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> handsItem = Arrays.asList(player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand());
        Block block = event.getBlock();
        for (ItemStack hand : handsItem) {
            if (LocalTime.now().isAfter(cooldownTo.getOrDefault(player, LocalTime.now().minusSeconds(1))) &&
                    hand.getType() == Material.GOLDEN_AXE &&
                    CustomItemManager.isCustomItem(hand, TreecapitatorItem.itemName, TreecapitatorItem.lore) &&
                    Arrays.stream(acceptableBlock).anyMatch(l -> l == block.getType())) {
                BreakingBlocks.breakBlocks(block);
                break;
            }
        }
    }
}