package me.idra.multiblocksystem.commands;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.filehandlers.FileHandlerPlayerData;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListChatListers;
import me.idra.multiblocksystem.objects.ChatLister;
import net.md_5.bungee.api.chat.ComponentBuilder;



public class CommandListTrusted extends BaseCommand{
	

	public CommandListTrusted() {
		super();
		
		name = new String[] {"list", "trusted"};
		description = ChatColor.DARK_AQUA + "Lists all the players that you have trusted using" + ChatColor.AQUA + "/trust" + ChatColor.DARK_AQUA;
		arguments = new String[] {};
		hidden = false;
		console = false;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Get a list of the players that the sender has trusted
		Player[] trusted_players = FileHandlerPlayerData.getTrustedPlayers(((Player) sender).getUniqueId());
		
		// Command info top text
		List<String> top_text = new ArrayList<> ();
		top_text.add(MessageHandler.getInfo("trusted-list-title"));
		
		// Player list
		List<ComponentBuilder> formatted_players = new ArrayList<> ();
		
		// For each player
		for (Player player : trusted_players) {
						
			// Generate a base command (eg /mb assemble, /mb help)
			String name = player.getName();
						
			formatted_players.add(new ComponentBuilder("").append(
					ChatColor.translateAlternateColorCodes('&', MessageHandler.getInfo("trusted-list-format"))
					.replace("%name%", name)));
		}
		
		// Set chatlister
		ListChatListers.chat_listers.put((Player) sender, new ChatLister(
				(Player) sender,
				top_text,
				formatted_players,
				MessageHandler.getError("trusted-list-none")));
				
		// Display chat lister
		ListChatListers.chat_listers.get((Player) sender).display(1);
		
		// Successful execution
		return true;
	}
}
