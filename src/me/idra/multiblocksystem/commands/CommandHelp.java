package me.idra.multiblocksystem.commands;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListChatListers;
import me.idra.multiblocksystem.lists.ListCommands;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.ChatLister;
import net.md_5.bungee.api.chat.ComponentBuilder;



public class CommandHelp extends BaseCommand{
	

	public CommandHelp() {
		super();
		
		name = new String[] {"help"};
		description = ChatColor.DARK_AQUA + "Displays plugin information and a list of commands";
		arguments = new String[] {};
		hidden = false;
		console = true;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Basic information about the plugin
		MessageHandler.send(sender,
				MessageHandler.getInfo("help-title"));
		MessageHandler.send(sender,
				MessageHandler.getInfo("help-version")
				.replace("%version%", ManagerPlugin.VERSION));
		MessageHandler.send(sender,
				MessageHandler.getInfo("help-author")
				.replace("%author%", ManagerPlugin.AUTHOR));
		MessageHandler.send(sender,
				MessageHandler.getInfo("newline"));
		
		
		// Command info top text
		List<String> top_text = new ArrayList<> ();
		top_text.add(MessageHandler.getInfo("help-commands-title"));
		
		// Command list
		List<ComponentBuilder> formatted_commands = new ArrayList<> ();
		
		// For every command
		for (BaseCommand command_object : ListCommands.command_object_array) {
			
			// If it's supposed to be hidden or the player doesn't have permissions, don't display any information about it
			if (command_object.hidden)
				continue;
			if (!sender.hasPermission(command_object.permission))
				continue;
			
			// Get command attributes
			String name = "";
			String arguments = "";
			String description =  command_object.description;
			
			// Format command
			for (String arg : command_object.name)
				name += arg + " ";
					
			for (String argument : command_object.arguments)
				arguments += "<" + argument + "> ";
			
			// Add formatted command to ChatLister
			formatted_commands.add(new ComponentBuilder("").append(
					ChatColor.translateAlternateColorCodes('&', MessageHandler.getInfo("help-commands-format"))
					.replace("%name%", name)
					.replace("%args%", arguments)
					.replace("%description%", description)));
		}
		
		// Set chatlister
		ListChatListers.chat_listers.put(sender, new ChatLister(
				sender,
				top_text,
				formatted_commands,
				MessageHandler.getError("multiblock-list-none")));
		
		// Display chat lister
		ListChatListers.chat_listers.get(sender).display(1);
		
		// Successful execution
		return true;
	}
}
