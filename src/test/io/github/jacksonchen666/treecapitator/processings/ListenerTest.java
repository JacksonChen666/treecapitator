package io.github.jacksonchen666.treecapitator.processings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import io.github.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.*;

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
        player1.setOp(true);
        MockBukkit.load(Treecapitator.class);
    }

    @Test
    @Ignore(value = "breakNaturally in block method is not implemented.")
    public void onBlockBreak() {
        player1.getInventory().setItemInMainHand(TreecapitatorItem.createItem());
        World world = Objects.requireNonNull(server.getWorld("world"));
        int y = 4;
        while (world.getBlockAt(0, y, 0).getType() != Material.AIR) {
            y++;
        }
        List<Block> blocks = BreakingBlocks.getBlocks(world.getBlockAt(0, y + 3, 0), 3);
        for (Block block : blocks) {
            block.setType(BreakingBlocks.acceptableBlock[0]);
        }
        server.getScheduler().performOneTick();
        player1.simulateBlockBreak(world.getBlockAt(0, y, 0));
        server.getScheduler().performOneTick();
        blocks = BreakingBlocks.getBlocks(world.getBlockAt(0, y + 3, 0), 3);
        Assert.assertFalse("Did not cut the entire thing.", blocks.stream().anyMatch(block -> block.getType() == BreakingBlocks.acceptableBlock[0]));
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}