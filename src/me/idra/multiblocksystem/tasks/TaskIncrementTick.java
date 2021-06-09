package me.idra.multiblocksystem.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.idra.multiblocksystem.filehandlers.FileHandlerPermanentVariables;
import me.idra.multiblocksystem.managers.ManagerPlugin;



public class TaskIncrementTick extends BukkitRunnable {
	
	
	
	public TaskIncrementTick() {
		super();
	}
	
	
	
	@Override
	public void run() {
		ManagerPlugin.tick += ManagerPlugin.tick_interval;
		
		if (ManagerPlugin.tick % 100 == 0) // every 5 seconds
			FileHandlerPermanentVariables.setStoredTick(ManagerPlugin.tick);
	}
}
