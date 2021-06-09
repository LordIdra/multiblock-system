package me.idra.multiblocksystem.lists;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import me.idra.multiblocksystem.objects.ChatLister;



public class ListChatListers {

	private ListChatListers() {
		// Empty constructor
	}



	public static HashMap<CommandSender, ChatLister> chat_listers = new HashMap<CommandSender, ChatLister> ();
}
