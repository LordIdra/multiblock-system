package me.idra.multiblocksystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.ui.UserInterfaceSettings;



public class CommandSettings extends BaseCommand{
	

	public CommandSettings() {
		super();
		
		name = new String[] {"settings"};
		description = ChatColor.DARK_AQUA + "Opens the settings interface.";
		arguments = new String[] {};
		hidden = false;
		console = false;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Open user interface
		new UserInterfaceSettings((Player) sender).display();
		
		return true;
	}
}
