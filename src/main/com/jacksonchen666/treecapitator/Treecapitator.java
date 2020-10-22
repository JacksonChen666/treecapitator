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
package com.jacksonchen666.treecapitator;

import com.jacksonchen666.treecapitator.commands.TreecapitatorCommand;
import com.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import com.jacksonchen666.treecapitator.processings.BreakingBlocks;
import com.jacksonchen666.treecapitator.processings.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Treecapitator extends JavaPlugin {
    private NamespacedKey key;

    public Treecapitator() { // unit testing
        super();
    }

    protected Treecapitator(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) { // unit testing
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            List<Map<?, ?>> configOut = new ArrayList<>();
            List<String> blocksAndItemsList = getConfig().getStringList("settings.blocksAndItemsList");
            for (String item : blocksAndItemsList) {
                Map<String, List<String>> temp2 = new HashMap<>();
                temp2.put(item, getConfig().getStringList("settings.blocksAndItems." + item));
                configOut.add(temp2);
            }
            // get map config which is List<Map<?, ?>>
            // get the keys of Map<?, ?>
            // with the keys, process the value of the keys:
            // turn object into strings, then into material enum
            // now use the list of values to add it to acceptableBlocksAndItems (with the key also being a material enum)
            // also throws NullPointerException if the given item doesn't exist and stops loading
            configOut.forEach(map -> map.keySet().forEach(key -> BreakingBlocks.putItem(Objects.requireNonNull(Material.getMaterial(key.toString().toUpperCase())), ((List<?>) map.get(key)).stream().map(listValue -> Objects.requireNonNull(Material.getMaterial(listValue.toString().toUpperCase()))).collect(Collectors.toList()))));
        }
        catch (NullPointerException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An unknown item has been passed in the configuration file. Please check the config file for any non-existent minecraft items.", e);
            return;
        }

        BreakingBlocks.maxLogs = getConfig().getInt("settings.maxLogs");
        BreakingBlocks.cooldown = getConfig().getInt("settings.cooldown");

        new TreecapitatorCommand(this);
        getServer().getPluginManager().registerEvents(new Listener(), this);

        // recipe
        ItemStack axe = TreecapitatorItem.createItem();
        key = new NamespacedKey(this, "treecapitator");
        ShapedRecipe recipe = new ShapedRecipe(key, axe);
        recipe.shape(
                "GGG",
                "GXG",
                "GGG"
        );
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('X', Material.GOLDEN_AXE);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        Bukkit.removeRecipe(key);
    }

    @Override
    public void saveDefaultConfig() {
        String config = "config.yml";
        File file = new File(getDataFolder(), config);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            saveResource(config, false);
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        }
        catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
