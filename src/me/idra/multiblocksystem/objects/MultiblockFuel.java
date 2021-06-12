package me.idra.multiblocksystem.objects;



public class MultiblockFuel {
	
	public MixedItemStack stack = null;
	public int ticks = 0;
	
	
	
	public MultiblockFuel(MixedItemStack stack, int ticks) {
		this.stack = stack;
		this.ticks = ticks;
	}
}
