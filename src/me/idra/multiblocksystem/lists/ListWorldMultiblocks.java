package me.idra.multiblocksystem.lists;



import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Location;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public class ListWorldMultiblocks {

	private ListWorldMultiblocks() {
		// Empty constructor
	}


	
	public static Map<Integer, BaseWorldMultiblock> multiblock_objects = new HashMap<> ();
	
	
	
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



	private static BaseWorldMultiblock createMultiblockFromName(AbstractMultiblock abstract_multiblock, Map<BlockPosition, String[]> block_map, UUID player, int ID) {
		
		// Initialize the class, checking for a large number of potential problems
		BaseWorldMultiblock world_multiblock = null;
		
		if (abstract_multiblock.structure_class == null) {
			return null;
		}

		try {
			world_multiblock = 
					(BaseWorldMultiblock) abstract_multiblock.structure_class.getDeclaredConstructor(AbstractMultiblock.class,Map.class,
					UUID.class, int.class)
					.newInstance(abstract_multiblock, block_map, player, ID);
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			Logger.log(
					Logger.getWarning("world-multiblock-class-not-initialized")
					.replace("%structure%", String.valueOf(abstract_multiblock.name)),
					true);
			e.printStackTrace();
			return null;
		}

		return world_multiblock;
	}


	
	public static void instantiateWorldMultiblock(AbstractMultiblock abstract_multiblock, Map<BlockPosition, String[]> block_map, UUID player, int ID) {
		
		// Create multiblock
		BaseWorldMultiblock world_multiblock = createMultiblockFromName(abstract_multiblock, block_map, player, ID);

		if (world_multiblock == null) {
			return;
		}

		// Add the multiblock to the multiblock array
		multiblock_objects.put(world_multiblock.ID, world_multiblock);
	}
	
	
	
	public static void instantiateWorldMultiblock(AbstractMultiblock abstract_multiblock, Map<BlockPosition, String[]> block_map, UUID player, int ID, int in_fuel_ticks, int recipe_index, int recipe_time) {
		
		// Create multiblock
		BaseWorldMultiblock world_multiblock = createMultiblockFromName(abstract_multiblock, block_map, player, ID);
		
		if (world_multiblock == null) {
			return;
		}

		world_multiblock.fuel_ticks = in_fuel_ticks;

		if (recipe_index != -1) {
			world_multiblock.active_recipe = abstract_multiblock.recipes.get(recipe_index);
		}
		
		if (world_multiblock.active_recipe != null) {
			world_multiblock.active_recipe.time = recipe_time;
		}
		
		// Add the multiblock to the multiblock array
		multiblock_objects.put(world_multiblock.ID, world_multiblock);
	}
}
