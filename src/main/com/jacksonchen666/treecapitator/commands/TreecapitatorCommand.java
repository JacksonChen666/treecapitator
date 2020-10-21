/*
 * Treecapitator. Cuts down trees for you.
 * Copyright (C) 2020  JacksonChen666
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jacksonchen666.treecapitator.commands;

import com.jacksonchen666.treecapitator.Treecapitator;
import com.jacksonchen666.treecapitator.processings.BreakingBlocks;
import com.jacksonchen666.treecapitator.utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreecapitatorCommand implements CommandExecutor, TabCompleter {
    public static final String COMMAND_NAME = "treecapitator";
    public static final int dangerThreshold = 16384;
    static final List<String> arg2No0 = Arrays.asList("maxLogs", "cooldown", "blocksAndItems");
    private static final String warningMessage = "§4§lWARNING! §cChoosing a number above " + dangerThreshold + " " +
            "could cause server crashes and data loss. Consider choosing a smaller number, as it's not intended for " +
            "extremely large numbers. §4§l§oTHE CREATOR IS NOT RESPONSIBLE FOR ANY DAMAGES DONE BY THE USER IN ANY " +
            "WAY, SHAPE, OR FORM.";
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
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
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
                    int number = -1;
                    try {
                        if (!args[0].equalsIgnoreCase("blocksAndItems")) {
                            number = Integer.parseInt(args[1]);
                        }
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
                    else if (args[0].equalsIgnoreCase("blocksAndItems")) {
                        if (args[1].equalsIgnoreCase("list")) {
                            commandSender.sendMessage(BreakingBlocks.getAcceptableItemAndBlock().toString());
                            return true;
                        }
                        boolean add = args[1].equalsIgnoreCase("add");
                        boolean remove = args[1].equalsIgnoreCase("remove");
                        if (add || remove) {
                            if (args.length >= 4 && args[2].equalsIgnoreCase("item")) {
                                Material item = Material.getMaterial(args[3].toUpperCase());
                                if (item == null) {
                                    commandSender.sendMessage(ChatColors.color(getText("messages.unknown_material", prefix).replace("{name}", args[3])));
                                    return false;
                                }
                                if (add) {
                                    BreakingBlocks.putItem(item);
                                    commandSender.sendMessage(ChatColors.color(getText("messages.added_item", prefix).replace("{item}", item.toString())));
                                }
                                else {
                                    BreakingBlocks.removeItem(item);
                                    commandSender.sendMessage(ChatColors.color(getText("messages.removed_item", prefix).replace("{item}", item.toString())));
                                }
                                saveBlocksAndItems();
                            }
                            if (args.length >= 5 && args[2].equalsIgnoreCase("block")) {
                                Material item = Material.getMaterial(args[3].toUpperCase());
                                Material block = Material.getMaterial(args[4].toUpperCase());
                                if (item == null || block == null) {
                                    String replaceTo = null;
                                    if (item == null) {
                                        replaceTo = args[3];
                                    }
                                    if (block == null) {
                                        replaceTo = args[4];
                                    }
                                    commandSender.sendMessage(ChatColors.color(getText("messages.unknown_material", prefix).replace("{name}", replaceTo)));
                                    return false;
                                }
                                if (add) {
                                    BreakingBlocks.putBlock(item, block);
                                    commandSender.sendMessage(ChatColors.color(getText("messages.added_block", prefix).replace("{item}", item.toString()).replace("{block}", block.toString())));
                                }
                                else {
                                    BreakingBlocks.removeBlock(item, block);
                                    commandSender.sendMessage(ChatColors.color(getText("messages.removed_block", prefix).replace("{item}", item.toString()).replace("{block}", block.toString())));
                                }
                                saveBlocksAndItems();
                            }
                        }
                        else {
                            commandSender.sendMessage(ChatColors.color(getText("messages.unknown_setting", prefix)));
                            return false;
                        }
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

    private void saveBlocksAndItems() {
        Map<Material, List<Material>> map = BreakingBlocks.getAcceptableItemAndBlock();
        Map<String, List<String>> result = map.keySet().stream().collect(Collectors.toMap(Enum::toString, key -> map.get(key).stream().map(Enum::toString).collect(Collectors.toList()), (a, b) -> b));
        for (String s : result.keySet()) {
            plugin.getConfig().set("settings.blocksAndItems." + s, result.get(s));
        }
        plugin.getConfig().set("settings.blocksAndItemsList", new ArrayList<>(result.keySet()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length - 1) {
            case 0:
                List<String> tabComplete = new ArrayList<>();
                if (sender.hasPermission("treecapitator.giveItem")) {
                    tabComplete.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
                }
                if (sender.hasPermission("treecapitator.settings")) {
                    tabComplete.addAll(arg2No0);
                }
                return tabComplete;
            case 1:
                if (sender.hasPermission("treecapitator.settings")) {
                    if (args[0].equalsIgnoreCase("maxLogs")) { // 32, 64, 128... 2048
                        return IntStream.range(5, 11).mapToObj(i -> String.valueOf((int) Math.pow(2, i))).collect(Collectors.toList());
                    }
                    else if (args[0].equalsIgnoreCase("cooldown")) { // 0, 2, 4... 10
                        return IntStream.iterate(0, i -> i < 10, i -> i + 2).mapToObj(String::valueOf).collect(Collectors.toList());
                    }
                    else if (args[0].equalsIgnoreCase("blocksAndItems")) {
                        return Arrays.asList("add", "remove", "list"); // /treecapitator _____
                    }
                }
                break;
            case 2:
                if (sender.hasPermission("treecapitator.settings") && args[0].equalsIgnoreCase("blocksAndItems") && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    return Arrays.asList("block", "item");
                }
                break;
            case 3:
                if (sender.hasPermission("treecapitator.settings") && args[0].equalsIgnoreCase("blocksAndItems")) {
                    boolean add = args[1].equalsIgnoreCase("add");
                    boolean remove = args[1].equalsIgnoreCase("remove");
                    boolean block = args[2].equalsIgnoreCase("block");
                    boolean item = args[2].equalsIgnoreCase("item");
                    if (block && (add || remove) || item && remove) {
                        return BreakingBlocks.getAcceptableItemAndBlock().keySet().stream().map(material -> material.toString().toLowerCase()).collect(Collectors.toList());
                    }
                    else if (item && add) {
                        return Arrays.stream(Material.values()).filter(Material::isItem).map(material -> material.toString().toLowerCase()).collect(Collectors.toList()); // /treecapitator add block _____
                    }
                }
                break;
            case 4:
                if (sender.hasPermission("treecapitator.settings") && args[0].equalsIgnoreCase("blocksAndItems")) {
                    boolean add = args[1].equalsIgnoreCase("add");
                    boolean remove = args[1].equalsIgnoreCase("remove");
                    boolean block = args[2].equalsIgnoreCase("block");
                    boolean item = args[2].equalsIgnoreCase("item");
                    if (block) {
                        if (add) {
                            return Arrays.stream(Material.values()).filter(Material::isBlock).map(material -> material.toString().toLowerCase()).collect(Collectors.toList()); // /treecapitator add block _____
                        }
                        else if (remove) {
                            return BreakingBlocks.getAcceptableItemAndBlock().values().stream().flatMap(Collection::stream).map(material -> material.toString().toLowerCase()).collect(Collectors.toList());
                        }
                    }
                }
                break;
        }
        //return Arrays.stream(Material.values()).filter(Material::isBlock).map(material -> material.toString().toLowerCase()).collect(Collectors.toList()); // /treecapitator add block _____
        return Collections.emptyList();
    }
}
