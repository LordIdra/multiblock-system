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
	
	static FileConfiguration permanent_variable_file;
	static File data_file;

	

	private FileHandlerPermanentVariables() {
		// Empty constructor
	}
	
	
	
	public static void loadFile() {
		
		// Load the files
		data_file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "PermanentVariables.yml");
	
		// Check the PermanentVariables.yml file exists
		if (!data_file.exists()) {
			try {
				data_file.createNewFile();
			} catch (IOException e) {
				// This should never happen - if it does, we have much larger problems to worry about
			}
		}
		
		// Load the config
		permanent_variable_file = YamlConfiguration.loadConfiguration(data_file);
		
		// Check the CurrentID counter exists
		
		if (!permanent_variable_file.contains(CURRENT_ID)) {
			
			permanent_variable_file.set(CURRENT_ID, 0);
			Logger.log(
					Logger.getWarning("current-id-not-found"),
					true);
		}
		
		// Check the StoredTick counter exists
		if (!permanent_variable_file.contains(STORED_TICK)) {
			
			permanent_variable_file.set(STORED_TICK, 0);
			Logger.log(
					Logger.getWarning("stored-tick-not-found"),
					true);
		}
		
		// Save config file
		saveAndReload();
	}
	
	
	
	public static void saveAndReload() {
		
		try {
			permanent_variable_file.save(data_file);
		} catch (IOException e) {
			Logger.log(
					Logger.getWarning("save-fail-permanent-data"),
					true);
		}
		
		data_file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "PermanentVariables.yml");
		permanent_variable_file = YamlConfiguration.loadConfiguration(data_file);
	}

	
	
	public static int currentID() {
		
		// Return the current multiblock ID, then increment it by 1
		int previous_ID = permanent_variable_file.getInt(CURRENT_ID);
		permanent_variable_file.set(CURRENT_ID, previous_ID + 1);
		
		saveAndReload();
		
		return previous_ID;
	}
	
	
	
	public static int getStoredTick() {
		
		// Return the stored tick number
		return permanent_variable_file.getInt(STORED_TICK);
	}
	
	
	
	public static void setStoredTick(long tick) {
		
		// Set stored tick number
		permanent_variable_file.set(STORED_TICK, tick);
		
		saveAndReload();
	}
}
