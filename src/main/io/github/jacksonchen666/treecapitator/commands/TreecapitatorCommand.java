package io.github.jacksonchen666.treecapitator.commands;

import io.github.jacksonchen666.treecapitator.Treecapitator;
import io.github.jacksonchen666.treecapitator.processings.BreakingBlocks;
import io.github.jacksonchen666.treecapitator.utils.ChatColors;
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

public class TreecapitatorCommand implements CommandExecutor, TabCompleter {
    public static final String COMMAND_NAME = "treecapitator";
    public static final int dangerThreshold = 16384;
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " " +
            "could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for " +
            "extremely large numbers. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY " +
            "WAY, SHAPE, OR FORM.";
    private static final List<String> arg2No0 = Arrays.asList("maxLogs", "cooldown");
    private final Treecapitator plugin;

    public TreecapitatorCommand(Treecapitator plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(COMMAND_NAME)).setExecutor(this);
    }

    private String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    private String getText(String path, String prefix) {
        return Objects.requireNonNull(plugin.getConfig().getString(path)).replace("{prefix}", prefix);
    }

    private boolean hasPermission(CommandSender commandSender, String permission) {
        if (!(commandSender instanceof Player) || commandSender.hasPermission(permission)) {
            return true;
        }
        else {
            commandSender.sendMessage(ChatColors.color(getText("messages.missing_permission", getText("prefix")).replace("{permission}", permission)));
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        String prefix = getText("prefix");
        if (!(commandSender instanceof Player) && args.length == 0) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColors.color(getText("messages.missing_argument", prefix).replace("{argument}", "player/setting")));
            return false;
        }

        switch (args.length) {
            case 0:
                if (hasPermission(commandSender, "treecapitator.getItem")) {
                    TreecapitatorItem.giveItem((Player) commandSender);
                    commandSender.sendMessage(ChatColors.color(getText("messages.get_axe", prefix)));
                }
                else {
                    return false;
                }
                break;
            case 1:
                if (hasPermission(commandSender, "treecapitator.giveItem")) {
                    try {
                        Player player = Objects.requireNonNull(Bukkit.getPlayer(args[0]));
                        TreecapitatorItem.giveItem(player);
                        commandSender.sendMessage(ChatColors.color(getText("messages.give_axe", prefix).replace("{player}", args[0])));
                        player.sendMessage(ChatColors.color(getText("messages.other_get", prefix).replace("{player}", commandSender.getName())));
                    }
                    catch (NullPointerException e1) {
                        commandSender.sendMessage(ChatColors.color(getText("messages.player_not_found", prefix).replace("{player}", args[0])));
                        return false;
                    }
                }
                else {
                    return false;
                }
                break;
            default:
                if (hasPermission(commandSender, "treecapitator.settings")) {
                    int number;
                    try {
                        number = Integer.parseInt(args[1]);
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColors.color(getText("messages.number_required", prefix)));
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("maxLogs")) {
                        BreakingBlocks.maxLogs = number;
                        if (BreakingBlocks.maxLogs > dangerThreshold) {
                            commandSender.sendMessage(warningMessage);
                        }
                        plugin.getConfig().set("settings.maxLogs", BreakingBlocks.maxLogs);
                        commandSender.sendMessage(ChatColors.color(getText("messages.set_maxLogs", prefix).replace("{amount}", String.valueOf(BreakingBlocks.maxLogs))));
                    }
                    else if (args[0].equalsIgnoreCase("cooldown")) {
                        BreakingBlocks.cooldown = number;
                        plugin.getConfig().set("settings.cooldown", BreakingBlocks.cooldown);
                        commandSender.sendMessage(ChatColors.color(getText("messages.set_cooldown", prefix).replace("{amount}", String.valueOf(BreakingBlocks.cooldown))));
                    }
                    else {
                        commandSender.sendMessage(ChatColors.color(getText("messages.unknown_setting", prefix)));
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
                else {
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length - 1) {
            case 1:
                if (args[0].equalsIgnoreCase("maxLogs")) { // 32, 64, 128... 2048
                    return IntStream.range(5, 11).mapToObj(i -> String.valueOf((int) Math.pow(2, i))).collect(Collectors.toList());
                }
                else if (args[0].equalsIgnoreCase("cooldown")) { // 0, 2, 4... 10
                    return IntStream.iterate(0, i -> i < 10, i -> i + 2).mapToObj(String::valueOf).collect(Collectors.toList());
                }
                else {
                    return Collections.emptyList();
                }
            case 2:
                return Collections.emptyList();
        }
        List<String> tabComplete = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        tabComplete.addAll(arg2No0);
        return tabComplete;
    }
}
