package me.idra.multiblocksystem.objects;

import java.util.ArrayList;

public class MultiblockRecipe {
	
	public ArrayList<MixedItemStack> in_stack = null;
	public ArrayList<MixedItemStack> out_stack = null;
	
	public int in_energy = 0;
	public int out_energy = 0;
	public int time = 0;
	
	
	
	public MultiblockRecipe(
			ArrayList<MixedItemStack> in_stack,		// Input  items
			ArrayList<MixedItemStack> out_stack,	// Output items
			int in_energy,							// Input  energy
			int out_energy,							// Output items
			int time								// Recipe time
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
		if (in_stack == null)
			return false;
		return true;
	}
	
	public boolean hasInputEnergy() {
		if (in_energy == 0)
			return false;
		return true;
	}
	
	public boolean hasOutputStack() {
		if (out_stack == null)
			return false;
		return true;
	}
	
	public boolean hasOutputEnergy() {
		if (out_energy == 0)
			return false;
		return true;
	}
}
