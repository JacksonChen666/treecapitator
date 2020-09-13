package io.github.jacksonchen666.treecap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

import static io.github.jacksonchen666.treecap.Listener.*;

public class BreakingBlocks extends BukkitRunnable {
    private final Iterator<Block> its;
    private final int blocksAmount;
    private final ItemStack tool;
    private final Player player;

    public BreakingBlocks(List<Block> blocks, ItemStack tool, Player player) {
        this.its = blocks.iterator();
        blocksAmount = blocks.size();
        this.tool = tool;
        this.player = player;
    }

    @Override
    public void run() {
        for (int i = 0; i < blocksAmount / 4; i++) {
            if (its.hasNext()) {
                int amount = amounts.getOrDefault(player, 0);
                Block block = its.next();
                if (block.breakNaturally(tool)) {
                    amounts.put(player, amount);
                }
            }
            else {
                its.forEachRemaining(block -> {});
                coolDownTo.put(player, LocalTime.now().plusSeconds(coolDownSeconds));
                this.cancel();
                break;
            }
        }
    }
}
