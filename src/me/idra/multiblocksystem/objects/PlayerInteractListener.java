package me.idra.multiblocksystem.objects;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;


public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		 
		 Location location = new Location(
				 	event.getPlayer().getWorld(),
					event.getClickedPosition().getBlockX(), 
					event.getClickedPosition().getBlockY(),
					event.getClickedPosition().getBlockZ());
		 
		 BaseWorldMultiblock baseWorldMultiblock = ListWorldMultiblocks.getMultiblockFromLocation(location);
		 
		 
	 }
}