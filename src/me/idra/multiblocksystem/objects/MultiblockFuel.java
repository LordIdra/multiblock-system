package me.idra.multiblocksystem.objects;



public class MultiblockFuel {
	
	public MixedItemStack fuel_stack = null;
	public int ticks = 0;
	
	
	
	public MultiblockFuel(MixedItemStack fuel_stack, int ticks) {
		this.fuel_stack = fuel_stack;
		this.ticks = ticks;
	}
}
