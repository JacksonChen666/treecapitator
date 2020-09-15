package io.github.jacksonchen666.treecapitator.processings;

import io.github.jacksonchen666.treecapitator.commands.Treecapitator;
import io.github.jacksonchen666.treecapitator.utils.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.acceptableBlock;
import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.cooldownTo;

public class Listener implements org.bukkit.event.Listener {
    private final JavaPlugin plugin;

    public Listener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> handsItem = Arrays.asList(player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand());
        Block block = event.getBlock();
        for (ItemStack hand : handsItem) {
            if (LocalTime.now().isAfter(cooldownTo.getOrDefault(player, LocalTime.now().minusSeconds(1))) &&
                    hand.getType() == Material.GOLDEN_AXE &&
                    CustomItemManager.isCustomItem(hand, Treecapitator.itemName, Treecapitator.lore) &&
                    Arrays.stream(acceptableBlock).anyMatch(l -> l == block.getType())) {
                new BreakingBlocks(block, player).runTaskTimer(plugin, 1L, 1L);
                break;
            }
        }
    }
}