package io.github.jacksonchen666.treecapitator.processings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BreakingBlocksTest {
    private ServerMock server;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        MockBukkit.load(Treecapitator.class);
    }

    @Test
    public void testAcceptableBlock() {
        Block block = Objects.requireNonNull(server.getWorld("world")).getBlockAt(0, 0, 0);
        block.setType(BreakingBlocks.acceptableBlock[0]);
        Assert.assertTrue(BreakingBlocks.acceptableBlock(block));
    }

    @Test
    public void getBlocks() {
        List<Block> blocks = BreakingBlocks.getBlocks(Objects.requireNonNull(server.getWorld("world")).getBlockAt(0, 50, 0), 10);
        Assert.assertEquals(9261, blocks.size());
    }

    @Test
    public void acceptableBlock() {
        List<Boolean> acceptables = Arrays.stream(BreakingBlocks.acceptableBlock).map(BreakingBlocks::acceptableBlock).collect(Collectors.toList());
        for (boolean bool : acceptables) {
            if (!bool) {
                Assert.fail("Not all met the correct condition");
                break;
            }
        }
    }

    @Test
    public void testBreakBlocks() {
        int radius = 5;
        Block start = server.getWorlds().get(0).getBlockAt(0, 64, 0);
        List<Block> blocks = BreakingBlocks.getBlocks(start, radius);
        Material toBreak = Material.OAK_LOG;
        for (Block block : blocks) {
            block.setType(toBreak);
        }
        int previous = BreakingBlocks.maxLogs;
        BreakingBlocks.maxLogs = blocks.size();
        new BreakingBlocks(start).breakBlocks();
        BreakingBlocks.maxLogs = previous;
        blocks.removeIf(block -> block.getType() != toBreak);
        Assert.assertEquals("Did not finish cutting logs down. " + blocks.size() + " blocks left uncut.", 0, blocks.size());
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}