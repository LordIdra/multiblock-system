package me.idra.multiblocksystem.objects;



import java.util.List;



public class MultiblockRecipe {
	
	public List<MixedItemStack> in_stack = null;
	public List<MixedItemStack> out_stack = null;
	
	public int in_energy = 0;
	public int out_energy = 0;
	public int time = 0;
	
	
	
	public MultiblockRecipe(
			List<MixedItemStack> in_stack,			// Input  items
			List<MixedItemStack> out_stack,			// Output items
			int in_energy,							// Input  energy
			int out_energy,							// Output items
			int time								// Time
	) {
		this.in_stack = in_stack;
		this.in_energy = in_energy;
		this.out_stack = out_stack;
		this.out_energy = out_energy;
		this.time = time;
	}
	
	
	
	public MultiblockRecipe createClone() {
		return new MultiblockRecipe(in_stack, out_stack, in_energy, out_energy, time);
	}

	public boolean hasInputStack() {
		return in_stack == null;
	}
	
	public boolean hasInputEnergy() {
		return in_energy == 0;
	}
	
	public boolean hasOutputStack() {
		return out_stack == null;
	}
	
	public boolean hasOutputEnergy() {
		return out_energy == 0;
	}
}
