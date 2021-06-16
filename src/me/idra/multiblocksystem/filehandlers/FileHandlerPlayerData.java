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
import me.idra.multiblocksystem.objects.PlayerSettings.SettingContainer;



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
			Logger.fileNotFoundError(data_file);
			return;
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
		
		for (String container_key : settings.settingContainerMap.keySet()) {

			SettingContainer container = settings.settingContainerMap.get(container_key);
			settings_section.set(container_key, container.getValue());
		}
		
		saveAndReload();
	}
	
	
	
	public static void loadSettings(UUID player) {
		
		checkPlayer(player);
		
		PlayerSettings settings = new PlayerSettings();
		
		ConfigurationSection player_section = player_data_file.getConfigurationSection(player.toString());
		ConfigurationSection settings_section = player_section.getConfigurationSection(SETTINGS);
		
		settings.auto_build_enabled = settings_section.getBoolean("auto_build_enabled");
		
		for (String container_key : settings.settingContainerMap.keySet()) {

			int value = settings_section.getInt(container_key);
			SettingContainer container = settings.settingContainerMap.get(container_key);
			container.setValue(value);
			settings.settingContainerMap.put(container_key, container);
		}
		
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
		}

		if (player_section.getConfigurationSection(SETTINGS) == null) {

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
