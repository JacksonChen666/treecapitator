/*
Treecapitator. Cuts down trees for you.
Copyright (C) 2020 JacksonChen666

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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
    public void testGetBlocks() {
        List<Block> blocks = BreakingBlocks.getBlocks(Objects.requireNonNull(server.getWorld("world")).getBlockAt(0, 50, 0), 10);
        Assert.assertEquals(9261, blocks.size());
    }

    @Test
    public void testAcceptableBlocks() {
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
        BreakingBlocks.breakBlocks(start);
        BreakingBlocks.maxLogs = previous;
        blocks.removeIf(block -> block.getType() != toBreak);
        Assert.assertEquals("Did not finish cutting logs down. " + blocks.size() + " blocks left uncut.", 0, blocks.size());
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}