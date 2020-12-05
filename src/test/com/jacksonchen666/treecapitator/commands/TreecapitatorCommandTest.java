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
package com.jacksonchen666.treecapitator.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.CommandResult;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.jacksonchen666.treecapitator.Treecapitator;
import com.jacksonchen666.treecapitator.utils.ChatColors;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class TreecapitatorCommandTest {
    private ServerMock server;
    private PlayerMock player1;
    private PlayerMock player2;
    private final HashMap<String[], Integer> tabCompletes = new HashMap<>();

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        player1 = server.addPlayer();
        player1.setOp(true);
        player2 = server.addPlayer();
        MockBukkit.load(Treecapitator.class);
        tabCompletes.put(new String[] {""}, TreecapitatorCommand.arg2No0.size() + server.getOnlinePlayers().size());
        tabCompletes.put(new String[] {"maxLogs", ""}, 6);
        tabCompletes.put(new String[] {"maxLogs", "16384", ""}, 0);
        tabCompletes.put(new String[] {"cooldown", ""}, 5);
        tabCompletes.put(new String[] {"cooldown", "5", ""}, 0);
        tabCompletes.put(new String[] {player2.getName(), ""}, 0);
        tabCompletes.put(new String[] {"blocksAndItems", ""}, 3);
        tabCompletes.put(new String[] {"blocksAndItems", "no", "", ""}, 0);
        tabCompletes.put(new String[] {"blocksAndItems", "add", ""}, 2);
        long result = 0L;
        for (Material material1 : Material.values()) {
            if (material1.isItem()) {
                result++;
            }
        }
        tabCompletes.put(new String[] {"blocksAndItems", "add", "item", ""}, (int) result);
        tabCompletes.put(new String[] {"blocksAndItems", "add", "item", "GOLDEN_AXE", ""}, 0);
        tabCompletes.put(new String[] {"blocksAndItems", "add", "block", ""}, 1);
        long count = 0L;
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                count++;
            }
        }
        tabCompletes.put(new String[] {"blocksAndItems", "add", "block", "GOLDEN_AXE", ""}, (int) count);
        tabCompletes.put(new String[] {"blocksAndItems", "remove", ""}, 2);
        tabCompletes.put(new String[] {"blocksAndItems", "remove", "item", ""}, 1);
        tabCompletes.put(new String[] {"blocksAndItems", "remove", "item", "GOLDEN_AXE", ""}, 0);
        tabCompletes.put(new String[] {"blocksAndItems", "remove", "block", ""}, 1);
        tabCompletes.put(new String[] {"blocksAndItems", "remove", "block", "GOLDEN_AXE", ""}, 12);
        tabCompletes.put(new String[] {"blocksAndItems", "remove", "block", "GOLDEN_AXE", "OAK_LOG", ""}, 0);
    }

    @Test
    public void testGetItem() {
        CommandResult result = server.execute("treecapitator", player1);
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Ok, here is your treecapitator."));
        ItemStack item = TreecapitatorItem.createItem();
        boolean b = false;
        for (int i = 0; i < 35; i++) {
            ItemStack itemStack = player1.getInventory().getItem(i);
            if (item.equals(itemStack)) {
                b = true;
                break;
            }
        }
        Assert.assertTrue(b);
        result.assertSucceeded();
    }

    @Test
    public void testGiveItemToOther() {
        CommandResult result = server.execute("treecapitator", player1, player2.getName());
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Gave " + player2.getName() + " a treecapitator."));
        ItemStack item = TreecapitatorItem.createItem();
        boolean b = false;
        for (int i = 0; i < 35; i++) {
            ItemStack itemStack = player2.getInventory().getItem(i);
            if (item.equals(itemStack)) {
                b = true;
                break;
            }
        }
        Assert.assertTrue(b);
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
    public void testBlocksAndItemsUnknownSetting() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "lol no");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cUnknown setting"));
        result.assertFailed();
    }

    @Test
    public void testBlocksAndItemsAddItem() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "add", "item", "golden_axe");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Added GOLDEN_AXE to the list of usable items."));
        result.assertSucceeded();
    }

    @Test
    public void testBlocksAndItemsAddBlock() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "add", "block", "golden_axe", "dirt");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Added DIRT to GOLDEN_AXE."));
        result.assertSucceeded();
    }

    @Test
    public void testBlocksAndItemsRemoveItem() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "remove", "item", "golden_axe");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Removed GOLDEN_AXE from the list of usable items."));
        result.assertSucceeded();
    }

    @Test
    public void testBlocksAndItemsRemoveBlock() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "remove", "block", "golden_axe", "oak_log");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r Removed OAK_LOG from GOLDEN_AXE."));
        result.assertSucceeded();
    }

    @Test
    public void testBlocksAndItemsAddItemUnknownItem() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "add", "item", "no");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &4Unknown item no"));
        result.assertFailed();
    }

    @Test
    public void testBlocksAndItemsAddBlockUnknownBlock() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "add", "block", "golden_axe", "no");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &4Unknown item no"));
        result.assertFailed();
    }

    @Test
    public void testBlocksAndItemsRemoveBlockUnknownBlock() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "remove", "block", "golden_axe", "no");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &4Unknown item no"));
        result.assertFailed();
    }

    @Test
    public void testBlocksAndItemsAddBlockUnknownItem() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "add", "block", "no", "dirt");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &4Unknown item no"));
        result.assertFailed();
    }

    @Test
    public void testBlocksAndItemsRemoveBlockUnknownItem() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "remove", "block", "no", "dirt");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &4Unknown item no"));
        result.assertFailed();
    }

    @Test
    public void testBlocksAndItemsList() {
        CommandResult result = server.execute("treecapitator", player1, "blocksAndItems", "list");
        result.assertResponse("{GOLDEN_AXE=[ACACIA_LOG, BIRCH_LOG, DARK_OAK_LOG, JUNGLE_LOG, OAK_LOG, SPRUCE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_BIRCH_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG]}");
        result.assertSucceeded();
    }

    @Test
    public void testConsoleError() {
        CommandResult result = server.executeConsole("treecapitator");
        result.assertResponse(ChatColors.color("&a[&rTreecapitator&a]&r &cYou are missing arguments: player/setting"));
        result.assertFailed();
    }

    @Test
    public void testTabCompleteOPPlayer() {
        PluginCommand command = Objects.requireNonNull(server.getPluginCommand("treecapitator"));
        for (String[] string : tabCompletes.keySet()) {
            Assert.assertEquals(Arrays.toString(string), command.tabComplete(player1, "treecapitator", string).size(), (int) tabCompletes.get(string));
        }
    }

    @Test
    public void testTabCompleteNormalPlayer() {
        PluginCommand command = Objects.requireNonNull(server.getPluginCommand("treecapitator"));
        for (String[] string : tabCompletes.keySet()) {
            Assert.assertEquals(Arrays.toString(string), 0, command.tabComplete(player2, "treecapitator", string).size());
        }
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}