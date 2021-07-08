package me.idra.multiblocksystem.commands;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListAbstractMultiblocks;
import me.idra.multiblocksystem.lists.ListChatListers;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.ChatLister;
import net.md_5.bungee.api.chat.ComponentBuilder;



public class CommandListMultiblocks extends BaseCommand{
	

	public CommandListMultiblocks() {
		super();
		
		name = new String[] {"list", "multiblocks"};
		description = ChatColor.DARK_AQUA + "Lists all the multiblocks. Run " + ChatColor.AQUA + "/multiblock info <multiblock>" + ChatColor.DARK_AQUA + "for information about a specific multiblock.";
		arguments = new String[] {};
		hidden = false;
		console = true;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Generate top text
		List<String> top_text = new ArrayList<> ();
		top_text.add(MessageHandler.getInfo("multiblock-list-title"));
		
		// Array of multiblocks names + description
		List<ComponentBuilder> formatted_multiblocks = new ArrayList<> ();
		
		// For every multiblock structure, send the name and description of said structure if the player has the appropriate permission
		for (AbstractMultiblock structure : ListAbstractMultiblocks.structures.values())
			if (sender.hasPermission("multiblocksystem.multiblock." + structure.name_of_structure_block.toLowerCase()))
				formatted_multiblocks.add(new ComponentBuilder("").append(
						ChatColor.translateAlternateColorCodes('&', MessageHandler.getInfo("multiblock-list-format"))
						.replace("%name%", structure.name_of_structure_block)
						.replace("%description%", structure.multiblock_structure_description.replace("\n", ""))));
		
		// Set chatlister
		ListChatListers.chat_listers.put(sender, new ChatLister(
				sender,
				top_text,
				formatted_multiblocks,
				MessageHandler.getError("multiblock-list-none")));
		
		// Display chat lister
		ListChatListers.chat_listers.get(sender).display(1);
		
		// Successful execution
		return true;
	}
}
