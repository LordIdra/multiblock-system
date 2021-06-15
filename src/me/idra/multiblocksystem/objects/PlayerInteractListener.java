package me.idra.multiblocksystem.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.lists.ListInformationScoreBoards;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;



public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		 
		// Fires twice (once for each hand) and we need to only use one event (the main hand one)
		if (event.getHand() == EquipmentSlot.HAND) {

			Block block = event.getClickedBlock();

			if (block.getType() == Material.LECTERN) {

				Location location = event.getClickedBlock().getLocation();
				BaseWorldMultiblock multiblock = ListWorldMultiblocks.getMultiblockFromLocation(location);

				if (multiblock != null) {
					ListInformationScoreBoards.toggleScoreBoard(event.getPlayer(), multiblock);
				}
			}
		}
	}
}