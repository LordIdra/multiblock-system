package me.idra.multiblocksystem.lists;



import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.managers.ManagerBlockErrorVisualisation;
import me.idra.multiblocksystem.objects.BlockError;
import me.idra.multiblocksystem.objects.ChatLister;
import me.idra.multiblocksystem.objects.PlayerSettings;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;



public class ListBlockErrors {



	public static Map<Player, List<BlockError>> block_errors = new HashMap<> ();


	private ListBlockErrors() {
		// Empty constructor
	}

	public static void setBlockErrors(Player player, List<BlockError> block_error_array) {
		
		// Add the new array; this will overwrite existing ones automatically
		block_errors.put(player, block_error_array);
	}
	
	public static void display(Player player) {
		
		// Check that a BlockError array actually exists for specified player
		if (block_errors.get(player) == null) {
			MessageHandler.send(player, 
					MessageHandler.getError("multiblock-assembly-not-attempted"));
			return;
		}
		
		// If the player has auto-build enabled, start by displaying the first error
		PlayerSettings settings = ListPlayerSettings.getPlayerSettings(player.getUniqueId());
		
		if (settings.auto_build_enabled) {

			BlockError block_error = ListBlockErrors.block_errors.get(player).get(0);
			ManagerBlockErrorVisualisation.addError(block_error);
			
			return;
		}
		
		// Initialize arrays we'll send to the player
		List<BlockError> errors = block_errors.get(player);
		List<String> top_text = new ArrayList<> ();
		List<ComponentBuilder> error_text = new ArrayList<>();
		
		// Generate top text
		top_text.add(ChatColor.BLUE + "" + ChatColor.BOLD + "=====[" + ChatColor.AQUA + "" + ChatColor.BOLD + "Multiblock Errors" + ChatColor.BLUE + "" + ChatColor.BOLD + "]=====");
		top_text.add("");
		
		// For every block error
		for (BlockError error : errors) {
			
			// Create a new component builder
			ComponentBuilder error_message = new ComponentBuilder();
			
			// Add opening bracket
			TextComponent new_text = new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + "[");
			error_message.append(new_text);
			
			// Add 'view' text and add error view events
			new_text = new TextComponent(ChatColor.RED   + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "View");
			new_text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb show error " + String.valueOf(error_text.size())));
			new_text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Visualise error")));
			error_message.append(new_text);

			// Add closing bracket and reset the events
			new_text = new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + "] ");
			new_text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
			new_text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("")));
			error_message.append(new_text);
			
			// Add the error text itself
			new_text = new TextComponent(error.getErrorMessage());
			error_message.append(new_text);
			
			// Append the whole thing to the array of error messages
			error_text.add(error_message);
		}
		
		// Generate and display a new chat lister
		ListChatListers.chat_listers.put(player, new ChatLister(
				player, 
				top_text,
				error_text,
				"Your multiblock has no errors."
		));
		
		ListChatListers.chat_listers.get(player).display(1);
	}
}
