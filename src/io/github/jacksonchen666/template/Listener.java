package io.github.jacksonchen666.template;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onEvent(PlayerLoginEvent event) {
        event.getPlayer().sendMessage(event.getPlayer().getName() + ", welcome to the server!");
    }
}
