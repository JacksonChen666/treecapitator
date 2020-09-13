package io.github.jacksonchen666.treecap;

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

import static io.github.jacksonchen666.treecap.BreakingBlocks.*;

public class Listener implements org.bukkit.event.Listener {
    private final JavaPlugin plugin;

    public Listener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

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
            List<Block> thing = searchAroundBlocks(block, player);
            BreakingBlocks task = new BreakingBlocks(plugin, thing, mainHand, player);
            task.runTaskTimer(plugin, 1L, 1L);
            chosenBlocks.remove(player);
        }
    }
}