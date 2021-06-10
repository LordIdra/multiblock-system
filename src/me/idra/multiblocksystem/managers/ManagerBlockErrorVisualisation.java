package me.idra.multiblocksystem.managers;



import me.idra.multiblocksystem.lists.ListPlayerSettings;
import me.idra.multiblocksystem.objects.BlockError;
import me.idra.multiblocksystem.objects.PlayerSettings;
import me.idra.multiblocksystem.tasks.TaskVisualiseError;



public class ManagerBlockErrorVisualisation {

	private ManagerBlockErrorVisualisation() {
		// Empty constructor
	}


	
	public static void addError(BlockError error) {
		
		// Schedule a new error visualisation task - the task will then manage itself
		PlayerSettings settings = ListPlayerSettings.getPlayerSettings(error.player.getUniqueId());
    	TaskVisualiseError task = new TaskVisualiseError(error, settings.unresolved_error_time);
    	task.runTaskTimer(ManagerPlugin.plugin, 0, ManagerPlugin.config.getInt("Ticks.error-tick-interval"));
	}
}
