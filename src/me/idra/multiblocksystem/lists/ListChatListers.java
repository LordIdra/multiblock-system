package me.idra.multiblocksystem.lists;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import me.idra.multiblocksystem.objects.ChatLister;



public class ListChatListers {

	public static HashMap<CommandSender, ChatLister> chat_listers = new HashMap<CommandSender, ChatLister> ();
}
