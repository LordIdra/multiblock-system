package me.idra.multiblocksystem.lists;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public class ListWorldMultiblocks {
	
	public static HashMap<Integer, BaseWorldMultiblock> multiblock_objects = new HashMap<Integer, BaseWorldMultiblock> ();
	
	
	
	public static BaseWorldMultiblock getMultiblockFromLocation(Location loc) {
		
		// Convert location to block position
		BlockPosition block_pos = new BlockPosition(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		// Check if the block position exists in the array - return the multiblock if it does
		for (BaseWorldMultiblock multiblock_object : multiblock_objects.values())
			if (multiblock_object.blocks.containsKey(block_pos))
				return multiblock_object;
		
		// If no multiblock contains the location, return null
		return null;
	}
	
	
	
	public static void addMultiblock(BaseWorldMultiblock multiblock_object) {
		
		// Check the multiblock is not already in the array
		if (multiblock_objects.containsKey(multiblock_object.ID)) {
			
			// We have a pretty big problem
			Logger.log(
					Logger.getWarning("abstract-structure-not-initialized")
					.replace("%id%", String.valueOf(String.valueOf(multiblock_object.ID))),
					true);
			
			if (Bukkit.getPlayer(multiblock_object.owner) != null)
				MessageHandler.send(Bukkit.getPlayer(multiblock_object.owner), 
						MessageHandler.getError("id-invalid"));
		}
		
		// Key = ID; value = BaseMultiblockObject
		multiblock_objects.put(multiblock_object.ID, multiblock_object);
	}
}
