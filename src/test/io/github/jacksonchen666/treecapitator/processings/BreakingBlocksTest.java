package io.github.jacksonchen666.treecapitator.processings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
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
    public void testAcceptableMaterial() {
        List<Boolean> acceptables = Arrays.stream(BreakingBlocks.acceptableBlock).map(BreakingBlocks::acceptableBlock).collect(Collectors.toList());
        for (boolean bool : acceptables) {
            if (!bool) {
                Assert.fail("Not all met the correct condition");
                break;
            }
        }
    }

    @Test
    public void testAcceptableBlock() {
        Block block = Objects.requireNonNull(server.getWorld("world")).getBlockAt(0, 0, 0);
        block.setType(BreakingBlocks.acceptableBlock[0]);
        Assert.assertTrue(BreakingBlocks.acceptableBlock(block));
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}