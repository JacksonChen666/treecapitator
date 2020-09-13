package io.github.jacksonchen666.treecap;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        new TreeCap(this);
        getServer().getPluginManager().registerEvents(new Listener(this), this);
    }
}
