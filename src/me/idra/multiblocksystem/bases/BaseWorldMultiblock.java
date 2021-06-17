package me.idra.multiblocksystem.bases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.MultiblockFuel;
import me.idra.multiblocksystem.objects.MultiblockRecipe;
import me.idra.multiblocksystem.tasks.TaskTickMultiblock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public abstract class BaseWorldMultiblock {
	
	public final int ID;
	public final UUID owner;
	
	public static final String REQUIRED_TAG_NOT_FOUND = "required-tag-not-found";

	public AbstractMultiblock abstract_multiblock = null;
	public Map<BlockPosition, String> blocks;
	
	public Map<String, List<Inventory>> tags_inventory 		= new HashMap<> ();
	public Map<String, List<Inventory>> tags_fuel 			= new HashMap<> ();
	public Map<String, List<BlockPosition>> tags_energy 	= new HashMap<> ();
	public Map<String, List<BlockPosition>> tags_position 	= new HashMap<> ();
	
	public MultiblockRecipe active_recipe = null;
	public int recipe_ticks_remaining = 0;

	public TaskTickMultiblock tick_task = null;
	public String status = RUNNING;		// is the machine currently processing a recipe?
	public int fuel_ticks = 0;
	
	
	
	protected BaseWorldMultiblock(AbstractMultiblock abstract_multiblock, Map<BlockPosition, String> blocks, UUID owner, int ID) {
		
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

		for (String tag : abstract_multiblock.fuel_tags) {
			tags_fuel.put(tag, inventoriesWithTag(blocks, tag));
		}

		for (String tag : abstract_multiblock.energy_tags) {
			tags_energy.put(tag, positionsWithTag(blocks, tag));
		}

		for (String tag : abstract_multiblock.position_tags.keySet()) {
			tags_position.put(tag, positionsWithTag(blocks, tag));
		}
		
		// Check that all the required tags are set
		// Just throws a warning, given we can't really do much about it at this stage
		// Inventory tags
		for (String tag : abstract_multiblock.inventory_tags) {
			if (tags_inventory.get(tag) == null) {

				Logger.log(
					Logger.getWarning(REQUIRED_TAG_NOT_FOUND)
					.replace(ConstantPlaceholders.TAG, tag)
					.replace(ConstantPlaceholders.MULTIBLOCK, abstract_multiblock.name),
					true);
			}
		}

		// Energy tags
		for (String tag : abstract_multiblock.energy_tags) {
			if (tags_energy.get(tag) == null) {

				Logger.log(
					Logger.getWarning(REQUIRED_TAG_NOT_FOUND)
					.replace(ConstantPlaceholders.TAG, tag)
					.replace(ConstantPlaceholders.MULTIBLOCK, abstract_multiblock.name),
					true);
			}
		}
					
		// Position tags
		for (String tag : abstract_multiblock.position_tags.keySet()) {
				if (tags_position.get(tag) == null) {

					Logger.log(
						Logger.getWarning(REQUIRED_TAG_NOT_FOUND)
						.replace(ConstantPlaceholders.TAG, tag)
						.replace(ConstantPlaceholders.MULTIBLOCK, abstract_multiblock.name),
						true);
			}
		}
	}



	private void processFuels() {

		/* Add new fuel ticks */
		// Check we aren't close to the fuel limit
		// Could be made more efficient by finding the shortest possible
		// time that fuels could add, but that would be overly complex
		if (abstract_multiblock.max_fuel <= (fuel_ticks + 20)) {
			return;
		}

		// For every fuel inventory
		for (List<Inventory> inv_list : tags_fuel.values()) {
			for (Inventory inv : inv_list) {

				// Skip the inventory if it's empty
				if (inv.isEmpty()) {
					continue;
				}

				// For every possible fuel
				for (MultiblockFuel fuel : abstract_multiblock.fuels) {
					
					// Get an array of all the materials that match the fuel's Material
					Map<Integer, ? extends ItemStack> inv_stacks = inv.all(fuel.stack.getType());

					for (Map.Entry<Integer, ? extends ItemStack> entry : inv_stacks.entrySet()) {
						
						ItemStack stack = entry.getValue();

						// Slimefun item check
						if ((SlimefunItem.getByItem(fuel.stack) != null) && (!SlimefunUtils.isItemSimilar(stack, fuel.stack, false))) {
							continue;
						}

						// Number of fuel ticks can we add if we use the whole stack
						int add_fuel = fuel.ticks * stack.getAmount();

						// Add all of the stack as fuel
						if (fuel_ticks + add_fuel < abstract_multiblock.max_fuel) {
							inv.clear(entry.getKey());
							fuel_ticks += add_fuel;
						
						
						// Just add some of the stack as fuel
						} else if (fuel_ticks + fuel.ticks < abstract_multiblock.max_fuel) {
							int add_limit = Math.floorDiv(abstract_multiblock.max_fuel - fuel_ticks, fuel.ticks);
							inv.clear(entry.getKey());
							stack.setAmount(stack.getAmount() - add_limit);
							inv.setItem(entry.getKey(), stack);
							fuel_ticks += add_limit * fuel.ticks;
						}
					}
				}
			}
		}


		/* Set machine status */
		if (fuel_ticks <= 0) {
			status = PAUSED_NO_FUEL;
		} else {
			status = RUNNING;
		}
	}



	private boolean canTickEnergy() {

		for (String tag : active_recipe.energy.keySet()) { 

			int energy = active_recipe.energy.get(tag);

			// We need to remove charge
			if (energy > 0) {

				int available_charge = 0;
				
				// Check each capacitor assigned to the tag, and add the available charge
				for (BlockPosition position : tags_energy.get(tag)) {
					Capacitor capacitor = (Capacitor) BlockStorage.check(position.toLocation());
					available_charge += capacitor.getCharge(position.toLocation());
				}

				// Check there's enough energy overall
				if (available_charge < energy) {
					status = PAUSED_NOT_ENOUGH_ENERGY;
					return false;
				}


			// We need to add charge
			} else if (energy < 0) {

				int available_capacity = 0;
				
				// Check each capacitor assigned to the tag, and add the available capacity
				for (BlockPosition position : tags_energy.get(tag)) {
					Capacitor capacitor = (Capacitor) BlockStorage.check(position.toLocation());
					available_capacity += capacitor.getCapacity() - capacitor.getCharge(position.toLocation());
				}

				// Check there's enough energy overall
				if (available_capacity < energy) {
					status = PAUSED_ENERGY_OUTPUT_FULL;
					return false;
				}
			}
		}

		// If false has not been returned at this point, we should be fine
		return true;
	}



	private boolean tickEnergy() {

		for (String tag : active_recipe.energy.keySet()) { 

			int energy = Math.abs(active_recipe.energy.get(tag));

			// We need to remove charge
			if (energy > 0) {
				
				// Check each capacitor assigned to the tag, and remove charge until we've removed as much as has been specified
				for (BlockPosition position : tags_energy.get(tag)) {
					Capacitor capacitor = (Capacitor) BlockStorage.check(position.toLocation());
					int charge = capacitor.getCharge(position.toLocation());

					// We should remove only some of the capacitor's charge
					if (energy < charge) {
						capacitor.removeCharge(position.toLocation(), energy);
						energy -= energy; // yes, set it to 0
					
					// We should remove all of the capacitor's charge
					} else {
						capacitor.removeCharge(position.toLocation(), charge);
						energy -= charge;
					}

					// Have we removed enough energy yet?
					if (energy <= 0) {
						break;
					}
				}


			// We need to add charge
			} else if (energy < 0) {

				// Check each capacitor assigned to the tag, and add charge until we've added as much as has been specified
				for (BlockPosition position : tags_energy.get(tag)) {
					Capacitor capacitor = (Capacitor) BlockStorage.check(position.toLocation());
					int capacity = capacitor.getCapacity() - capacitor.getCharge(position.toLocation());

					// We should add only some of the capacitor's capacity
					if (energy < capacity) {
						capacitor.addCharge(position.toLocation(), energy);
						energy -= energy;
					
					// We should add all of the capacitor's capacity
					} else {
						capacitor.addCharge(position.toLocation(), capacity);
						energy -= capacity;
					}

					// Have we removed enough energy yet?
					if (energy <= 0) {
						break;
					}
				}
			}
		}

		// If false has not been returned at this point, we should be fine
		return true;
	}



	private boolean stacksExistInTag(Map<ItemStack, Integer> recipe_stacks, String tag) {

		// For each inventory in the tag
		Map<ItemStack, Integer> amount_map = new HashMap<> ();

		for (ItemStack recipe_stack : recipe_stacks.keySet()) {
			amount_map.put(recipe_stack, recipe_stacks.get(recipe_stack));
		}

		for (Inventory inv : tags_inventory.get(tag)) {

			// Check that the target items exist within the inventory
			for (ItemStack recipe_stack : recipe_stacks.keySet()) {

				Map<Integer, ? extends ItemStack> items = inv.all(recipe_stack.getType());

				// If there's no match
				if (items.isEmpty()) {
					continue;
				}

				for (ItemStack inventory_stack : items.values()) {

					// Slimefun item check
					if (SlimefunItem.getByItem(recipe_stack) != null) {

						if (SlimefunItem.getByItem(recipe_stack) == null) {
							continue;
						}
						
						if (SlimefunItem.getByItem(inventory_stack) != SlimefunItem.getByItem(recipe_stack)) {
							continue;
						}
					}

					// If there is a match, subtract the number of items from the relevant RecipeMixedItemStack
					int amount = amount_map.get(recipe_stack);
					amount -= inventory_stack.getAmount();
					amount_map.put(recipe_stack, amount);

					if (amount_map.get(recipe_stack) <= 0) {
						break;
					}
				}

				// Check if the threshold has been met for that item - if so, remove it from the input list
				if (amount_map.get(recipe_stack) <= 0) {
					amount_map.remove(recipe_stack);
				}
			}
		}


		// If any stack's quantity is below target by now, we don't have enough items to start the recipe
		return amount_map.size() <= 0;
	}



	private void takeStacksFromTag(Map<ItemStack, Integer> recipe_stacks, String tag) {

		// Map to store how many items we've removed already
		Map<ItemStack, Integer> amount_map = new HashMap<> ();

		for (ItemStack recipe_stack : recipe_stacks.keySet()) {
			amount_map.put(recipe_stack, recipe_stacks.get(recipe_stack));
		}

		// For each inventory in the tag
		for (Inventory inv : tags_inventory.get(tag)) {

			// For each itemstack we need to remove from this tag
			for (ItemStack recipe_stack : amount_map.keySet()) {

				// Get the items within the inventory that match target materials
				Map<Integer, ? extends ItemStack> items = inv.all(recipe_stack.getType());

				// If there's no match
				if (items.isEmpty()) {
					continue;
				}
				
				for (Map.Entry<Integer, ? extends ItemStack> stack : items.entrySet()) {

					ItemStack inventory_stack = stack.getValue();

					// Slimefun item check
					if (SlimefunItem.getByItem(recipe_stack) != null) {

						if (SlimefunItem.getByItem(recipe_stack) == null) {
							continue;
						}
						
						if (SlimefunItem.getByItem(inventory_stack) != SlimefunItem.getByItem(recipe_stack)) {
							continue;
						}
					}

					// If there is a match, subtract the number of items from the relevant RecipeMixedItemStack
					amount_map.put(recipe_stack, amount_map.get(recipe_stack) - inventory_stack.getAmount());
					inv.clear(stack.getKey());

					// We took away too many items, add back the relevant number
					if (amount_map.get(recipe_stack) < 0) {

						ItemStack stack_to_add = inventory_stack;
						stack_to_add.setAmount(-amount_map.get(recipe_stack));
						inv.setItem(stack.getKey(), stack_to_add);
					}

					// We've taken away enough items
					if (amount_map.get(recipe_stack) <= 0) {
						break;
					}
				}
			}
		}
	}



	private boolean spaceExistsInTag(Map<ItemStack, Integer> recipe_stacks, String tag) {

		// Map to show how much of each item we have stored already
		Map<ItemStack, Integer> space_for_items = new HashMap<> ();

		for (ItemStack stack : recipe_stacks.keySet()) {
			space_for_items.put(stack, 0);
		}


		// Figure out how many items we can fit into each existing itemstack
		for (Inventory inv : tags_inventory.get(tag)) {
			for (ItemStack recipe_stack : recipe_stacks.keySet()) {

				HashMap<Integer, ? extends ItemStack> inventory_stacks = inv.all(recipe_stack.getType());

				for (Map.Entry<Integer, ? extends ItemStack> stack : inventory_stacks.entrySet()) {

					// Get inventory stack from key
					ItemStack inventory_stack = stack.getValue();
					
					// Slimefun item check
					if (SlimefunItem.getByItem(recipe_stack) != null) {

						if (SlimefunItem.getByItem(recipe_stack) == null) {
							continue;
						}
						
						if (SlimefunItem.getByItem(inventory_stack) == SlimefunItem.getByItem(recipe_stack)) {
							continue;
						}
					}

					// Find out how much space is left in the itemstack, add that to the map
					int space_left_in_stack = inventory_stack.getMaxStackSize() - inventory_stack.getAmount();
					space_for_items.put(recipe_stack, space_for_items.get(recipe_stack) + space_left_in_stack);
				}
			}
		}


		// Figure out how many more free stacks we need
		int extra_slots_needed = 0;

		for (ItemStack recipe_stack : recipe_stacks.keySet()) {
			
			int extra_space_needed = recipe_stacks.get(recipe_stack) - space_for_items.get(recipe_stack);

			if (extra_space_needed > 0) {
				extra_slots_needed += 1 + Math.floorDiv(extra_space_needed, recipe_stack.getMaxStackSize());
			}
		}


		// Figure out how many free stacks we currently have
		int current_slots = 0;

		for (Inventory inventory : tags_inventory.get(tag)) {
			for (ItemStack stack : inventory.getContents()) {
				if (stack == null) {
					current_slots++;
				}
			}
		}


		// Figure out if we have enough free stacks
		return extra_slots_needed <= current_slots;
	}

	

	private void addItemsToTag(Map<ItemStack, Integer> stacks, String tag) {

		// Map to show how much of each item we still need to add
		Map<ItemStack, Integer> items_to_add = new HashMap<> ();

		for (ItemStack stack : stacks.keySet()) {
			items_to_add.put(stack, stacks.get(stack));
		}

		// For each inventory
		for (Inventory inventory : tags_inventory.get(tag)) {

			// For each recipe stack
			for (ItemStack recipe_stack : items_to_add.keySet()) {

				// Get the recipe stacks in the inventory
				Map<Integer, ? extends ItemStack> items_in_inventory = inventory.all(recipe_stack.getType());

				// For every recipe stack in the inventory
				for (Map.Entry<Integer, ? extends ItemStack> inventory_slot : items_in_inventory.entrySet()) {
					
					// Get the inventory stack and figure how much space is 
					ItemStack inventory_stack = items_in_inventory.get(inventory_slot.getKey());
					int to_add = items_to_add.get(recipe_stack);
					int space_in_stack = inventory_stack.getMaxStackSize() - inventory_stack.getAmount();

					// Add only some capacity
					if (to_add <= space_in_stack) {

						ItemStack stack = recipe_stack.clone();
						stack.setAmount(to_add);
						inventory.addItem(stack);

						items_to_add.put(recipe_stack, 0);
					}

					// Add all items
					else {

						ItemStack stack = recipe_stack.clone();
						stack.setAmount(space_in_stack);
						inventory.addItem(stack);

						items_to_add.put(recipe_stack, to_add - space_in_stack);
					}

					// Remove the value if it's now below 0
					if (items_to_add.get(recipe_stack) <= 0) {
						items_to_add.remove(recipe_stack);
						break;
					}
				}
			}
		}


		// Add any remaining ItemStacks
		for (Inventory inventory : tags_inventory.get(tag)) {
			for (ItemStack recipe_stack : items_to_add.keySet()) {
				
				if (inventory.firstEmpty() == -1) {
					break;
				}

				int amount_to_add = items_to_add.get(recipe_stack);
				ItemStack item_to_add = recipe_stack.clone();

				int amount_can_add = amount_to_add % item_to_add.getMaxStackSize();

				items_to_add.put(recipe_stack, amount_to_add - amount_can_add);
				
				item_to_add.setAmount(amount_can_add);
				inventory.addItem(item_to_add);
			}
		}
	}



	private void tickRecipeOutputs() {

		for (String stack_key : active_recipe.outputs.keySet()) {

			if (!spaceExistsInTag(active_recipe.outputs.get(stack_key), stack_key)) {
				status = PAUSED_ITEM_OUTPUT_FULL;
				return;
			}

			addItemsToTag(active_recipe.outputs.get(stack_key), stack_key);
		}
	}



	private void startNewRecipe() {
		
		// For each recipe
		for (MultiblockRecipe recipe : abstract_multiblock.recipes) {

			boolean recipe_valid = true;

			// For each input tag
			if (recipe.hasInputs()) {
				for (String tag : recipe.inputs.keySet()) {

					// Items that should be in the tag
					Map<ItemStack, Integer> stacks = recipe.inputs.get(tag);
					recipe_valid = stacksExistInTag(stacks, tag);

					if (!recipe_valid) {
						break;
					}
				}
				
				// Check if we can add this recipe
				if (recipe_valid) {
					active_recipe = new MultiblockRecipe(recipe);
					recipe_ticks_remaining = active_recipe.time;
					break;
				}
			}
		}


		// If we've found a recipe
		if (active_recipe == null) {
			status = PAUSED_NOT_ENOUGH_ITEMS;
		
		} else {

			// Take inputs
			// For each input tag
			for (String tag : active_recipe.inputs.keySet()) {

				// Items that should be in the tag
				Map<ItemStack, Integer> stacks = active_recipe.inputs.get(tag);

				takeStacksFromTag(stacks, tag);
			}

			status = RUNNING;
		}
	}



	public void tickRecipes() {
		
		// Handle any new fuel items
		processFuels();

		// Check that fuel hasn't run out
		if (status.equals(PAUSED_NO_FUEL)) {
			return;
		}

		// Start a new recipe if nothing is active
		if (active_recipe == null) {
			startNewRecipe();
		}

		// If a recipe is active, tick it
		if (active_recipe != null) {
			
			if (recipe_ticks_remaining <= 0) {
				tickRecipeOutputs();
				active_recipe = null;
				return;
			}

			// Tick the active recipe (providing it exists)
			// Handle energy
			if (active_recipe.hasEnergy()) {

				// Check that we are able to handle energy input/output
				if (!canTickEnergy()) {
					return;
				}

				// Handle energy input/output
				tickEnergy();
			}

			// Decrement time
			recipe_ticks_remaining--;
			fuel_ticks--;
		}
	}
	
	
	
	/*
	 * ABSTRACT METHODS
	 */
	
	public abstract void tick();
	
	
	
	/*
	 * STATIC METHODS
	 */

	public static final String RUNNING = ChatColor.GREEN + "RUNNING";
	public static final String PAUSED_ENERGY_OUTPUT_FULL = ChatColor.RED + "Energy output full";
	public static final String PAUSED_NOT_ENOUGH_ENERGY = ChatColor.RED + "No energy";
	public static final String PAUSED_ITEM_OUTPUT_FULL = ChatColor.RED + "Item output full";
	public static final String PAUSED_NOT_ENOUGH_ITEMS = ChatColor.RED + "No inputs";
	public static final String PAUSED_NO_FUEL = ChatColor.RED + "No fuel";
	
	

	public static List<Inventory> inventoriesWithTag(Map<BlockPosition, String> position_map, String tag) {
		
		// Array to store all matches
		List<Inventory> tag_matches = new ArrayList<> ();
		
		// For every block position
		for (BlockPosition position : position_map.keySet()) {
			
			// Get inventory of that block (if possible)
			if (position.getBlock().getType() == Material.CHEST || position.getBlock().getType() == Material.BARREL) {
				
				Inventory inventory = ((Chest) position.getBlock().getState()).getBlockInventory();
			
				// Compare the tag for that section - if it matches, check that the inventory doesn't already exist, then add the inventory to our array
				if (position_map.get(position).equalsIgnoreCase(tag) && (!tag_matches.contains(inventory))) {
					tag_matches.add(inventory);
				}
			}
		}
		
		// Return the block positions that contain the specified tag
		return tag_matches;
	}
	
	
	
	public static List<BlockPosition> positionsWithTag(Map<BlockPosition, String> position_map, String tag) {
		
		// Array to store all matches
		List<BlockPosition> tag_matches = new ArrayList<> ();
		
		// For every block position
		for (BlockPosition position : position_map.keySet()) {
			
			// Compare the tag for that section - if it matches, add the position to our array
			if (position_map.get(position) != null && position_map.get(position).equalsIgnoreCase(tag)) {
				tag_matches.add(position);
			}
		}
		
		// Return the block positions that contain the specified tag
		return tag_matches;
	}
}
