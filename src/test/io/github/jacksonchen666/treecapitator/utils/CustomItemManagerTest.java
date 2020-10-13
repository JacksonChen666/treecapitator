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
package io.github.jacksonchen666.treecapitator.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.jacksonchen666.treecapitator.Treecapitator;
import io.github.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class CustomItemManagerTest {
    @Before
    public void setUp() {
        MockBukkit.mock();
        MockBukkit.load(Treecapitator.class);
    }

    @Test
    public void testIsCustomItem() {
        ItemStack item = TreecapitatorItem.createItem();
        Assert.assertTrue("Expected true, got false from isCustomItem", CustomItemManager.isCustomItem(item, TreecapitatorItem.itemName, TreecapitatorItem.lore));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(Arrays.asList("lol", "no"));
        item.setItemMeta(meta);
        Assert.assertFalse("Expected false, got true from isCustomItem", CustomItemManager.isCustomItem(item, TreecapitatorItem.itemName, TreecapitatorItem.lore));
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}