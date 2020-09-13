package io.github.jacksonchen666.treecap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "TreeCap", version = "1.0.0")
@Description(value = "Capitates trees just like Hypixel")
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        new TreeCap(this);
        getServer().getPluginManager().registerEvents(new Listener(this), this);
    }
}
