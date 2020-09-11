package io.github.jacksonchen666.treecap.commands;

import io.github.jacksonchen666.treecap.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Template implements CommandExecutor {
    private final Main plugin;
    public static final String commandName = "treecap";

    public Template(Main plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
    }

    public String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) { // Console. Do your thing or just ignore.
            plugin.getServer().getConsoleSender().sendMessage(getText("console_execution").replace("{prefix}", getText("error_prefix")));
            return false;
        }

        Player p = (Player) commandSender;
        if (s.equalsIgnoreCase(commandName)) { // s variable is what the command was executed
            // Do your thing
            p.sendMessage("This is a treecap command. This has nothing special.");
            return true;
        }
        return false;
    }
}
