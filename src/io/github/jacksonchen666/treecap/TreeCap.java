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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@org.bukkit.plugin.java.annotation.command.Command(name = "treecap", desc = "Get a Tree Capitator, give others players, and settings.")
public class TreeCap implements CommandExecutor, TabCompleter {
    public static final String commandName = "treecap";
    public static final int dangerThreshold = 16384;
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " " +
            "could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for " +
            "extremely large numbers. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY " +
            "WAY, SHAPE, OR FORM.";
    private static final List<String> arg2No0 = Arrays.asList("maxLogs", "cooldown", "blocksPerTick", "searchTimeoutSeconds");
    private static Main plugin;

    public TreeCap(Main plugin) {
        TreeCap.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
    }

    public static String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    public static String getText(String path, String prefix) {
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
                int number;
                try {
                    number = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColors.color(getText("messages.number_required", prefix)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("maxLogs")) {
                    BreakingBlocks.maximum = number;
                    if (BreakingBlocks.maximum > dangerThreshold) {
                        commandSender.sendMessage(warningMessage);
                    }
                    plugin.getConfig().set("settings.maxLogs", BreakingBlocks.maximum);
                    commandSender.sendMessage(ChatColors.color(getText("messages.set_maxLogs", prefix).replace("{amount}", String.valueOf(BreakingBlocks.maximum))));
                }
                else if (args[0].equalsIgnoreCase("cooldown")) {
                    BreakingBlocks.cooldown = number;
                    plugin.getConfig().set("settings.cooldown", BreakingBlocks.cooldown);
                    commandSender.sendMessage(ChatColors.color(getText("messages.set_cooldown", prefix).replace("{amount}", String.valueOf(BreakingBlocks.cooldown))));
                }
                else if (args[0].equalsIgnoreCase("blocksPerTick")) {
                    BreakingBlocks.blocksPerTick = number;
                    plugin.getConfig().set("settings.blocksPerTick", BreakingBlocks.blocksPerTick);
                    commandSender.sendMessage(ChatColors.color(getText("messages.set_blocksPerTick", prefix).replace("{amount}", String.valueOf(BreakingBlocks.blocksPerTick))));
                }
                else if (args[0].equalsIgnoreCase("searchTimeoutSeconds")) {
                    BreakingBlocks.searchTimeoutSeconds = number;
                    plugin.getConfig().set("settings.searchTimeoutSeconds", BreakingBlocks.searchTimeoutSeconds);
                    commandSender.sendMessage(ChatColors.color(getText("messages.set_searchTimeoutSeconds", prefix).replace("{amount}", String.valueOf(BreakingBlocks.searchTimeoutSeconds))));
                }
                else {
                    commandSender.sendMessage("Unknown setting.");
                    return false;
                }
                try {
                    plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
                }
                catch (IOException e) {
                    commandSender.sendMessage(ChatColors.color(getText("messages.save_failed")));
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("maxLogs")) { // 32 -> 2048
                return IntStream.range(5, 11).mapToObj(i -> String.valueOf(Math.pow(2, i))).collect(Collectors.toList());
            }
            else if (args[0].equalsIgnoreCase("cooldown")) { // 0, 2, 4... 10
                return IntStream.iterate(0, i -> i < 10, i -> i + 2).mapToObj(String::valueOf).collect(Collectors.toList());
            }
            else if (args[0].equalsIgnoreCase("blocksPerTick")) { // 64 -> 1024
                return IntStream.range(6, 10).mapToObj(i -> String.valueOf(Math.pow(2, i))).collect(Collectors.toList());
            }
        }
        List<String> tabComplete = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        tabComplete.addAll(arg2No0);
        return tabComplete;
    }
}
