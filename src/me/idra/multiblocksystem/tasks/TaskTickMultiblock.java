package me.idra.multiblocksystem.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;



public class TaskTickMultiblock extends BukkitRunnable {

	public BaseWorldMultiblock multiblock;
	
	public TaskTickMultiblock(BaseWorldMultiblock multiblock) {
		super();
		
		this.multiblock = multiblock;
	}
	
	
	
	@Override
	public void run() {
		multiblock.tick();
	}
}
