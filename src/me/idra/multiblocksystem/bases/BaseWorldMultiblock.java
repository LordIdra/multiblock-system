package me.idra.multiblocksystem.bases;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.MultiblockRecipe;
import me.idra.multiblocksystem.tasks.TaskTickMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public abstract class BaseWorldMultiblock {
	
	public final int ID;
	public final UUID owner;
	
	public AbstractMultiblock abstract_multiblock = null; 
	
	public Map<BlockPosition, String[]> blocks;
	public Map<BlockPosition, String[]> original_tags = new HashMap<> ();
	
	public Map<String, List<Inventory>> tags_inventory = new HashMap<> ();
	public Map<String, List<BlockPosition>> tags_position = new HashMap<> ();
	
	public MultiblockRecipe active_recipe = null;
	public TaskTickMultiblock tick_task = null;
	
	public String machine_status = RUNNING;		// is the machine running the tick() function?
	public String recipe_status = RUNNING;		// is the machine currently processing a recipe?

	public int fuel_ticks = 0;
	
	
	
	protected BaseWorldMultiblock(AbstractMultiblock abstract_multiblock, Map<BlockPosition, String[]> blocks, UUID owner, int ID) {
		
		// Set default variables
		this.blocks = blocks;
		this.owner = owner;
		this.ID = ID;
		
		// Get data from abstract multiblock
		this.abstract_multiblock = abstract_multiblock;
		
		// Debug
		Logger.log(
				Logger.getInfo("on-assemble")
				.replace("%player%", Bukkit.getOfflinePlayer(owner).getName())
				.replace("%id%", String.valueOf(ID)),
				false);
		
		// Load tags
		loadTags();
	}

	
	
	public void startTick(boolean repeat) {
		
		// Initialize tick task, which will either run just once, or run every X ticks (in accordance with config file)
		if (repeat) {
			tick_task = new TaskTickMultiblock(this);
			tick_task.runTaskTimer(ManagerPlugin.plugin, 0, ManagerPlugin.tick_interval);
		} else {
			tick();
		}
	}
	
	
	
	public void disassemble() {
		
		// Cancel ticking task
		tick_task.cancel();
		
		// Delete the stored multiblock
		FileHandlerWorldMultiblocks.deleteMultiblock(ID);
		
		// The java way of deleting an object: set all references to null and hope for the best
		ListWorldMultiblocks.multiblock_objects.remove(ID);
	}
	
	
	
	public void loadTags() {
		
		// Set required tags
		for (String tag : abstract_multiblock.inventory_tags) {
			tags_inventory.put(tag, inventoriesWithTag(blocks, tag));	
		}

		for (String tag : abstract_multiblock.position_tags) {
			tags_position.put(tag, positionsWithTag(blocks, tag));
		}
				
		// Check that all the required tags are set
		// Just throws a warning, given we can't really do much about it at this stage
		// Inventory tags
		for (String tag : abstract_multiblock.inventory_tags) {
			if (tags_inventory.get(tag) == null) {

				Logger.log(
					Logger.getWarning("required-tag-not-found")
					.replace("%tag%", tag)
					.replace("%multiblock%", abstract_multiblock.name),
					true);
			}
		}
					
		// Position tags
		for (String tag : abstract_multiblock.inventory_tags) {
				if (tags_position.get(tag) == null) {

					Logger.log(
						Logger.getWarning("required-tag-not-found")
						.replace("%tag%", tag)
						.replace("%multiblock%", abstract_multiblock.name),
						true);
			}
		}
	}
	
	
	
	/*
	 * ABSTRACT METHODS
	 */
	
	public abstract void tick();
	
	
	
	/*
	 * STATIC METHODS
	 */

	public static final String RUNNING = "RUNNING";
	public static final String PAUSED_ENERGY_OUTPUT_FULL = "PAUSED_ENERGY_OUTPUT_FULL";
	public static final String PAUSED_NOT_ENOUGH_ENERGY = "PAUSED_NOT_ENOUGH_ENERGY";
	public static final String PAUSED_ITEM_OUTPUT_FULL = "PAUSED_ITEM_OUTPUT_FULL";
	public static final String PAUSED_NOT_ENOUGH_ITEMS = "PAUSED_NOT_ENOUGH_ITEMS";
	public static final String PAUSED_NO_FUEL = "PAUSED_NO_FUEL";
	
	

	public static List<Inventory> inventoriesWithTag(Map<BlockPosition, String[]> position_map, String tag) {
		
		// Array to store all matches
		List<Inventory> tag_matches = new ArrayList<> ();
		
		// For every block position
		for (BlockPosition position : position_map.keySet()) {
			
			// Get inventory of that block (if possible)
			if (position.getBlock().getType() == Material.CHEST || position.getBlock().getType() == Material.BARREL) {
				
				Inventory inventory = ((Chest) position.getBlock().getState()).getBlockInventory();
			
				// Compare the tag for that section - if it matches, check that the inventory doesn't already exist, then add the inventory to our array
				if ((Arrays.asList(position_map.get(position)).contains(tag)) && (!tag_matches.contains(inventory))) {
					tag_matches.add(inventory);
				}
			}
		}
		
		// Return the block positions that contain the specified tag
		return tag_matches;
	}
	
	
	
	public static List<BlockPosition> positionsWithTag(Map<BlockPosition, String[]> position_map, String tag) {
		
		// Array to store all matches
		List<BlockPosition> tag_matches = new ArrayList<> ();
		
		// For every block position
		for (BlockPosition position : position_map.keySet()) {
			
			// Compare the tag for that section - if it matches, add the position to our array
			if (Arrays.asList(position_map.get(position)).contains(tag)) {
				tag_matches.add(position);
			}
		}
		
		// Return the block positions that contain the specified tag
		return tag_matches;
	}



	/*
	 * INSTANTIATION
	 */

	private static BaseWorldMultiblock createMultiblockFromName(AbstractMultiblock abstract_multiblock, String name, Map<BlockPosition, String[]> block_map, UUID player, int ID) {
		
		// Get class location, and from that the class itself
		String class_location = "me.idra.multiblocksystem.multiblocks." + name.toUpperCase();
		Class<?> structure_class = null;
		
		// If the class isn't found, throw a warning
		try {
			structure_class = Class.forName(class_location);
		} catch (ClassNotFoundException e) {
			Logger.log(
					Logger.getWarning("class-not-found")
					.replace("%class%", String.valueOf(class_location)),
					true);
			return null;
		}
		
		// Initialize the class, checking for a large number of potential problems
		BaseWorldMultiblock world_multiblock = null;
		
		try {
			world_multiblock = 
					(BaseWorldMultiblock) structure_class.getDeclaredConstructor(AbstractMultiblock.class,Map.class,
					UUID.class, int.class)
					.newInstance(abstract_multiblock, block_map, player, ID);
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			Logger.log(
					Logger.getWarning("abstract-structure-not-initialized")
					.replace("%structure%", String.valueOf(class_location)),
					true);
			return null;
		}

		return world_multiblock;
	}


	
	public static void instantiateWorldMultiblock(AbstractMultiblock abstract_multiblock, String name, Map<BlockPosition, String[]> block_map, UUID player, int ID) {
		
		// Create multiblock
		BaseWorldMultiblock world_multiblock = createMultiblockFromName(abstract_multiblock, name, block_map, player, ID);

		if (world_multiblock == null) {
			return;
		}

		// Add the multiblock to the multiblock array
		ListWorldMultiblocks.multiblock_objects.put(world_multiblock.ID, world_multiblock);
	}
	
	
	
	public static void instantiateWorldMultiblock(AbstractMultiblock abstract_multiblock, String name, Map<BlockPosition, String[]> block_map, UUID player, int ID, int in_fuel_ticks, int recipe_index, int recipe_time) {
		
		// Create multiblock
		BaseWorldMultiblock world_multiblock = createMultiblockFromName(abstract_multiblock, name, block_map, player, ID);
		
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
		ListWorldMultiblocks.multiblock_objects.put(world_multiblock.ID, world_multiblock);
	}
}
