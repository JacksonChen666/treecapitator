package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@org.bukkit.plugin.java.annotation.command.Command(name = "treecap", desc = "Get a Tree Capitator, give others players, and settings.")
public class TreeCap implements CommandExecutor, TabCompleter {
    public static final String commandName = "treecap";
    public static final int dangerThreshold = 1024;
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " " +
            "could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for " +
            "extremely large numbers. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY " +
            "WAY, SHAPE, OR FORM.";
    private static final List<String> arg2No0 = Arrays.asList("maxLogs", "cooldown");
    private final Main plugin;

    public TreeCap(Main plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
    }

    public String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    public String getText(String path, String prefix) {
        return Objects.requireNonNull(plugin.getConfig().getString(path)).replace("{prefix}", prefix);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase(commandName)) {
            String prefix = getText("prefix");
            if (!(commandSender instanceof Player)) {
                if (args.length == 0) {
                    plugin.getServer().getConsoleSender().sendMessage(ChatColors.color(getText("messages.missing_argument", prefix).replace("{argument}", "player/setting")));
                    return false;
                }
            }

            if (args.length == 0) {
                TreeCapitator.giveItem((Player) commandSender);
                commandSender.sendMessage(ChatColors.color(getText("messages.get_axe", prefix)));
            }
            else if (args.length == 1) {
                try {
                    TreeCapitator.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                }
                catch (NullPointerException e1) {
                    commandSender.sendMessage(ChatColors.color(getText("messages.player_not_found", prefix).replace("{player}", args[0])));
                    return false;
                }
            }
            else {
                if (args[0].equalsIgnoreCase("maxLogs")) {
                    try {
                        BreakingBlocks.maximum = Integer.parseInt(args[1]);
                        if (BreakingBlocks.maximum > dangerThreshold) {
                            commandSender.sendMessage(warningMessage);
                        }
                        plugin.getConfig().set("settings.maxLogs", BreakingBlocks.maximum);
                        plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
                        commandSender.sendMessage(ChatColors.color(getText("messages.set_maxLogs", prefix).replace("{amount}", String.valueOf(BreakingBlocks.maximum))));
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColors.color(getText("messages.number_required", prefix)));
                        return false;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (args[0].equalsIgnoreCase("cooldown")) {
                    try {
                        BreakingBlocks.cooldown = Integer.parseInt(args[1]);
                        plugin.getConfig().set("settings.cooldown", BreakingBlocks.cooldown);
                        plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
                        commandSender.sendMessage(ChatColors.color(getText("messages.set_cooldown", prefix).replace("{amount}", String.valueOf(BreakingBlocks.cooldown))));
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColors.color(getText("messages.number_required", prefix)));
                        return false;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    commandSender.sendMessage("Unknown setting.");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.toString().equalsIgnoreCase(commandName)) {
            if (args.length == 0) {
                List<String> tabComplete = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                tabComplete.addAll(arg2No0);
                return tabComplete;
            }
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("maxLogs".toLowerCase())) {
                    return IntStream.range(0, dangerThreshold).mapToObj(String::valueOf).collect(Collectors.toList());
                }
                else if (args[0].equalsIgnoreCase("cooldown")) {
                    return IntStream.range(0, 10).mapToObj(String::valueOf).collect(Collectors.toList());
                }
            }
            else {
                return Collections.emptyList();
            }
        }
        return null;
    }
}
