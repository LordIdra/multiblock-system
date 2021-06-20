package me.idra.multiblocksystem.objects;

import org.bukkit.inventory.ItemStack;



public class MultiblockFuel {
	
	public ItemStack stack;
	public int ticks;
	
	
	
	public MultiblockFuel(ItemStack stack, int ticks) {
		this.stack = stack;
		this.ticks = ticks;
	}
}
