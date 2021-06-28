package anon.playground.recipes;

import org.bukkit.inventory.ItemStack;

public class SimpleRandomRecipe extends Recipe {
	private final ItemStack[] outputItems;
	int maxCount;

	public SimpleRandomRecipe(ItemStack[] requiredItems, ItemStack[] usableFuel, int craftingTime, ItemStack[] outputItems) {
		super(requiredItems, usableFuel, craftingTime);
		this.outputItems = outputItems;
		maxCount = this.outputItems.length;
	}

	@Override
	public ItemStack[] outputCraftedItem() {
		int idx = (int) (Math.random() * maxCount);
		return new ItemStack[]{outputItems[Math.min(idx, maxCount)]};
	}
}
