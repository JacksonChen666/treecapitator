package io.github.jacksonchen666.treecapitator.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestCheckTest {
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
    public void run() {
        Block block = Objects.requireNonNull(server.getWorld("world")).getBlockAt(0, 50, 0);
        block.setType(Material.OAK_LOG);
        server.getScheduler().performOneTick();

        TestCheck testCheck = new TestCheck(player1, block, Material.OAK_LOG, 1);
        testCheck.run();
        assertNotEquals("Test check responded with OK status, while it has leftover block.", "Ok it works fine", player1.nextMessage());

        block.setType(Material.AIR);
        server.getScheduler().performOneTick();
        testCheck.run();
        assertEquals("Test check responded with leftover status, while it has no leftover blocks", "Ok it works fine", player1.nextMessage());
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}