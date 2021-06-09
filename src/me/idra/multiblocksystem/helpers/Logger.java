package me.idra.multiblocksystem.helpers;

import java.util.HashMap;

import org.bukkit.ChatColor;

import me.idra.multiblocksystem.managers.ManagerPlugin;

import java.time.LocalDateTime; 



public class Logger {
	
	public static int log_ID = 0;
	
	public static HashMap<Integer, String> log_times = new HashMap<Integer, String> ();
	public static HashMap<Integer, String> log_messages = new HashMap<Integer, String> ();
	
	
	
	public static String getInfo(String location) {
		return ManagerPlugin.messages.getString("Logger.Info." + location);
	}
	
	public static String getWarning(String location) {
		return ManagerPlugin.messages.getString("Logger.Warning." + location);
	}
	
	
	
	public static void log(String message, boolean warning) {
		ManagerPlugin.plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[MultiblockSystem] " + message));
		
		if (warning) {
			log_ID++;
			log_times.put(log_ID, LocalDateTime.now().toString());
			log_messages.put(log_ID, message);
		}
	}
}
