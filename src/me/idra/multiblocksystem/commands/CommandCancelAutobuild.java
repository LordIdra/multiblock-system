package me.idra.multiblocksystem.commands;



import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListPlayerSettings;



public class CommandCancelAutobuild extends BaseCommand{
	

	public CommandCancelAutobuild() {
		super();
		
		name = new String[] {"cancel", "autobuild"};
		description = ChatColor.DARK_AQUA + "Cancels auto-build mode.";
		arguments = new String[] {};
		hidden = false;
		console = false;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Get UUID of sender
		UUID player = ((Player) sender).getUniqueId();
		
		// Find existing settings and cancel auto-build
		ListPlayerSettings.getPlayerSettings(player).auto_build_running = false;
		
		Logger.log(String.valueOf(ListPlayerSettings.getPlayerSettings(player).auto_build_running), true);
		
		// Success, everyone's happy, yay
		MessageHandler.send(sender,
				MessageHandler.getSuccess("auto-build-cancel"));
		return true;
	}
}
