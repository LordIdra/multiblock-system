package me.idra.multiblocksystem.objects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.idra.multiblocksystem.filehandlers.FileHandlerPlayerData;



public class JoinListener implements Listener {

	 @EventHandler
	 public void onPlayerJoin(PlayerJoinEvent event) {
		 
		 FileHandlerPlayerData.loadSettings(event.getPlayer().getUniqueId());
	 }
}