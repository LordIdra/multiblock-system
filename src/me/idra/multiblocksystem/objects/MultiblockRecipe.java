package me.idra.multiblocksystem.objects;



import java.util.List;
import java.util.Map;



public class MultiblockRecipe {
	
	public Map<String, List<MixedItemStack>> inputs = null;
	public Map<String, List<MixedItemStack>> outputs = null;
	public Map<String, Integer> energy = null;
	public int time = 0;
	
	
	
	public MultiblockRecipe(
			Map<String, List<MixedItemStack>> inputs,		// Input  items
			Map<String, List<MixedItemStack>> outputs,	// Output items
			Map<String, Integer> energy,					// Energy
			int time										// Time
	) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.energy = energy;
		this.time = time;
	}
	

	
	public boolean hasEnergy() {
		return energy == null;
	}

	public boolean hasInputs() {
		return inputs == null;
	}
	
	public boolean hasOutputs() {
		return outputs == null;
	}
}
