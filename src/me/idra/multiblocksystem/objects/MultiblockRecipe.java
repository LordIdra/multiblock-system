package me.idra.multiblocksystem.objects;

import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class MultiblockRecipe {
	public Map<ItemStack, Integer> inputItems; // Map<Item, Amount>
	public Map<ItemStack, Integer> outputItems; // Map<Item, Amount>
	public int crafting_time; // Time in Seconds

	/**
	 * Constructor for a MultiBlockRecipe inputting
	 *
	 * @param inputItems   Map consisting of Items and respective Amounts
	 * @param outputItems  Map consisting of Items and respective Amounts
	 * @param crafting_time Crafting time in seconds for the recipe
	 */
	public MultiblockRecipe(Map<ItemStack, Integer> inputItems, Map<ItemStack, Integer> outputItems, int crafting_time) {
		this.inputItems = inputItems;
		this.outputItems = outputItems;
		this.crafting_time = crafting_time;
	}

	public MultiblockRecipe(Map<ItemStack, Integer> inputItems, Map<ItemStack, Integer> outputItems) {
		this(inputItems, outputItems, 0);
	}
}
