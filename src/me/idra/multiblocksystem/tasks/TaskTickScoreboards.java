package me.idra.multiblocksystem.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.idra.multiblocksystem.lists.ListInformationScoreBoards;



public class TaskTickScoreboards extends BukkitRunnable{
	
	public TaskTickScoreboards() {
		super();
	}
	
	

	@Override
	public void run() {
		ListInformationScoreBoards.updateScoreboard();
	}
}
