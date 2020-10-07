package io.github.jacksonchen666.treecapitator.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.CommandResult;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import io.github.jacksonchen666.treecapitator.utils.ChatColors;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.getBlocks;

public class TreecapitatorCommandTest {
    private ServerMock server;
    private PlayerMock player1;
    private PlayerMock player2;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        player1 = server.addPlayer();
        player1.setOp(true);
        player2 = server.addPlayer();
        MockBukkit.load(Treecapitator.class);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testOnCommand() {
        CommandResult resultGive = server.execute("treecapitator", player1);
        resultGive.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Ok, here is your treecapitator."));
        Assert.assertTrue(Objects.requireNonNull(player1.getInventory()).contains(TreecapitatorItem.createItem()));

        CommandResult resultGiveOther = server.execute("treecapitator", player1, player2.getName());
        resultGiveOther.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Gave " + player2.getName() + " a treecapitator."));
        Assert.assertTrue(Objects.requireNonNull(player2.getInventory()).contains(TreecapitatorItem.createItem()));

        CommandResult resultGiveNone = server.execute("treecapitator", player1, "not_a_name");
        resultGiveNone.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cnot_a_name is not online!"));

        CommandResult resultGetOpLess = server.execute("treecapitator", player2);
        resultGetOpLess.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r  &cYou are missing permission: treecapitator.getItem"));

        CommandResult resultGiveOpLess = server.execute("treecapitator", player2, player1.getName());
        resultGiveOpLess.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r  &cYou are missing permission: treecapitator.giveItem"));

        CommandResult resultGiveNoneOpLess = server.execute("treecapitator", player2, "not_a_name");
        resultGiveNoneOpLess.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r  &cYou are missing permission: treecapitator.giveItem"));

        server.executePlayer("treecapitator", "test", "10");
        Block start = server.getWorlds().get(0).getBlockAt(0, 150, 0);
        List<Block> blocks = getBlocks(start, 10);
        blocks.removeIf(block -> block.getType() != Material.OAK_LOG);
        Assert.assertTrue("Did not finish cutting logs down. " + blocks.size() + " blocks left uncut.", blocks.size() > 1);
    }
}