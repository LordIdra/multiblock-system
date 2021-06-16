package me.idra.multiblocksystem.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import me.idra.multiblocksystem.managers.ManagerPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime; 



public class Logger {
	
	private static int log_ID = 0;
	
	private static Map<Integer, String> log_times = new HashMap<> ();
	private static Map<Integer, String> log_messages = new HashMap<> ();

	public static final String OPTION_NOT_FOUND = "option_not_found";
	public static final String OPTION_INVALID = "option_invalid";
	


	private Logger() {
		// Empty constructor
	}


	public static void reset() {
		log_ID 			= 0;
		log_times 		= new HashMap<> ();
		log_messages 	= new HashMap<> ();
	}

	public static String getLogTime(int ID) {
		return log_times.get(ID);
	}

	public static String getLogMessage(int ID) {
		return log_messages.get(ID);
	}

	public static Set<Integer> getIDSet() {
		return log_messages.keySet();
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
			error_message = getWarning("config-option-not-found").replace(ConstantPlaceholders.FILE, file.getPath());

		} else if (type.equals(OPTION_INVALID)) {
			error_message = getWarning("config-option-invalid").replace(ConstantPlaceholders.FILE, file.getPath());
		
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



	public static void fileNotFoundError(File file) {
		Logger.log(getWarning("file-not-found").replace(ConstantPlaceholders.FILE, file.getPath()), true);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
