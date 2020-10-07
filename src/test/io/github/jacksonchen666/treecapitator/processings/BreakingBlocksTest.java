package io.github.jacksonchen666.treecapitator.processings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BreakingBlocksTest {

    @Before
    public void setUp() {
        ServerMock server = MockBukkit.mock();
        server.addSimpleWorld("world");
        MockBukkit.load(Treecapitator.class);
    }

    @Test
    public void testAcceptableBlock() {
        List<Boolean> acceptables = Arrays.stream(BreakingBlocks.acceptableBlock).map(BreakingBlocks::acceptableBlock).collect(Collectors.toList());
        for (boolean bool : acceptables) {
            if (!bool) {
                Assert.fail("Not all met the correct condition");
                break;
            }
        }
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}