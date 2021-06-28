package anon.playground;

import anon.playground.recipes.Recipe;
import anon.playground.recipes.SimpleMultiOutputRecipe;
import anon.playground.recipes.SimpleRandomRecipe;
import anon.playground.recipes.SimpleRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestingSoftware {
	public static void main(String[] args) {


		Recipe r1 = new SimpleRecipe(new ItemStack[]{new ItemStack(Material.COBBLESTONE)}, new ItemStack[]{new ItemStack(Material.COAL)}, 10, new ItemStack(Material.SMOOTH_STONE));
		Recipe r2 = new SimpleRandomRecipe(new ItemStack[]{new ItemStack(Material.COBBLESTONE)}, new ItemStack[]{new ItemStack(Material.COAL)}, 10, new ItemStack[]{new ItemStack(Material.SMOOTH_STONE), new ItemStack(Material.STONE)});
		Recipe r3 = new SimpleMultiOutputRecipe(new ItemStack[]{new ItemStack(Material.COBBLESTONE)}, new ItemStack[]{new ItemStack(Material.COAL)}, 10, new ItemStack[]{new ItemStack(Material.SMOOTH_STONE), new ItemStack(Material.STONE)});

		Recipe[] recipes = {r1, r2, r3};

		for (Recipe recipe : recipes) {
			System.out.println("\n> " + recipe);
			for (ItemStack itemStack : recipe.outputCraftedItem()) {
				System.out.println(itemStack.getType());
			}
		}

	}
}
