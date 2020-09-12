package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TreeCap implements CommandExecutor {
    public static final String commandName = "treecap";
    private final Main plugin;

    public TreeCap(Main plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
    }

    public String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            if (args.length >= 1) {
                try {
                    TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                }
                catch (NullPointerException e) {
                    plugin.getServer().getConsoleSender().sendMessage(getText("player_not_found").replace("{prefix}", getText("error_prefix").replace("{player}", args[0])));
                }
            }
            if (args.length >= 2) {
                try {
                    Listener.maximum = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ignored) {

                }
            }
            else {
                plugin.getServer().getConsoleSender().sendMessage(getText("missing_argument").replace("{prefix}", getText("error_prefix")).replace("{argument}", "player"));
            }
            return false;
        }

        Player p = (Player) commandSender;
        if (s.equalsIgnoreCase(commandName)) {
            if (args.length == 0) {
                TreeCapitator.giveItem(p);
            }
            else {
                try {
                    TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                }
                catch (NullPointerException e) {
                    p.sendMessage(getText("player_not_found").replace("{prefix}", getText("error_prefix").replace("{player}", args[0])));
                }
                try {
                    if (args.length >= 2) {
                        Listener.maximum = Integer.parseInt(args[1]);
                    }
                }
                catch (NumberFormatException ignored) {

                }
            }
            return true;
        }
        return false;
    }
}
