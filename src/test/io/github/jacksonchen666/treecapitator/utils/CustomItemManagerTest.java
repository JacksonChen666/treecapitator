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
    public void isCustomItem() {
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