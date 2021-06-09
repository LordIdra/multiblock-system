package me.idra.multiblocksystem.commands;



import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.lists.ListBlockErrors;
import me.idra.multiblocksystem.managers.ManagerBlockErrorVisualisation;
import me.idra.multiblocksystem.objects.BlockError;



public class CommandShowError extends BaseCommand{
	

	public CommandShowError() {
		super();
		
		name = new String[] {"show", "error"};
		description = ChatColor.DARK_AQUA + "View a specific error from /mb errors. " + ChatColor.AQUA + "/mb assemble <multiblock>" + ChatColor.DARK_AQUA + " must be run first.";
		arguments = new String[] {"index"};
		hidden = true;
		console = false;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Convert first argument (index) to integer
		int index = Integer.parseInt(args[2]);
		
		// Get an array of block errors for the specified player
		List<BlockError> block_error_list = ListBlockErrors.block_errors.get((Player) sender);
		
		// If the player has no errors, return false
		if (block_error_list == null)
			return false;
		if (index >= block_error_list.size())
			return false;
		
		// If the player does have errors, find the one they want to access
		BlockError block_error = block_error_list.get(index);
		
		// If it doesn't exist, return false
		if (block_error == null)
			return false;
		
		// If all the above checks have succeeded, display the error
		ManagerBlockErrorVisualisation.addError(block_error);
		
		// Successful execution
		return true;
	}
}
