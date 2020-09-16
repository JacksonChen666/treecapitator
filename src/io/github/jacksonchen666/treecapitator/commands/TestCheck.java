package io.github.jacksonchen666.treecapitator.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.getBlocks;

public class TestCheck extends BukkitRunnable {
    private final Player player;
    private final Block start;
    private final Material originalMaterial;

    public TestCheck(Player player, Block start, Material originalMaterial) {
        this.player = player;
        this.start = start;
        this.originalMaterial = originalMaterial;
    }

    @Override
    public void run() {
        List<Block> check = getBlocks(start, 10);
        check.removeIf(block -> block.getType() == originalMaterial);
        if (check.size() != 0) {
            player.sendMessage("There are " + check.size() + " logs left uncut");
        }
        else {
            player.sendMessage("Ok it works fine");
        }
    }
}
