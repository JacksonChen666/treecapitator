package io.github.jacksonchen666.treecapitator.commands;

import io.github.jacksonchen666.treecapitator.Main;
import io.github.jacksonchen666.treecapitator.processings.BreakingBlocks;
import io.github.jacksonchen666.treecapitator.utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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

import static io.github.jacksonchen666.treecapitator.processings.BreakingBlocks.getBlocks;

public class TreecapitatorCommand implements CommandExecutor, TabCompleter {
    public static final String COMMAND_NAME = "treecapitator";
    public static final int dangerThreshold = 16384;
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " " +
            "could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for " +
            "extremely large numbers. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY " +
            "WAY, SHAPE, OR FORM.";
    private static final List<String> arg2No0 = Arrays.asList("maxLogs", "cooldown");
    private static Main plugin;

    public TreecapitatorCommand(Main plugin) {
        TreecapitatorCommand.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand(COMMAND_NAME)).setExecutor(this);
    }

    public static String getText(String path) {
        return plugin.getConfig().getString(path);
    }

    public static String getText(String path, String prefix) {
        return Objects.requireNonNull(plugin.getConfig().getString(path)).replace("{prefix}", prefix);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase(COMMAND_NAME)) {
            String prefix = getText("prefix");
            if (!(commandSender instanceof Player)) {
                if (args.length == 0) {
                    plugin.getServer().getConsoleSender().sendMessage(ChatColors.color(getText("messages.missing_argument", prefix).replace("{argument}", "player/setting")));
                    return false;
                }
            }

            if (args.length == 0) {
                if (commandSender.hasPermission("treecapitator.getItem")) {
                    TreecapitatorItem.giveItem((Player) commandSender);
                    commandSender.sendMessage(ChatColors.color(getText("messages.get_axe", prefix)));
                }
                else {
                    commandSender.sendMessage(ChatColors.color(getText("messages.missing_permission", prefix).replace("{permission}", "treecapitator.getItem")));
                    return false;
                }
            }
            else if (args.length == 1) {
                if (commandSender.hasPermission("treecapitator.giveItem")) {
                    try {
                        TreecapitatorItem.giveItem(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
                    }
                    catch (NullPointerException e1) {
                        commandSender.sendMessage(ChatColors.color(getText("messages.player_not_found", prefix).replace("{player}", args[0])));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColors.color(getText("messages.missing_permission", prefix).replace("{permission}", "treecapitator.giveItem")));
                    return false;
                }
            }
            else {
                if (commandSender.hasPermission("treecapitator.settings")) {
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
                    else if (args[0].equalsIgnoreCase("test")) {
                        assert commandSender instanceof Player;
                        Player player = (Player) commandSender;
                        World world = player.getWorld();
                        Block start = world.getBlockAt(0, 150, 0);
                        Material originalMaterial = start.getType();
                        int radius = Integer.parseInt(args[1]);
                        for (Block block : getBlocks(start, radius)) {
                            block.setType(Material.OAK_LOG);
                        }
                        BreakingBlocks.breakBlocks(start, player);
                        TestCheck temp = new TestCheck(player, start, originalMaterial);
                        temp.runTaskLater(plugin, 1L);
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
                else {
                    commandSender.sendMessage(ChatColors.color(getText("messages.missing_permission", prefix).replace("{permission}", "treecapitator.settings")));
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length - 1 == 1) {
            if (args[0].equalsIgnoreCase("maxLogs")) { // 32, 64, 128... 2048
                return IntStream.range(5, 11).mapToObj(i -> String.valueOf((int) Math.pow(2, i))).collect(Collectors.toList());
            }
            else if (args[0].equalsIgnoreCase("cooldown")) { // 0, 2, 4... 10
                return IntStream.iterate(0, i -> i < 10, i -> i + 2).mapToObj(String::valueOf).collect(Collectors.toList());
            }
            else {
                return Collections.emptyList();
            }
        }
        else if (args.length - 1 == 2) {
            return Collections.emptyList();
        }
        List<String> tabComplete = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        tabComplete.addAll(arg2No0);
        return tabComplete;
    }
}
