package me.idra.multiblocksystem.commands;



import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.constants.ConstantPlaceholders;
import me.idra.multiblocksystem.filehandlers.FileHandlerPlayerData;
import me.idra.multiblocksystem.helpers.MessageHandler;



public class CommandTrust extends BaseCommand{
	

	public CommandTrust() {
		super();
		
		name = new String[] {"trust"};
		description = ChatColor.DARK_AQUA + "Adds or removes a player from the list of players who can access your multiblock.";
		arguments = new String[] {"player"};
		hidden = false;
		console = false;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Check that the player actually exists
		Player owner = (Player) sender;
		Player player = Bukkit.getPlayer(args[1]);
		
		if (player == null) {
			MessageHandler.send((Player) sender, 
					MessageHandler.getError("player-not-found")
					.replace(ConstantPlaceholders.PLAYER, args[1]));
			return false;
		}
		
		// If the player already is in the trusted list, remove them
		if (Arrays.asList(FileHandlerPlayerData.getTrustedPlayers(owner.getUniqueId())).contains(player)) {
			
			FileHandlerPlayerData.removeTrusted(owner.getUniqueId(), player.getUniqueId());
			MessageHandler.send((Player) sender, 
					MessageHandler.getSuccess("player-untrusted")
					.replace(ConstantPlaceholders.PLAYER, args[1]));
			
		// If the player is not in the trusted list, add them
		} else {
			
			FileHandlerPlayerData.addTrusted(owner.getUniqueId(), player.getUniqueId());
			MessageHandler.send((Player) sender, 
					MessageHandler.getSuccess("player-trusted")
					.replace(ConstantPlaceholders.PLAYER, args[1]));
		}
		
		// Successful execution
		return true;
	}
}
