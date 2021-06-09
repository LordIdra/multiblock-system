package me.idra.multiblocksystem.commands;



import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.MessageHandler;



public class CommandAdminLogClear extends BaseCommand{
	

	public CommandAdminLogClear() {
		super();
		
		name = new String[] {"admin", "log", "clear"};
		description = ChatColor.DARK_AQUA + "Clear the warnings generated since last restart.";
		arguments = new String[] {};
		hidden = false;
		console = true;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Reset ID and clear log maps
		Logger.log_ID = 0;
		Logger.log_times = new HashMap<Integer, String> ();
		Logger.log_messages = new HashMap<Integer, String> ();
		
		// Tell the player we've successfully cleared the logs
		MessageHandler.send(sender, 
				MessageHandler.getSuccess("admin-logs-cleared"));
		
		// Successful execution
		return true;
	}
}
