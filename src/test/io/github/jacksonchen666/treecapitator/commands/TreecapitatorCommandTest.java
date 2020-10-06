package io.github.jacksonchen666.treecapitator.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.CommandResult;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

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
        CommandResult resultGive = server.executePlayer("treecapitator");
        Assert.assertTrue(Objects.requireNonNull(player1.getInventory()).contains(TreecapitatorItem.createItem()));

        CommandResult resultGiveOther = server.executePlayer("treecapitator", player1.getName());
        Assert.assertTrue(Objects.requireNonNull(player2.getInventory()).contains(TreecapitatorItem.createItem()));

        CommandResult resultGiveNone = server.executePlayer("treecapitator", "not_a_name");
        resultGiveNone.assertResponse("&a[&rTreecapitator&a]&r &cnot_a_name is not online!");

        CommandResult resultTest = server.executePlayer("treecapitator", "test", "10");
    }
}