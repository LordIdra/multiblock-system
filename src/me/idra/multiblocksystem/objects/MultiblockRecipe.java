package me.idra.multiblocksystem.objects;



import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;



public class MultiblockRecipe {
	
	public Map<String, List<ItemStack>> inputs = null;
	public Map<String, List<ItemStack>> outputs = null;
	public Map<String, Integer> energy = null;
	public int time = 0;
	
	
	
	public MultiblockRecipe(
			Map<String, List<ItemStack>> inputs,		// Input  items
			Map<String, List<ItemStack>> outputs,	// Output items
			Map<String, Integer> energy,						// Energy
			int time											// Time
	) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.energy = energy;
		this.time = time;
	}



	public MultiblockRecipe(MultiblockRecipe recipe) {
		this.inputs = recipe.inputs;
		this.outputs = recipe.outputs;
		this.energy = recipe.energy;
		this.time = recipe.time;
	}
	

	
	public boolean hasEnergy() {
		return energy != null;
	}

	public boolean hasInputs() {
		return inputs != null;
	}
	
	public boolean hasOutputs() {
		return outputs != null;
	}
}
