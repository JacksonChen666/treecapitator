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
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import io.github.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

public class ListenerTest {
    private ServerMock server;
    private PlayerMock player1;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        player1 = server.addPlayer();
        MockBukkit.load(Treecapitator.class);
    }

    @Test
    public void testOnBlockBreak() {
        player1.getInventory().setItemInMainHand(TreecapitatorItem.createItem());
        World world = Objects.requireNonNull(server.getWorld("world"));
        int y = 64;
        List<Block> blocks = BreakingBlocks.getBlocks(world.getBlockAt(0, y, 0), 3);
        for (Block block : blocks) {
            block.setType(BreakingBlocks.acceptableBlock[0]);
        }
        BreakingBlocks.maxLogs = blocks.size();
        player1.simulateBlockBreak(world.getBlockAt(0, y, 0));
        blocks = BreakingBlocks.getBlocks(world.getBlockAt(0, y, 0), 3);
        Assert.assertFalse("Did not cut the entire thing.", blocks.stream().anyMatch(BreakingBlocks::acceptableBlock));
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}