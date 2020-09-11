package io.github.jacksonchen666.treecap;

import io.github.jacksonchen666.treecap.commands.Template;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Listener(), this);
    }
}
