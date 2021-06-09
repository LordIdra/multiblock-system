package me.idra.multiblocksystem.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.managers.ManagerPlugin;



public class MessageHandler {
			
			
			
	public static String getInfo(String location) {
		return ManagerPlugin.messages.getString("Messages.Info." + location);
	}
	
	public static String getError(String location) {
		return ManagerPlugin.messages.getString("Messages.Error." + location);
	}
	
	public static String getSuccess(String location) {
		return ManagerPlugin.messages.getString("Messages.Success." + location);
	}
	
	
	
	public static void send(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	
	
	public static void send(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
