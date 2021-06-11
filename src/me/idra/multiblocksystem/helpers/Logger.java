package me.idra.multiblocksystem.helpers;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import me.idra.multiblocksystem.managers.ManagerPlugin;

import java.io.File;
import java.time.LocalDateTime; 



public class Logger {
	
	public static int log_ID = 0;
	
	public static HashMap<Integer, String> log_times = new HashMap<Integer, String> ();
	public static HashMap<Integer, String> log_messages = new HashMap<Integer, String> ();

	public static final String OPTION_NOT_FOUND = "option_not_found";
	public static final String OPTION_INVALID = "option_invalid";
	


	private Logger() {
		// Empty constructor
	}
	
	
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
	


	public static void configError(String type, File file, ConfigurationSection section, String option) {
		
		String error_message;

		if (type.equals(OPTION_NOT_FOUND)) {
			error_message = getWarning("config-option-not-found").replace(ConstantPlaceholders.FILE, file.getName());

		} else if (type.equals(OPTION_INVALID)) {
			error_message = getWarning("config-option-invalid").replace(ConstantPlaceholders.FILE, file.getName());
		
		} else {
			return;
		}


		if (section != null) {

			if (option != null) {
				error_message = error_message.replace(ConstantPlaceholders.PATH, section.getCurrentPath() + "." + option);

			} else {
				error_message = error_message.replace(ConstantPlaceholders.PATH, section.getCurrentPath());
			}

		} else {
			error_message = error_message.replace(ConstantPlaceholders.PATH, option);
		}

		log(error_message, true);
	}
}
