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
    private final Iterator<List<Block>> its;
    private final ItemStack tool;
    private final Player player;

    public BreakingBlocks(Iterator<List<Block>> its, ItemStack tool, Player player) {
        this.its = its;
        this.tool = tool;
        this.player = player;
    }

    @Override
    public void run() {
        if (its.hasNext()) {
            List<Block> t = its.next();
            for (Block block : t) {
                int amount = amounts.getOrDefault(player, 0);
                if (maximum > amount) {
                    if (block.breakNaturally(tool)) {
                        amounts.put(player, amount + 1);
                    }
                }
                else {
                    this.cancel();
                }
            }
        }
        else {
            this.cancel();
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        coolDownTo.put(player, LocalTime.now().plusSeconds(coolDownSeconds));
    }
}
