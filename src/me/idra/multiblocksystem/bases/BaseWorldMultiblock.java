package me.idra.multiblocksystem.bases;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.MultiblockRecipe;
import me.idra.multiblocksystem.tasks.TaskTickMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public abstract class BaseWorldMultiblock {
	public final int ID;
	public final UUID owner;
	public final World world;

	public AbstractMultiblock abstract_multiblock;
	public List<BlockPosition> blocks;
	public List<Inventory> inputs;
	public List<Inventory> outputs;
	public Map<Inventory, List<ItemStack>> out_filter;

	public MultiblockRecipe active_recipe = null;
	public int recipe_ticks_remaining = 0;

	public TaskTickMultiblock tick_task = null;
	public String status = RUNNING;        // is the machine currently processing a recipe?
	public int fuel_ticks = 0;

	public static final String RUNNING = ChatColor.GREEN + "RUNNING";
	public static final String PAUSED_ENERGY_OUTPUT_FULL = ChatColor.RED + "Energy output full";
	public static final String PAUSED_NOT_ENOUGH_ENERGY = ChatColor.RED + "No energy";
	public static final String PAUSED_ITEM_OUTPUT_FULL = ChatColor.RED + "Item output full";
	public static final String PAUSED_NOT_ENOUGH_ITEMS = ChatColor.RED + "No inputs";
	public static final String PAUSED_NO_FUEL = ChatColor.RED + "No fuel";

	/**
	 * Constructor for a Multiblock in the Minecraft World. Defaults all inventories to outputs
	 *
	 * @param abstract_multiblock
	 * @param blocks
	 * @param owner
	 * @param ID
	 */
	protected BaseWorldMultiblock(AbstractMultiblock abstract_multiblock, List<BlockPosition> blocks, UUID owner, int ID) {

		// Set default variables
		this.blocks = blocks;
		this.owner = owner;
		this.ID = ID;

		// Get data from abstract multiblock
		this.abstract_multiblock = abstract_multiblock;
		this.world = this.blocks.get(0).getWorld();

		setInventoriesAsOutputs();

		// Debug
		String owner_name = Bukkit.getOfflinePlayer(owner).getName();

		if (owner_name != null) {
			Logger.log(
					Logger.getInfo("on-assemble")
							.replace("%player%", owner_name)
							.replace("%id%", String.valueOf(ID)),
					false);
		}
	}

	/**
	 * Loops through all blocks in the multiblock and sets all inventories as outputs
	 */
	private void setInventoriesAsOutputs() {
		for (BlockPosition block : blocks) {
			if (block.getBlock().getState() instanceof Container) {
				outputs.add(((Container) block.getBlock().getState()).getInventory());
			}
		}
	}

	public void flipIO(Inventory inv) {
		if (inputs.contains(inv)) {
			outputs.add(inv);
			inputs.remove(inv);
		} else if (outputs.contains(inv)) {
			inputs.add(inv);
			outputs.remove(inv);
		}
	}

	public abstract void tick();

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

	public void tickRecipes() {
		//TODO: Implement a ticking system
		List<ItemStack> storage_items = getFromStorage(); // DONE
		getCraftableRecipe(storage_items); // DONE
		if (status.equals(RUNNING)) {
			List<ItemStack> crafted_items = handleRecipes();
			putItemsToStorage(crafted_items);
		}
	}

	private List<ItemStack> getFromStorage() {
		ArrayList<ItemStack> stored_items = new ArrayList<>();
		for (Inventory input : inputs) {
			stored_items.addAll(Arrays.asList(input.getContents()));
		}
		return stored_items;
	}

	private void getCraftableRecipe(List<ItemStack> available_items) {
		for (MultiblockRecipe recipe : abstract_multiblock.recipes) {
			if (recipe.canCraft(available_items)) {
				active_recipe = recipe;
				status = RUNNING;
				return;
			}
		}
		status = PAUSED_NOT_ENOUGH_ITEMS;
		active_recipe = null;
	}

	private List<ItemStack> handleRecipes() {
		List<ItemStack> items_needed_to_craft = active_recipe.inputItems;
		for (Inventory input_storage : inputs) {
			for (ItemStack item_stack : input_storage) {
				for (ItemStack recipe_item : items_needed_to_craft) {
					if (SlimefunUtils.isItemSimilar(item_stack, recipe_item, true, false)) {
//						input_storage.remove(item_stack);
						item_stack.setAmount(item_stack.getAmount() - recipe_item.getAmount());
//						input_storage.addItem(item_stack);
					}
				}
			}
		}
		return active_recipe.outputItems;
	}

	private void putItemsToStorage(List<ItemStack> items_to_store) {
		Map<Inventory, Inventory> inventories_and_new_inventories = findSuitableInventory(items_to_store);

		if (inventories_and_new_inventories == null) {
			status = PAUSED_ITEM_OUTPUT_FULL;
			return;
		}

		for (Inventory storage_inventory : inventories_and_new_inventories.keySet()) { // for each storage block
			storage_inventory.setContents(inventories_and_new_inventories.get(storage_inventory).getContents());
		}

	}

	private Map<Inventory, Inventory> findSuitableInventory(List<ItemStack> items) { // TODO: Missing Filter checking
		Inventory clonedOutputInventory;
		Map<Inventory, Inventory> acceptedInventories = new HashMap<>();

		for (Inventory output : outputs) {
			// Clone Output Storage to ItemStack Array. This is to be used as a reset for the Inventory
			InventoryHolder holder = output.getHolder();

			if (holder == null) {
				Logger.log(
						Logger.getWarning("inventory-holder-not-found")
						.replace(ConstantPlaceholders.ID, String.valueOf(ID)),
						true);
				return null;
			}

			clonedOutputInventory = holder.getInventory();

			// Attempt to add items to Inventory. If there's not enough space, it will return a HashMap containing the Items and Amounts that would not fit
			// This has to be done due to an inability to create an Inventory
			if (clonedOutputInventory.addItem(items.toArray(ItemStack[]::new)).isEmpty()) { // True if All Items could fit into Inventory
				acceptedInventories.put(output, clonedOutputInventory);
			}
		}
		return acceptedInventories;

	}


}
