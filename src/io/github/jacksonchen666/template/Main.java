package io.github.jacksonchen666.template;

import io.github.jacksonchen666.template.commands.Template;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        new Template(this);
        getServer().getPluginManager().registerEvents(new Listener(), this);
    }
}
