package io.github.jacksonchen666.treecapitator.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TreecapitatorCommandTest {
    private ServerMock server;

    @Before
    public void setUp() throws Exception {
        server = MockBukkit.mock();
        server.addPlayer();
        MockBukkit.load(Treecapitator.class);
    }

    @After
    public void tearDown() throws Exception {
        MockBukkit.unmock();
    }

    @Test
    public void testOnCommand() {
        server.executeConsole("treecapitator", "test");
    }
}