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

import java.util.Arrays;
import java.util.HashMap;
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

    @Test
    public void testSettingsNonOp() {
        CommandResult result = server.execute("treecapitator", player2, "maxLogs", "123456789");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cYou are missing permission: treecapitator.settings"));
        result.assertFailed();
    }

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

    @Test
    public void testSettingNumberRequired() {
        CommandResult result = server.execute("treecapitator", player1, "maxLogs", "lol");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cMust be a number"));
        result.assertFailed();
    }

    @Test
    public void testSettingUnknownSetting() {
        CommandResult result = server.execute("treecapitator", player1, "adfsfeq", "1234");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cUnknown setting"));
        result.assertFailed();
    }

    @Test
    public void testConsoleError() {
        CommandResult result = server.executeConsole("treecapitator");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cYou are missing arguments: player/setting"));
        result.assertFailed();
    }

    @Test
    public void testTabComplete() {
        HashMap<String[], Integer> tests = new HashMap<>();
        tests.put(new String[] {""}, TreecapitatorCommand.arg2No0.size() + server.getOnlinePlayers().size());
        tests.put(new String[] {"maxLogs", ""}, 6);
        tests.put(new String[] {"maxLogs", "16384", ""}, 0);
        tests.put(new String[] {"cooldown", ""}, 5);
        tests.put(new String[] {"cooldown", "5", ""}, 0);
        tests.put(new String[] {player2.getName(), ""}, 0);
        tests.keySet().stream().filter(test -> TreecapitatorCommand.tabComplete(player1, test).size() != tests.get(test)).map(Arrays::toString).forEach(Assert::fail);
        tests.keySet().stream().filter(test -> !(TreecapitatorCommand.tabComplete(player2, test).size() == 0)).map(Arrays::toString).forEach(Assert::fail);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}