package io.github.jacksonchen666.treecapitator;

import io.github.jacksonchen666.treecapitator.commands.TreecapitatorCommand;
import io.github.jacksonchen666.treecapitator.commands.TreecapitatorItem;
import io.github.jacksonchen666.treecapitator.processings.BreakingBlocks;
import io.github.jacksonchen666.treecapitator.processings.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Treecapitator extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        BreakingBlocks.maxLogs = getConfig().getInt("settings.maxLogs");
        BreakingBlocks.cooldown = getConfig().getInt("settings.cooldown");
        new TreecapitatorCommand(this);
        getServer().getPluginManager().registerEvents(new Listener(), this);

        // recipe
        ItemStack axe = TreecapitatorItem.createItem();
        NamespacedKey key = new NamespacedKey(this, "treecapitator");
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
