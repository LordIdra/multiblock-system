package anon.playground.recipes;

import org.bukkit.inventory.ItemStack;

public class SimpleRecipe extends Recipe {
	private ItemStack output;

	public SimpleRecipe(ItemStack[] requiredItems, ItemStack[] usableFuel, int craftingTime, ItemStack output) {
		super(requiredItems, usableFuel, craftingTime);
		this.output = output;
	}


	@Override
	public ItemStack[] outputCraftedItem() {
		return new ItemStack[]{output};
	}
}
