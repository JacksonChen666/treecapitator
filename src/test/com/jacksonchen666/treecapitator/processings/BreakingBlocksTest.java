/*
 * Treecapitator. Cuts down trees for you.
 * Copyright (C) 2020  JacksonChen666
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jacksonchen666.treecapitator.processings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.jacksonchen666.treecapitator.Treecapitator;
import com.jacksonchen666.treecapitator.commands.TreecapitatorItem;
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
    private PlayerMock player1;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        player1 = server.addPlayer();
        MockBukkit.load(Treecapitator.class);
        player1.getInventory().setItemInMainHand(TreecapitatorItem.createItem());
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
        Block start = player1.getWorld().getBlockAt(0, 64, 0);
        List<Block> blocks = BreakingBlocks.getBlocks(start, radius);
        Material toBreak = BreakingBlocks.acceptableBlock[0];
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

    @Test
    public void testCooldown() {
        List<Block> blocks = BreakingBlocks.getBlocks(player1.getWorld().getBlockAt(0, 64, 0), 1);
        Material toBreak = BreakingBlocks.acceptableBlock[0];
        blocks.forEach(block -> block.setType(toBreak));
        player1.simulateBlockBreak(blocks.get(0));
        blocks.stream().filter(BreakingBlocks::acceptableBlock).map(block1 -> "Failed to cut down logs").forEach(Assert::fail);
        blocks.forEach(block -> block.setType(toBreak));
        player1.simulateBlockBreak(blocks.get(0));
        blocks.get(0).setType(toBreak);
        blocks.stream().filter(block -> !BreakingBlocks.acceptableBlock(block)).map(block -> "Failed to not cut down logs").forEach(Assert::fail);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}