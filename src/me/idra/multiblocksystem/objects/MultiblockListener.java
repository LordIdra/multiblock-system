package me.idra.multiblocksystem.objects;

import java.util.Arrays;

import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;

import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.filehandlers.FileHandlerPlayerData;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;



public class MultiblockListener implements Listener {



	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		// Is the location part of a multiblock?
		BaseWorldMultiblock multiblock = ListWorldMultiblocks.getMultiblockFromLocation(event.getBlock().getLocation());
		Player player = event.getPlayer();
		
		if (multiblock != null) {
			
			// Does the person who broke the block own the multiblock (or is trusted by the owner)?
			if ((multiblock.owner.equals(player.getUniqueId())) || (Arrays.asList(FileHandlerPlayerData.getTrustedPlayers(multiblock.owner)).contains(player))) {
				
				// If so, disassemble the multiblock
				multiblock.disassemble();
				MessageHandler.send(player,
						MessageHandler.getSuccess("multiblock-disassembled"));
				
			} else {
				
				// NOOOOO!!! YOU CAN'T DO THAT!!!
				event.setCancelled(true);
				MessageHandler.send(player, 
						MessageHandler.getError("cannot-modify-multiblock")
						.replace(ConstantPlaceholders.arg1, player.getName()));
			}
		}
	}



	@EventHandler
	public void onAndroidMine(AndroidMineEvent event) {
		
		// Get multiblock that was/is at the location
		BaseWorldMultiblock multiblock = ListWorldMultiblocks.getMultiblockFromLocation(event.getBlock().getLocation());
		
		// Cancel event
		if (multiblock != null) {
			event.setCancelled(true);
		}
	}



	@EventHandler
	public void onExplosiveToolBreakBlocks(ExplosiveToolBreakBlocksEvent event) {
		
		// For every block broken
		for (Block block : event.getAdditionalBlocks()) {

			// Get multiblock that was/is at the location
			BaseWorldMultiblock multiblock = ListWorldMultiblocks.getMultiblockFromLocation(block.getLocation());
			
			// Cancel event
			if (multiblock != null) {
				event.setCancelled(true);
			}
		}
	}



	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {

		// Get multiblock that was/is at the location
		BaseWorldMultiblock multiblock = ListWorldMultiblocks.getMultiblockFromLocation(event.getBlock().getLocation());
			
		// Cancel event
		if (multiblock != null) {
			event.setCancelled(true);
		}
	}
}
