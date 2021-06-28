package anon.playground.recipes;

import org.bukkit.inventory.ItemStack;

public class SimpleMultiOutputRecipe extends Recipe {
	private ItemStack[] outputItems;

	public SimpleMultiOutputRecipe(ItemStack[] requiredItems, ItemStack[] usableFuel, int craftingTime, ItemStack[] outputItems) {
		super(requiredItems, usableFuel, craftingTime);
		this.outputItems = outputItems;
	}

	@Override
	public ItemStack[] outputCraftedItem() {
		return outputItems;
	}
}
