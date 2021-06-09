package me.idra.multiblocksystem.managers;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListCommands;



public class ManagerCommands implements CommandExecutor {



	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		// If no arguments provided, just display the help message
		if (args.length == 0) {
			
			if (!sender.hasPermission("multiblocksystem.command.help")) {
				MessageHandler.send(sender, 
						MessageHandler.getError("no-permission-command"));
				return true;
			}
			
			ListCommands.command_object_array[0].commandFunction(sender, command, label, args);
			return true;
		}
		
		// If arguments have been provided
		if (args.length > 0) {
			
			// For every command
			for (BaseCommand command_object : ListCommands.command_object_array) {
				
				// If the command length is lower than the length of the command inputted
				if (args.length >= command_object.name.length) {
					
					boolean command_matches = true;
					
					// Check that each argument matches
					for (int i = 0; i < command_object.name.length; i++)
						if (!args[i].equals(command_object.name[i]))
							command_matches = false;
					
					// If any argument doesn't match, continue to next command
					if (!command_matches)
						continue;
					
					// Generate the permission we need to check
					String command_permission = "multiblocksystem.command.";
					
					for (int i = 0; i < command_object.name.length; i++) {
						command_permission += command_object.name[i];
						if (i != command_object.name.length - 1)
							command_permission += ".";
					}
					
					// Check the sender has appropriate permissions
					if (!sender.hasPermission(command_permission)) {
						MessageHandler.send(sender, 
								MessageHandler.getError("no-permission-command"));
						return true;
					}
					
					// If the sender is the console, check that the command can be run by console
					if (sender instanceof ConsoleCommandSender && (!command_object.console)) {
						MessageHandler.send(sender, 
								MessageHandler.getError("command-cannot-be-run-from-console"));
						return true;
					}
					
					// If sufficient arguments after the command name are provided, attempt to execute command
					if (args.length - command_object.name.length >= command_object.arguments.length) {
						
						// Call command
						command_object.commandFunction(sender, command, label, args);
								
						// Command successfully handled, return true before the incorrect number of arguments message is sent
						return true;
					}
							
					// Incorrect number of arguments provided, let's notify the sender
					String name = "";
					String arguments = "";
					String description =  command_object.description;
					
					for (String arg : command_object.name)
						name += arg + " ";
							
					for (String argument : command_object.arguments)
						arguments += "<" + argument + "> ";
					
					// Send all the above together
					MessageHandler.send(sender,
							MessageHandler.getError("not-enough-arguments")
							.replace("%name%", name)
							.replace("%args%", arguments)
							.replace("%description%", description));
							
					// Command successfully handled, return true before the unrecognised command message is sent
					return true;
				}
			}
		}
		
		// Send 'unrecognized command' message since none of the above have been triggered
		MessageHandler.send(sender,
				MessageHandler.getError("command-not-found"));
		
		// Successful execution
		return true;
	}
}
