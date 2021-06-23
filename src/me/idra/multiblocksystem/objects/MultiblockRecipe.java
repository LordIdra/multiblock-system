package me.idra.multiblocksystem.objects;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class MultiblockRecipe {
	public List<ItemStack> inputItems; // Map<Item, Amount>
	public List<ItemStack> outputItems; // Map<Item, Amount>
	public int energy; // Energy Consumption (If negative, will add energy)
	public int crafting_time; // Time in Seconds

	/**
	 * Constructor for a MultiBlockRecipe inputting
	 *
	 * @param inputItems    Map consisting of Items and respective Amounts
	 * @param outputItems   Map consisting of Items and respective Amounts
	 * @param energy        Energy amount to be used in crafting. If negative it will be creating energy
	 * @param crafting_time Crafting time in seconds for the recipe
	 */
	public MultiblockRecipe(List<ItemStack> inputItems, List<ItemStack> outputItems, int energy, int crafting_time) {
		this.inputItems = inputItems;
		this.outputItems = outputItems;
		this.energy = energy;
		this.crafting_time = crafting_time;
	}

	public boolean canCraft(List<ItemStack> available_items) {
		boolean craftable = false;
		for (ItemStack available_item : available_items) { // For each item available in inv
			for (ItemStack item_stack : inputItems) { // For each item needed in the recipe
				if (SlimefunUtils.isItemSimilar(available_item, item_stack, true, false)) { // If item is part of recipe
					craftable = true;
					if (available_item.getAmount() < item_stack.getAmount()) { // Available_item might not sum the total amount pr Item
						return false;
					}
				}
			}
		}
		return craftable;
	}
}
