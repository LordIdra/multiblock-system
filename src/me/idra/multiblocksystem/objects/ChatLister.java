package me.idra.multiblocksystem.objects;



import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.helpers.ConstantSettingNames;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListPlayerSettings;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;



public class ChatLister {
	
	String[] info_text;
	ComponentBuilder[] list_text;
	String nothing_to_display_error;
	
	CommandSender player;
	
	int max_pages;
	int entries_per_page;
	
	
	
	public ChatLister(CommandSender in_player, List<String> in_info_text, List<ComponentBuilder> in_list_text, String in_nothing_to_display_error) {

		// Handle color codes for info array
		for (int i = 0; i < in_info_text.size(); i++)
			in_info_text.set(i, ChatColor.translateAlternateColorCodes('&', in_info_text.get(i)));
		
		// Convert the ArrayList<String>'s to String[]'s
		info_text = in_info_text.toArray(new String[0]);
		list_text = in_list_text.toArray(new ComponentBuilder[0]);
		
		// Set remaining variables
		nothing_to_display_error = ChatColor.translateAlternateColorCodes('&', in_nothing_to_display_error);
		player = in_player;
		entries_per_page = ListPlayerSettings.getPlayerSettings(((Player) player).getUniqueId()).getContainerValueAsInt(ConstantSettingNames.LIST_ITEMS_PER_PAGE);
		max_pages = 1 + Math.floorDiv(list_text.length - 1, entries_per_page);
	}
	
	
	
	public boolean display(int page) {
				
		// Is the list empty?
		if (list_text.length == 0) {
			MessageHandler.send(player, 
					MessageHandler.getError("nothing-to-display"));
			return false;
		}
		
		// Are there enough arguments to warrant displaying this page?
		if (page > max_pages) {
			MessageHandler.send(player, 
					MessageHandler.getError("invalid-page"));
			return false;
		}
		
		// Display information text
		for (String info : info_text)
			MessageHandler.send(player, info);
		
		// every [page] entries = a new page
		int offset = (page - 1) * entries_per_page;
		
		// If there's only 1 page
		if (max_pages == 1) {
			
			// Display errors from that page
			for (ComponentBuilder componentBuilder : list_text) {
				player.spigot().sendMessage(componentBuilder.create());
			}
			
			// And a newline to separate this from the next message
			MessageHandler.send(player, "");
			
			
		} else {
			
			// Display list text
			for (int i = 0; i < entries_per_page; i++)
				if (offset + i < list_text.length)
					player.spigot().sendMessage(list_text[offset + i].create());
				else
					break;
			
			// Start creating navigation message (arrows at the bottom)
			ComponentBuilder navigate_message = new ComponentBuilder();
			
			// Create left arrow if needed
			if (page > 1) {
				TextComponent arrow_left = new TextComponent("<<< ");
				arrow_left.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb page " + (page - 1)));
				arrow_left.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Previous Page")));
				navigate_message.append(arrow_left);
				navigate_message.color(net.md_5.bungee.api.ChatColor.WHITE);
				navigate_message.bold(true);
			}
			
			// Create 'page x out of y'
			TextComponent new_text = new TextComponent(
					ChatColor.BLUE + "page " + 
					ChatColor.AQUA + page +
					ChatColor.BLUE + " out of " +
					ChatColor.AQUA + max_pages +
					" ");
			
			// Reset click/hover events
			new_text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
			new_text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("")));
			navigate_message.append(new_text);
			
			// Create right arrow if needed
			if (page < max_pages) {
				TextComponent arrow_right = new TextComponent(">>>");
				arrow_right.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mb page " + (page + 1)));
				arrow_right.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Next Page")));
				navigate_message.append(arrow_right);
				navigate_message.color(net.md_5.bungee.api.ChatColor.WHITE);
				navigate_message.bold(true);
			}
			
			// Send the navigation message
			MessageHandler.send(player, "");
			player.spigot().sendMessage(navigate_message.create());
		}
		
		
		// Command successfully executed
		return true;
	}
}
