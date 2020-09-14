package io.github.jacksonchen666.treecap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@Plugin(name = "TreeCap", version = "1.0.0")
@Description(value = "Capitates trees just like Hypixel")
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        BreakingBlocks.maximum = getConfig().getInt("settings.maxLogs");
        BreakingBlocks.cooldown = getConfig().getInt("settings.cooldown");
        BreakingBlocks.blocksPerTick = getConfig().getInt("settings.blocksPerTick");
        new TreeCap(this);
        getServer().getPluginManager().registerEvents(new Listener(this), this);
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
