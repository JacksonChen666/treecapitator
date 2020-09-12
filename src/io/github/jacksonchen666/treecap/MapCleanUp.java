package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.jacksonchen666.treecap.Listener.amounts;
import static io.github.jacksonchen666.treecap.Listener.chosenBlocks;

public class MapCleanUp extends BukkitRunnable {
    @Override
    public void run() {
        amounts.values().removeIf(value -> value < 1 || value >= 32);
        List<String> playersList = Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
        amounts.keySet().removeIf(key -> playersList.contains(key.getName()));
        chosenBlocks.keySet().removeIf(key -> playersList.contains(key.getName()));
    }
}
