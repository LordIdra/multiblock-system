package me.idra.multiblocksystem.filehandlers;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.managers.ManagerPlugin;



public class FileHandlerPermanentVariables {

	static final String STORED_TICK = "stored-stick";
	static final String CURRENT_ID = "current-id";
	
	static FileConfiguration config;
	static File file;

	

	private FileHandlerPermanentVariables() {
		// Empty constructor
	}
	
	
	
	public static void loadFile() {
		
		// Load the files
		file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "PermanentVariables.yml");
	
		// Check the PermanentVariables.yml file exists
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// This should never happen - if it does, we have much larger problems to worry about
			}
		}
		
		// Load the config
		config = YamlConfiguration.loadConfiguration(file);
		
		// Check the CurrentID counter exists
		
		if (!config.contains(CURRENT_ID)) {
			
			config.set(CURRENT_ID, 0);
			Logger.configError(Logger.OPTION_NOT_FOUND, file, null, CURRENT_ID);
		}
		
		// Check the StoredTick counter exists
		if (!config.contains(STORED_TICK)) {
			
			config.set(STORED_TICK, 0);
			Logger.configError(Logger.OPTION_NOT_FOUND, file, null, STORED_TICK);
		}
		
		// Save config file
		saveAndReload();
	}
	
	
	
	public static void saveAndReload() {
		
		try {
			config.save(file);
		} catch (IOException e) {
			Logger.log(
					Logger.getWarning("save-fail-permanent-data"),
					true);
		}
		
		file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "PermanentVariables.yml");
		config = YamlConfiguration.loadConfiguration(file);
	}

	
	
	public static int currentID() {
		
		// Return the current multiblock ID, then increment it by 1
		int previous_ID = config.getInt(CURRENT_ID);
		config.set(CURRENT_ID, previous_ID + 1);
		
		saveAndReload();
		
		return previous_ID;
	}
	
	
	
	public static int getStoredTick() {
		
		// Return the stored tick number
		return config.getInt(STORED_TICK);
	}
	
	
	
	public static void setStoredTick(long tick) {
		
		// Set stored tick number
		config.set(STORED_TICK, tick);
		
		saveAndReload();
	}
}
