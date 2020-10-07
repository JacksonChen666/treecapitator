package io.github.jacksonchen666.treecapitator.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.CommandResult;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import io.github.jacksonchen666.treecapitator.utils.ChatColors;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

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

    @Test
    public void testGetItem() {
        CommandResult result = server.execute("treecapitator", player1);
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Ok, here is your treecapitator."));
        ItemStack item = TreecapitatorItem.createItem();
        Assert.assertTrue(IntStream.range(0, 35).mapToObj(i -> player1.getInventory().getItem(i)).anyMatch(item::equals));
        result.assertSucceeded();
    }

    @Test
    public void testGiveItemToOther() {
        CommandResult result = server.execute("treecapitator", player1, player2.getName());
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Gave " + player2.getName() + " a treecapitator."));
        ItemStack item = TreecapitatorItem.createItem();
        Assert.assertTrue(IntStream.range(0, 35).mapToObj(i -> player2.getInventory().getItem(i)).anyMatch(item::equals));
        result.assertSucceeded();
    }

    @Test
    public void testGiveItemToInvalidName() {
        CommandResult result = server.execute("treecapitator", player1, "NotAName");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cNotAName is not online!"));
        result.assertFailed(); // for the command, just check source code
    }

    @Test
    public void testGiveItemNonOp() {
        CommandResult result = server.execute("treecapitator", player2);
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cYou are missing permission: treecapitator.getItem"));
        result.assertFailed();
    }

    @Test
    public void testGiveItemToOthersNonOp() {
        CommandResult result = server.execute("treecapitator", player2, player1.getName());
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cYou are missing permission: treecapitator.giveItem"));
        result.assertFailed();
    }

    @Test
    public void testGiveItemInvalidNameNonOp() {
        CommandResult result = server.execute("treecapitator", player2, "NotAName");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cYou are missing permission: treecapitator.giveItem"));
        result.assertFailed();
    }

    //    @Test
    //    public void testTreecapitator() { // disabled because y level is too high and getMaxHeight is not implemented
    //        int radius = 5;
    //        server.execute("treecapitator", player1, "test", String.valueOf(radius));
    //        Block start = server.getWorlds().get(0).getBlockAt(0, server.getWorlds().get(0).getMaxHeight() - 5, 0);
    //        List<Block> blocks = getBlocks(start, 5);
    //        blocks.removeIf(block -> block.getType() != Material.OAK_LOG);
    //        Assert.assertTrue("Did not finish cutting logs down. " + blocks.size() + " blocks left uncut.", blocks.size() > 1);
    //    }

    @Test
    public void testSettingCooldown() {
        CommandResult result = server.execute("treecapitator", player1, "cooldown", "5");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &aSet cooldown to 5 seconds"));
        result.assertSucceeded();
    }

    @Test
    public void testSettingMaxLogs() {
        CommandResult result = server.execute("treecapitator", player1, "maxLogs", "16384");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &aSet max logs to cut to 16384"));
        result.assertSucceeded();
    }

    @Test
    public void testSettingMaxLogsDanger() {
        CommandResult result = server.execute("treecapitator", player1, "maxLogs", "32767");
        result.assertResponse(ChatColors.color("&4&lWARNING! &cChoosing a number above 16384 could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for extremely large numbers. &4&l&oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY WAY, SHAPE, OR FORM."));
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &aSet max logs to cut to 32767"));
        result.assertSucceeded();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}