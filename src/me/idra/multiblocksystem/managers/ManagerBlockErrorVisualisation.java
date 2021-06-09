package me.idra.multiblocksystem.managers;

import me.idra.multiblocksystem.objects.BlockError;
import me.idra.multiblocksystem.tasks.TaskVisualiseError;



public class ManagerBlockErrorVisualisation {

	private ManagerBlockErrorVisualisation() {
		// Empty constructor
	}


	
	public static void addError(BlockError error) {
		
		// Schedule a new error visualisation task - the task will then manage itself
    	TaskVisualiseError task = new TaskVisualiseError(error, ManagerPlugin.config.getInt("ErrorVisualisation.particle-ticks") * ManagerPlugin.config.getInt("ErrorVisualisation.auto-build-multiplier"));
    	task.runTaskTimer(ManagerPlugin.plugin, 0, ManagerPlugin.config.getInt("ErrorVisualisation.particle-interval"));
	}
}
