package io.github.jacksonchen666.treecapitator.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.getBlocks;

class TestCheck extends BukkitRunnable {
    private final CommandSender commandSender;
    private final Block start;
    private final Material toBreak;
    private final int radius;

    TestCheck(CommandSender commandSender, Block start, Material toBreak, int radius) {
        this.commandSender = commandSender;
        this.start = start;
        this.toBreak = toBreak;
        this.radius = radius;
    }

    @Override
    public void run() {
        List<Block> check = getBlocks(start, radius);
        check.removeIf(block -> block.getType() == toBreak);
        if (check.size() != 0) {
            commandSender.sendMessage("There are " + check.size() + " logs left uncut");
        }
        else {
            commandSender.sendMessage("Ok it works fine");
        }
    }
}
