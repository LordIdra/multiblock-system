package me.idra.multiblocksystem.bases;

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
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.MultiblockFuel;
import me.idra.multiblocksystem.objects.MultiblockRecipe;
import me.idra.multiblocksystem.objects.RecipeMixedItemStack;
import me.idra.multiblocksystem.tasks.TaskTickMultiblock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public abstract class BaseWorldMultiblock {
	
	public final int ID;
	public final UUID owner;
	
	public AbstractMultiblock abstract_multiblock = null; 
	
	public Map<BlockPosition, String[]> blocks;
	public Map<BlockPosition, String[]> original_tags 		= new HashMap<> ();
	
	public Map<String, List<Inventory>> tags_inventory 		= new HashMap<> ();
	public Map<String, List<Inventory>> tags_fuel 			= new HashMap<> ();
	public Map<String, List<BlockPosition>> tags_energy 	= new HashMap<> ();
	public Map<String, List<BlockPosition>> tags_position 	= new HashMap<> ();
	
	public MultiblockRecipe active_recipe = null;
	public TaskTickMultiblock tick_task = null;

	public String status = RUNNING;		// is the machine currently processing a recipe?

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
					Logger.getWarning("required-tag-not-found")
					.replace("%tag%", tag)
					.replace("%multiblock%", abstract_multiblock.name),
					true);
			}
		}

		// Energy tags
		for (String tag : abstract_multiblock.energy_tags) {
			if (tags_energy.get(tag) == null) {

				Logger.log(
					Logger.getWarning("required-tag-not-found")
					.replace("%tag%", tag)
					.replace("%multiblock%", abstract_multiblock.name),
					true);
			}
		}
					
		// Position tags
		for (String tag : abstract_multiblock.position_tags.keySet()) {
				if (tags_position.get(tag) == null) {

					Logger.log(
						Logger.getWarning("required-tag-not-found")
						.replace("%tag%", tag)
						.replace("%multiblock%", abstract_multiblock.name),
						true);
			}
		}
	}



	private void processFuels() {

		/* Add new fuel ticks */
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
					Map<Integer, ? extends ItemStack> inv_stacks = inv.all(fuel.stack.asItemStack().getType());

					for (int index : inv_stacks.keySet()) {
						
						ItemStack stack = inv_stacks.get(index);

						// Slimefun item check
						if (SlimefunItem.getByItem(stack) != null) {
							if (SlimefunItem.getByItem(stack) != fuel.stack.slimefun_itemstack.getItem()) {
								continue;
							}
						}

						// Add fuel ticks and remove the item
						inv.clear(index);
						fuel_ticks += fuel.ticks * stack.getAmount();
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

		Logger.log(active_recipe.energy.toString(), false);

		for (String tag : active_recipe.energy.keySet()) { 

			int energy = active_recipe.energy.get(tag);

			// We need to remove charge
			if (energy > 0) {

				Logger.log("remove charge check", false);

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

				Logger.log("add charge check", false);

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
					Logger.log(String.valueOf(energy) + " " + String.valueOf(charge), false);
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



	private boolean stacksExistInTag(List<RecipeMixedItemStack> stacks, String tag) {

		// For each inventory in the tag
		Map<RecipeMixedItemStack, Integer> amount_map = new HashMap<> ();

		for (RecipeMixedItemStack stack : stacks) {
			amount_map.put(stack, stack.amount);
		}

		for (Inventory inv : tags_inventory.get(tag)) {

			// Check that the target items exist within the inventory
			for (RecipeMixedItemStack recipe_stack : stacks) {

				Map<Integer, ? extends ItemStack> items = inv.all(recipe_stack.asItemStack().getType());

				// If there's no match
				if (items.isEmpty()) {
					continue;
				}

				for (ItemStack inventory_stack : items.values()) {

					// Slimefun item check
					if (SlimefunItem.getByItem(inventory_stack) != null) {

						if (!recipe_stack.isSlimefunItem()) {
							continue;
						}
						
						if (SlimefunItem.getByItem(inventory_stack) != recipe_stack.slimefun_itemstack.getItem()) {
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



	private void takeStacksFromTag(List<RecipeMixedItemStack> stacks, String tag) {

		// Map to store how many items we've removed already
		Map<RecipeMixedItemStack, Integer> amount_map = new HashMap<> ();

		for (RecipeMixedItemStack stack : stacks) {
			amount_map.put(stack, stack.amount);
		}

		// For each inventory in the tag
		for (Inventory inv : tags_inventory.get(tag)) {

			// For each itemstack we need to remove from this tag
			for (RecipeMixedItemStack recipe_stack : amount_map.keySet()) {

				// Get the items within the inventory that match target materials
				Map<Integer, ? extends ItemStack> items = inv.all(recipe_stack.asItemStack().getType());

				// If there's no match
				if (items.isEmpty()) {
					continue;
				}

				for (int stack_index : items.keySet()) {

					ItemStack inventory_stack = items.get(stack_index);

					// Slimefun item check
					if (SlimefunItem.getByItem(inventory_stack) != null) {

						if (!recipe_stack.isSlimefunItem()) {
							continue;
						}
						
						if (SlimefunItem.getByItem(inventory_stack) != recipe_stack.slimefun_itemstack.getItem()) {
							continue;
						}
					}

					// If there is a match, subtract the number of items from the relevant RecipeMixedItemStack
					amount_map.put(recipe_stack, amount_map.get(recipe_stack) - inventory_stack.getAmount());
					inv.clear(stack_index);

					// We took away too many items, add back the relevant number
					if (amount_map.get(recipe_stack) < 0) {

						ItemStack stack_to_add = inventory_stack;
						stack_to_add.setAmount(-amount_map.get(recipe_stack));
						inv.setItem(stack_index, stack_to_add);
					}

					// We've taken away enough items
					if (amount_map.get(recipe_stack) <= 0) {
						break;
					}
				}
			}
		}
	}



	private void tickRecipeOutputs() {

	}



	private void startNewRecipe() {
		
		// For each recipe
		for (MultiblockRecipe recipe : abstract_multiblock.recipes) {

			boolean recipe_valid = true;

			// For each input tag
			if (recipe.hasInputs()) {
				for (String tag : recipe.inputs.keySet()) {

					// Items that should be in the tag
					List<RecipeMixedItemStack> stacks = recipe.inputs.get(tag);
					recipe_valid = stacksExistInTag(stacks, tag);

					if (!recipe_valid) {
						break;
					}
				}
				
				// Check if we can add this recipe
				if (recipe_valid) {
					active_recipe = new MultiblockRecipe(recipe);
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
				List<RecipeMixedItemStack> stacks = active_recipe.inputs.get(tag);

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

		// Check if the recipe has been finished
		if (active_recipe == null || active_recipe.time <= 0) {
			active_recipe = null;
			tickRecipeOutputs();
			startNewRecipe();
			return;
		}

		// If the above checks haven't triggered, a recipe must still be running
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
		active_recipe.time--;
		fuel_ticks--;
	}
	
	
	
	/*
	 * ABSTRACT METHODS
	 */
	
	public abstract void tick();
	
	
	
	/*
	 * STATIC METHODS
	 */

	public static final String RUNNING = "RUNNING";
	public static final String PAUSED_ENERGY_OUTPUT_FULL = "PAUSED_ENERGY_FULL";
	public static final String PAUSED_NOT_ENOUGH_ENERGY = "PAUSED_NO_ENERGY";
	public static final String PAUSED_ITEM_OUTPUT_FULL = "PAUSED_OUTPUT_FULL";
	public static final String PAUSED_NOT_ENOUGH_ITEMS = "PAUSED_NO_INPUT";
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
}
