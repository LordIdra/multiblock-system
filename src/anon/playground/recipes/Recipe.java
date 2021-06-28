package anon.playground.recipes;

import me.idra.multiblocksystem.helpers.Logger;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Recipe {
	private final ItemStack[] requiredItems, usableFuel;
	private final double craftingTime;

	public Recipe(ItemStack[] requiredItems, ItemStack[] usableFuel, int craftingTime) {
		this.requiredItems = requiredItems;
		this.usableFuel = usableFuel;
		this.craftingTime = Math.max(0, craftingTime);
	}

	/**
	 * Checks through each item needed to craft and verifies the Inventory contains enough of the item as well as verifies the presence of Fuel
	 * @param inputInventory Inventory to be used for storing the Input Items
	 * @return True if item can be crafted, false otherwise
	 */
	public boolean canCraft(Inventory inputInventory) {
		for (ItemStack requiredItem : requiredItems) {
			if (!inputInventory.containsAtLeast(requiredItem, requiredItem.getAmount())) {
				return false;
			}
		}
		for (ItemStack fuel : usableFuel) { //TODO: Ponder whether or not it should use a fuel whenever it checks, because realism. As well as it ensuring the item used for fuel can't cause a problem if it's being used as part of the crafting recipe itself
			if (inputInventory.contains(fuel)) {
				break;
			}
		}
		return true;
	}

	/**
	 * Starts a Thread with the stored Crafting Time as Seconds and returns the Crafted Items when the time has passed.
	 * <br>
	 * This should be called when, and only when, the recipe can be crafted (use <b>canCraft(<i>Inventory inputInventory</i>)</b> to check)
	 *
	 * @param inputInventory  Inventory holding the items which should be used for Crafting
	 * @param outputInventory Inventory which the crafted items should be placed into when finished crafting
	 */
	public void craft(Inventory inputInventory, Inventory outputInventory) {
		try {
			inputInventory.removeItem(requiredItems);
			inputInventory.removeItem(availableFuel(inputInventory));
			Thread.sleep((int) (craftingTime * 1000));
			outputInventory.addItem(outputCraftedItem()); // Needs a check if the output chest has enough room
		} catch (InterruptedException e) {
			Logger.log("Crafting has been Interrupted", false);
		}
	}

	/**
	 * Method for getting the individual Recipe's items
	 * @return ArrayList of ItemStacks being all the items crafted through each recipe
	 */
	public abstract ItemStack[] outputCraftedItem();

	/**
	 * Checks through the Stored List of Fuel Types and returns the first possible Fuel which the InputInventory has enough of
	 *
	 * @param inputInventory Inventory containing the Fuel
	 * @return Fuel to be removed from the inputInventory
	 */
	private ItemStack availableFuel(Inventory inputInventory) {
		for (ItemStack fuel : usableFuel) {
			if (inputInventory.containsAtLeast(fuel, fuel.getAmount())) {
				return fuel;
			}
		}
		return null;
	}
}
