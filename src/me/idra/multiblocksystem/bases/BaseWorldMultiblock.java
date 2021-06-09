package me.idra.multiblocksystem.bases;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.MixedItemStack;
import me.idra.multiblocksystem.objects.MultiblockFuel;
import me.idra.multiblocksystem.objects.MultiblockRecipe;
import me.idra.multiblocksystem.tasks.TaskTickMultiblock;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public abstract class BaseWorldMultiblock {
	
	public final int ID;
	public final UUID owner;
	
	public AbstractMultiblock abstract_multiblock = null; 
	
	public HashMap<BlockPosition, String[]> blocks;
	public HashMap<BlockPosition, String[]> original_tags = new HashMap<BlockPosition, String[]> ();
	
	public HashMap<String, ArrayList<Inventory>> tags_inventory = new HashMap<String, ArrayList<Inventory>> ();
	public HashMap<String, ArrayList<BlockPosition>> tags_position = new HashMap<String, ArrayList<BlockPosition>> ();
	
	public MultiblockRecipe active_recipe = null;
	public TaskTickMultiblock tick_task = null;
	
	public String machine_status = RUNNING;		// is the machine running the tick() function?
	public String recipe_status = RUNNING;		// is the machine currently processing a recipe?

	public int fuel_ticks = 0;
	
	
	
	public BaseWorldMultiblock(AbstractMultiblock abstract_multiblock, HashMap<BlockPosition, String[]> blocks, UUID owner, int ID) {
		
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
	
	
	
	public boolean hasSpaceForItems(ArrayList<MixedItemStack> items) {
		
		// Number of total free slots
		int free_slots = 0;
		
		// For every output inventory
		for (Inventory inv : tags_inventory.get(ITEM_OUT)) {
			
			// For every free slot in the inventory, increment the free slot counter
			for (ItemStack itemstack : inv.getContents())
				if (itemstack == null)
					free_slots++;
		}
		
		// If there are enough free slots, return true - if not, return false
		if (free_slots >= items.size())
			return true;
		else
			return false;
	}
	
	
	
	public boolean hasInputItems(ArrayList<MixedItemStack> items) {
		
		// Number of items that match
		HashMap<MixedItemStack, Integer> items_required = new HashMap<MixedItemStack, Integer> (); 
		HashMap<MixedItemStack, Integer> items_total = new HashMap<MixedItemStack, Integer> (); 
		
		for (MixedItemStack stack : items) {
			items_total.put(stack, 0);
			items_required.put(stack, stack.asItemStack().getAmount());
		}
		
		// For every output inventory
		for (Inventory inv : tags_inventory.get(ITEM_IN)) {
			
			// For every item in the inventory
			for (ItemStack actual_item : inv.getContents()) {
				
				// For every input item
				for (MixedItemStack target_item : items) {
					
					// If the slot is null, just continue
					if (actual_item == null)
						continue;
					
					// If the items match, add the amount to the relevant item in the hashmap
					if (target_item.compareType(actual_item))
						items_total.put(target_item, items_total.get(target_item) + actual_item.getAmount());
				}
			}
		}
		
		// If an item hasn't been accounted for, return false
		for (MixedItemStack stack : items_total.keySet())
			if (items_total.get(stack) < items_required.get(stack))
				return false;
		
		// Null has not been returned, so all items are accounted for - return true
		return true;
	}
	
	
	
	public BlockPosition hasSpaceForEnergy(int energy) {
		
		// For every energy output
		for (BlockPosition position : tags_position.get(ENERGY_OUT)) {
			
			// Get capacitor at the location
			Capacitor capacitor = (Capacitor) BlockStorage.check(position.toLocation());
			
			// Check if the capacitor has enough space to add charge
			if ((capacitor.getCapacity() - capacitor.getCharge(position.toLocation())) >= energy)
				return position;
		}
		
		// If true hasn't been returned yet, no capacitor has space
		return null;
	}
	
	
	
	public BlockPosition hasInputEnergy(int energy) {
		
		// For every energy input
		for (BlockPosition position : tags_position.get(ENERGY_IN)) {
			
			// Get capacitor at the location
			Capacitor capacitor = (Capacitor) BlockStorage.check(position.toLocation());
			
			// Check if the capacitor has enough charge
			if (capacitor.getCharge(position.toLocation()) >= energy)
				return position;
		}
		
		// If true hasn't been returned yet, no capacitor has enough energy
		return null;
	}



	public boolean canTickFuelAndEnergy() {

		// Fuel ticks - check
		if (abstract_multiblock.fuels.size() != 0) {
			if (fuel_ticks <= 0) {
				recipe_status = PAUSED_NO_FUEL;
				return false;
			}
		}

		// Input energy - check
		if (active_recipe.hasInputEnergy()) {
			if (hasInputEnergy(active_recipe.in_energy) == null) {
				recipe_status = PAUSED_NOT_ENOUGH_ENERGY;
				return false;
			}
		}
		
		// Output energy - check
		if (active_recipe.hasOutputEnergy()) {
			if (hasSpaceForEnergy(active_recipe.out_energy) == null) {
				recipe_status = PAUSED_ENERGY_OUTPUT_FULL;
				return false;
			}
		}

		return true;
	}
	
	
	
	public void handleRecipes() {
		
		// If the machine uses fuels
		if (abstract_multiblock.fuels.size() != 0) {
			
			// For every fuel input inventory
			for (Inventory inv : tags_inventory.get(FUEL_IN)) {
				
				// For each itemstack in the inventory
				for (ItemStack stack : inv) {
					
					// For every fuel type
					for (MultiblockFuel fuel : abstract_multiblock.fuels) {
						
						// Normal item
						if (fuel.fuel_stack.compareType(stack))
							fuel_ticks += fuel.ticks * stack.getAmount();
							inv.remove(stack);
					}		
				}
			}
			
		}
		
		
		// If a recipe is active
		if (
			active_recipe != null
		    && active_recipe.time <= 0
			&& active_recipe.hasOutputStack()
			&& hasSpaceForItems(active_recipe.out_stack)
		) {
						
			// Add said items to an array
			ArrayList<MixedItemStack> items_to_add = new ArrayList<MixedItemStack> ();
			
			for (MixedItemStack stack : active_recipe.out_stack)
				items_to_add.add(stack);
				
			// Add each item in the array to an inventory
			for (Inventory inv : tags_inventory.get(ITEM_OUT)) {

				while (inv.firstEmpty() != -1) {
					
					// If there are no more items to add, we're finished
					if (items_to_add.size() <= 0) {
						active_recipe = null;
						break;
					}
					
					// Add the item to inventory
					MixedItemStack stack = items_to_add.get(0);
					
					inv.addItem(stack.asItemStack());
					items_to_add.remove(0);
				}
				
				if (items_to_add.size() <= 0)
					break;
			}
				
		
		// If active recipe is not finished
		} else if (
			active_recipe != null
			&& active_recipe.time <= 0
		) {
				
			// If we are able to handle active recipe's energy
			if (!canTickFuelAndEnergy())
				return;

			// Input energy - remove
			if (active_recipe.hasInputEnergy()) {
				BlockPosition input_position = hasSpaceForEnergy(active_recipe.out_energy);
				Capacitor capacitor = (Capacitor) BlockStorage.check(input_position.toLocation());
				capacitor.removeCharge(input_position.toLocation(), active_recipe.in_energy);
			}
				
			// Output energy - add
			if (active_recipe.hasOutputEnergy()) {
				BlockPosition output_position = hasSpaceForEnergy(active_recipe.out_energy);
				Capacitor capacitor = (Capacitor) BlockStorage.check(output_position.toLocation());
				capacitor.addCharge(output_position.toLocation(), active_recipe.out_energy);
			}
				
			// Decrement time
			active_recipe.time--;
			fuel_ticks--;
			
			// Don't try to find a new recipe
			return;
		}
		
		
		
		// Try to find a new recipe
		// For every potential recipe
		for (MultiblockRecipe recipe : abstract_multiblock.recipes) {
			
			// Number of items that have been accounted for, for each itemstack
			HashMap<MixedItemStack, Integer> item_numbers = new HashMap<MixedItemStack, Integer> ();
			
			for (MixedItemStack stack : recipe.in_stack)
				item_numbers.put(stack, 0);
			
			// Does the recipe use item inputs?
			if (!recipe.hasInputStack())
				continue;
				
			// Do we have enough inputs to start the recipe?
			if (!hasInputItems(recipe.in_stack))
				continue;
				
			// For each inventory itemstack
			for (Inventory inv : tags_inventory.get(ITEM_IN)) {
				for (int i = 0; i < inv.getSize(); i++) {
					
					// Get the stack we're dealing with
					ItemStack inv_stack = inv.getItem(i);
					
					// If the stack is empty, continue
					if (inv_stack == null)
						continue;
					
					// How many items in the inventory stack?
					int inventory_item_quantity = inv_stack.getAmount();
					
					// For each remaining recipe itemstack
					for (MixedItemStack recipe_stack : item_numbers.keySet()) {
						
						// Type check
						if (!recipe_stack.compareType(inv_stack))
							continue;
						
						// Find how many items need to be removed
						int recipe_item_quantity = recipe_stack.asItemStack().getAmount();
						
						// Remove the entire stack
						inv.clear(i);
						
						// Account for items removed in our hashmap
						item_numbers.put(recipe_stack, item_numbers.get(recipe_stack) + inventory_item_quantity);

						// We removed more items than we should have; add the required amount back
						if (item_numbers.get(recipe_stack) > recipe_item_quantity) {
								
							int amount_to_add_back = item_numbers.get(recipe_stack) - recipe_item_quantity;
								
							ItemStack stack_to_add_back = inv_stack.clone();
							stack_to_add_back.setAmount(amount_to_add_back);
							inv.addItem(stack_to_add_back);
						}
						
						// Check if enough items have been removed
						if (item_numbers.get(recipe_stack) >= recipe_item_quantity)
							item_numbers.remove(recipe_stack);
					}
				}
			}
			
			// Set active recipe
			active_recipe = recipe.clone();
			recipe_status = RUNNING;
			
			return;
		}
		
		// No recipe was found, set the status to not enough inputs 
		recipe_status = PAUSED_NOT_ENOUGH_ITEMS;
		return;
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
		for (String tag : abstract_multiblock.tags) {
					
			if (Arrays.asList(inventory_tags).contains(tag))
				tags_inventory.put(tag, inventoriesWithTag(blocks, tag));
			else
				tags_position.put(tag, positionsWithTag(blocks, tag));
		}
				
		// Check that all the required tags are set
		// Just throws a warning, given we can't really do much about it at this stage
		for (String tag : abstract_multiblock.tags) {
								
			// Inventory tag
			if (Arrays.asList(inventory_tags).contains(tag)) {
				if (tags_inventory.get(tag) == null)
					Logger.log(
							Logger.getWarning("required-tag-not-found")
							.replace("%tag%", tag)
							.replace("%multiblock%", abstract_multiblock.name),
							true);
					
			// Position tag
			} else {
				if (tags_position.get(tag) == null)
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
	
	public static final String TAG_INVENTORY = "TagInventory";
	public static final String TAG_POSITION =  "TagPosition";
	
	public static final String ITEM_IN = "ItemIn";
	public static final String ITEM_OUT = "ItemOut";
	public static final String FUEL_IN = "FuelIn";
	
	public static final String ENERGY_IN = "EnergyIn";
	public static final String ENERGY_OUT = "EnergyOut";
	
	public static final String[] inventory_tags = new String[] {
			ITEM_IN, ITEM_OUT, FUEL_IN
	};

	public static final String RUNNING = "RUNNING";
	public static final String PAUSED_ENERGY_OUTPUT_FULL = "PAUSED_ENERGY_OUTPUT_FULL";
	public static final String PAUSED_NOT_ENOUGH_ENERGY = "PAUSED_NOT_ENOUGH_ENERGY";
	public static final String PAUSED_ITEM_OUTPUT_FULL = "PAUSED_ITEM_OUTPUT_FULL";
	public static final String PAUSED_NOT_ENOUGH_ITEMS = "PAUSED_NOT_ENOUGH_ITEMS";
	public static final String PAUSED_NO_FUEL = "PAUSED_NO_FUEL";
	
	
	
	public static void instantiateWorldMultiblock(AbstractMultiblock abstract_multiblock, String name, HashMap<BlockPosition, String[]> block_map, UUID player, int ID) {
		
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
			return;
		}
		
		// Initialize the class, checking for a large number of potential problems
		BaseWorldMultiblock world_multiblock = null;
		
		try {
			world_multiblock = 
					(BaseWorldMultiblock) structure_class.getDeclaredConstructor(AbstractMultiblock.class, new HashMap<BlockPosition, String[]> ()
					.getClass(), UUID.class, int.class)
					.newInstance(abstract_multiblock, block_map, player, ID);
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			Logger.log(
					Logger.getWarning("abstract-structure-not-initialized")
					.replace("%structure%", String.valueOf(class_location)),
					true);
			return;
		}
		
		// Return the multiblocks if it's loaded (if it hasn't this will just return null)
		ListWorldMultiblocks.multiblock_objects.put(world_multiblock.ID, world_multiblock);
	}
	
	
	
	public static void instantiateWorldMultiblock(AbstractMultiblock abstract_multiblock, String name, HashMap<BlockPosition, String[]> block_map, UUID player, int ID, int in_fuel_ticks, int recipe_index, int recipe_time) {
		
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
			return;
		}
		
		// Initialize the class, checking for a large number of potential problems
		BaseWorldMultiblock world_multiblock = null;
		
		try {
			world_multiblock = 
					(BaseWorldMultiblock) structure_class.getDeclaredConstructor(AbstractMultiblock.class, new HashMap<BlockPosition, String[]> ()
					.getClass(), UUID.class, int.class)
					.newInstance(abstract_multiblock, block_map, player, ID);
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			Logger.log(
					Logger.getWarning("abstract-structure-not-initialized")
					.replace("%structure%", String.valueOf(class_location)),
					true);
			return;
		}
		
		world_multiblock.fuel_ticks = in_fuel_ticks;
		if (recipe_index != -1)
			world_multiblock.active_recipe = abstract_multiblock.recipes.get(recipe_index);
		
		if (world_multiblock.active_recipe != null)
			world_multiblock.active_recipe.time = recipe_time;
		
		// Return the multiblocks if it's loaded (if it hasn't this will just return null)
		ListWorldMultiblocks.multiblock_objects.put(world_multiblock.ID, world_multiblock);
	}
	
	
	
	public static ArrayList<Inventory> inventoriesWithTag(HashMap<BlockPosition, String[]> position_map, String tag) {
		
		// Array to store all matches
		ArrayList<Inventory> tag_matches = new ArrayList<Inventory> ();
		
		// For every block position
		for (BlockPosition position : position_map.keySet()) {
			
			// Get inventory of that block (if possible)
			if (position.getBlock().getType() == Material.CHEST || position.getBlock().getType() == Material.BARREL) {
				
				Inventory inventory = ((Chest) position.getBlock().getState()).getBlockInventory();
			
				// Compare the tag for that section - if it matches, check that the inventory doesn't already exist, then add the inventory to our array
				if (Arrays.asList(position_map.get(position)).contains(tag))
					if (!tag_matches.contains(inventory))
						tag_matches.add(inventory);
			}
		}
		
		// Return the block positions that contain the specified tag
		return tag_matches;
	}
	
	
	
	public static ArrayList<BlockPosition> positionsWithTag(HashMap<BlockPosition, String[]> position_map, String tag) {
		
		// Array to store all matches
		ArrayList<BlockPosition> tag_matches = new ArrayList<BlockPosition> ();
		
		// For every block position
		for (BlockPosition position : position_map.keySet()) {
			
			// Compare the tag for that section - if it matches, add the position to our array
			if (Arrays.asList(position_map.get(position)).contains(tag))
				tag_matches.add(position);
		}
		
		// Return the block positions that contain the specified tag
		return tag_matches;
	}
}
