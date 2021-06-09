package me.idra.multiblocksystem.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListPlayerSettings;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.PlayerSettings;



public class FileHandlerPlayerData {

	static final String SETTINGS = "settings";
	static final String _TRUSTED = ".trusted";
	
	static FileConfiguration player_data_file;
	static File data_file; 
	
	

	private FileHandlerPlayerData() {
		// Empty constructor
	}


	
	public static void loadFile() {
		
		// Load the file
		data_file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "PlayerData.yml");
	
		// Check the PlayerData.yml file exists
		if (!data_file.exists()) {
			try {
				data_file.createNewFile();
			} catch (IOException e) {
				// This should never happen - if it does, we have much bigger problems to worry about
			}
		}
		
		// Load the config
		player_data_file = YamlConfiguration.loadConfiguration(data_file);
	}
	
	
	
	public static void saveAndReload() {
		try {
			player_data_file.save(data_file);
		} catch (IOException e) {
			Logger.log(
					Logger.getWarning("save-fail-player-data"),
					true);
		}
		
		data_file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "PlayerData.yml");
		player_data_file = YamlConfiguration.loadConfiguration(data_file);
	}
	
	
	
	public static void saveSettings(UUID player, PlayerSettings settings) {
		
		// Check the player exists in the file
		ConfigurationSection player_section = player_data_file.getConfigurationSection(player.toString());
		
		if (player_section == null) {
			player_data_file.createSection(player.toString());
			player_section = player_data_file.getConfigurationSection(player.toString());
		}

		ConfigurationSection settings_section = player_section.getConfigurationSection(SETTINGS);
		
		settings_section.set("auto_build_enabled", settings.auto_build_enabled);
		
		settings_section.set("unresolved_error_time", settings.unresolved_error_time);
		settings_section.set("resolved_error_time", settings.resolved_error_time);
		settings_section.set("error_particle_amount", settings.error_particle_amount);
		
		settings_section.set("location_particle_time", settings.location_particle_time);
		settings_section.set("location_particle_amount", settings.location_particle_amount);
		
		settings_section.set("list_items_per_page", settings.list_items_per_page);
		
		settings_section.set("error_offset_x", settings.error_offset_x);
		settings_section.set("error_offset_y", settings.error_offset_y);
		settings_section.set("error_offset_z", settings.error_offset_z);
		
		settings_section.set("resolved_error_r", settings.resolved_error_r);
		settings_section.set("resolved_error_g", settings.resolved_error_g);
		settings_section.set("resolved_error_b", settings.resolved_error_b);
		
		settings_section.set("unresolved_error_r", settings.unresolved_error_r);
		settings_section.set("unresolved_error_g", settings.unresolved_error_g);
		settings_section.set("unresolved_error_b", settings.unresolved_error_b);
		
		settings_section.set("location_r", settings.location_r);
		settings_section.set("location_g", settings.location_g);
		settings_section.set("location_b", settings.location_b);
		
		
		saveAndReload();
	}
	
	
	
	public static void loadSettings(UUID player) {
		
		checkPlayer(player);
		
		PlayerSettings settings = new PlayerSettings();
		
		ConfigurationSection player_section = player_data_file.getConfigurationSection(player.toString());
		ConfigurationSection settings_section = player_section.getConfigurationSection(SETTINGS);
		
		settings.auto_build_enabled = settings_section.getBoolean("auto_build_enabled");
		
		settings.unresolved_error_time = settings_section.getInt("unresolved_error_time");
		settings.resolved_error_time = settings_section.getInt("resolved_error_time");
		settings.error_particle_amount = settings_section.getInt("error_particle_amount");
		
		settings.location_particle_time = settings_section.getInt("location_particle_time");
		settings.location_particle_amount = settings_section.getInt("location_particle_amount");
		
		settings.list_items_per_page = settings_section.getInt("list_items_per_page");
		
		settings.error_offset_x = settings_section.getInt("error_offset_x");
		settings.error_offset_y = settings_section.getInt("error_offset_y");
		settings.error_offset_z = settings_section.getInt("error_offset_z");
		
		settings.resolved_error_r = settings_section.getInt("resolved_error_r");
		settings.resolved_error_g = settings_section.getInt("resolved_error_g");
		settings.resolved_error_b = settings_section.getInt("resolved_error_b");
		
		settings.unresolved_error_r = settings_section.getInt("unresolved_error_r");
		settings.unresolved_error_g = settings_section.getInt("unresolved_error_g");
		settings.unresolved_error_b = settings_section.getInt("unresolved_error_b");
		
		settings.location_r = settings_section.getInt("location_r");
		settings.location_g = settings_section.getInt("location_g");
		settings.location_b = settings_section.getInt("location_b");
		
		ListPlayerSettings.setPlayerSettings(player, settings);
		
		saveAndReload();
	}
	
	
	
	static void checkPlayer(UUID owner) {
		
		// Check the player exists in the file - if they don't, create a config section for them
		ConfigurationSection player_section = player_data_file.getConfigurationSection(owner.toString());
						
		if (player_section == null) {
			player_data_file.createSection(owner.toString());
			player_section = player_data_file.getConfigurationSection(owner.toString());
		
			player_data_file.set(owner.toString() + _TRUSTED, new ArrayList<String> ());
			player_section.createSection(SETTINGS);
			
			saveSettings(owner, new PlayerSettings ());
		}
	}
	
	
	
	public static void addTrusted(UUID owner, UUID member) {
		
		// Ensure the player actually exists
		checkPlayer(owner);
		
		// Add the specified player to the list of trusted players
		List<String> trusted_array = player_data_file.getStringList(owner.toString() + _TRUSTED);
		trusted_array.add(member.toString());
		player_data_file.set(owner.toString() + _TRUSTED, trusted_array);
		
		saveAndReload();
	}
	
	
	
	public static boolean removeTrusted(UUID owner, UUID member) {
		
		// Ensure the player actually exists
		checkPlayer(owner);
		
		// Get already trusted players
		List<String> trusted_array = player_data_file.getStringList(owner.toString() + _TRUSTED);
		
		// Check the specified player to untrust is already trusted (if not, display an error)
		if (!trusted_array.contains(member.toString()))
			return false;
		
		// If they do exist, remove them and set the stored array to the new array
		trusted_array.remove(member.toString());
		player_data_file.set(owner.toString() + _TRUSTED, trusted_array);
		
		saveAndReload();
		
		// Successful execution
		return true;
	}
	
	
	
	public static Player[] getTrustedPlayers(UUID owner) {
		
		// Ensure the player actually exists
		checkPlayer(owner);
		
		// Get already trusted players
		List<String> trusted_array = player_data_file.getStringList(owner.toString() + _TRUSTED);
		
		// Read trusted players into a new array
		ArrayList<Player> players = new ArrayList<> ();
		for (String uuid : trusted_array)
			players.add(Bukkit.getPlayer(UUID.fromString(uuid)));
		
		// Return the new array
		return players.toArray(new Player[players.size()]);
	}
}
