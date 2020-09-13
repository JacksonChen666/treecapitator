package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TreeCap implements CommandExecutor {
    public static final String commandName = "treecap";
    public static final int dangerThreshold = 128;
    private final Main plugin;
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " is " +
            "dangerously high, and could possibly cause server crashes and data loss. Please consider choosing a smaller " +
            "number, as the plugin not intended for large amounts. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY " +
            "DAMAGES DONE BY THE USER IN ANY WAY, SHAPE, OR FORM.";

    public TreeCap(Main plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
    }

    public String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase(commandName)) {
            if (!(commandSender instanceof Player)) {
                if (args.length == 1) {
                    try {
                        Listener.maximum = Integer.parseInt(args[0]);
                        if (Listener.maximum > dangerThreshold) {
                            plugin.getServer().getConsoleSender().sendMessage(warningMessage.replaceAll("§.", ""));
                            plugin.getServer().getConsoleSender().sendMessage("Set max logs to cut to " + Listener.maximum);
                        }
                        return true;
                    }
                    catch (NumberFormatException e) {
                        try {
                            TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                            return true;
                        }
                        catch (NullPointerException e1) {
                            plugin.getServer().getConsoleSender().sendMessage(getText("player_not_found").replace("{prefix}", getText("error_prefix").replace("{player}", args[0])));
                        }
                    }
                }
                else {
                    plugin.getServer().getConsoleSender().sendMessage(getText("missing_argument").replace("{prefix}", getText("error_prefix")).replace("{argument}", "player"));
                }
                return false;
            }

            Player p = (Player) commandSender;
            if (args.length == 0) {
                TreeCapitator.giveItem(p);
            }
            else {
                try {
                    Listener.maximum = Integer.parseInt(args[0]);
                    if (Listener.maximum > dangerThreshold) {
                        p.sendMessage(warningMessage);
                    }
                    p.sendMessage("Set max logs to cut to " + Listener.maximum);
                }
                catch (NumberFormatException e) {
                    try {
                        TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                    }
                    catch (NullPointerException e1) {
                        p.sendMessage(getText("player_not_found").replace("{prefix}", getText("error_prefix").replace("{player}", args[0])));
                    }
                }
            }
            return true;
        }
        return false;
    }
}
