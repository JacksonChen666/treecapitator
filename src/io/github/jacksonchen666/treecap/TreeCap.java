package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

@org.bukkit.plugin.java.annotation.command.Command(name = "treecap", desc = "Get a Tree Capitator!")
public class TreeCap implements CommandExecutor {
    public static final String commandName = "treecap";
    public static final int dangerThreshold = 1024;
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " " +
            "could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for " +
            "extremely large numbers. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY " +
            "WAY, SHAPE, OR FORM.";
    private static final String[] arg2No0 = new String[] {"maxLogs", "cooldown"};
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
        if (s.equalsIgnoreCase(commandName)) {
            if (!(commandSender instanceof Player)) {
                //                if (args.length == 1) {
                //                    try {
                //                        Listener.maximum = Integer.parseInt(args[0]);
                //                        if (Listener.maximum > dangerThreshold) {
                //                            plugin.getServer().getConsoleSender().sendMessage(warningMessage.replaceAll("§.", ""));
                //                            plugin.getServer().getConsoleSender().sendMessage("Set max logs to cut to " + Listener.maximum);
                //                        }
                //                        return true;
                //                    }
                //                    catch (NumberFormatException e) {
                //                        try {
                //                            TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                //                            return true;
                //                        }
                //                        catch (NullPointerException e1) {
                //                            plugin.getServer().getConsoleSender().sendMessage(getText("player_not_found").replace("{prefix}", getText("error_prefix").replace("{player}", args[0])));
                //                        }
                //                    }
                //                }
                //                else {
                //                    plugin.getServer().getConsoleSender().sendMessage(getText("missing_argument").replace("{prefix}", getText("error_prefix")).replace("{argument}", "player"));
                //                }
                plugin.getServer().getConsoleSender().sendMessage("Console is currently unsupported because I'm too lazy rn.");
                return false;
            }

            Player p = (Player) commandSender;
            if (args.length == 0) {
                TreeCapitator.giveItem(p);
            }
            else if (args.length == 1) {
                try {
                    TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                }
                catch (NullPointerException e1) {
                    p.sendMessage(getText("player_not_found").replace("{prefix}", getText("error_prefix").replace("{player}", args[0])));
                }
            }
            else if (args.length == 2) {
                switch (args[0]) {
                    case "maxLogs":
                        try {
                            BreakingBlocks.maximum = Integer.parseInt(args[1]);
                            if (BreakingBlocks.maximum > dangerThreshold) {
                                p.sendMessage(warningMessage);
                            }
                            p.sendMessage("Set max logs to cut to " + BreakingBlocks.maximum);
                        }
                        catch (NumberFormatException e) {
                            p.sendMessage("Must be a number");
                        }
                    case "cooldown":
                        try {
                            BreakingBlocks.cooldown = Integer.parseInt(args[1]);
                            p.sendMessage("Set cooldown to " + BreakingBlocks.cooldown + " seconds");
                        }
                        catch (NumberFormatException e) {
                            p.sendMessage("Must be a number");
                        }
                }
            }
            return true;
        }
        return false;
    }
}
