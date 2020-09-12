package io.github.jacksonchen666.treecap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static io.github.jacksonchen666.treecap.Listener.amounts;
import static io.github.jacksonchen666.treecap.Listener.chosenBlocks;

public class MapCleanUp extends BukkitRunnable {
    @Override
    public void run() {
        amounts.values().removeIf(value -> value < 1 || value >= 32);
        OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        List<String> playersList = new ArrayList<>();
        for (OfflinePlayer player : players) {
            playersList.add(player.getName());
        }
        chosenBlocks.keySet().removeIf(key -> playersList.contains(key.getName()));
    }
}
