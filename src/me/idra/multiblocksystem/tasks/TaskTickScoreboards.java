package me.idra.multiblocksystem.tasks;

import org.bukkit.scheduler.BukkitRunnable;



public class TaskTickScoreboards extends BukkitRunnable{
	
	public TaskTickScoreboards() {
		super();
	}
	
	

	@Override
	public void run() {
		ListInformationScoreboards.updateScoreboard();
	}
}
