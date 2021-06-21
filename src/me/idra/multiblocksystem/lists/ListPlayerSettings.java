package me.idra.multiblocksystem.lists;



import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import me.idra.multiblocksystem.objects.PlayerSettings;



public class ListPlayerSettings {

	private static Map<UUID, PlayerSettings> player_settings = new HashMap<> ();

	private ListPlayerSettings() {
		// Empty constructor
	}

	public static void setPlayerSettings(UUID player, PlayerSettings settings) {
		
		// Add the new player settings; this will overwrite existing ones automatically
		player_settings.put(player, settings);
	}
	
	
	
	public static PlayerSettings getPlayerSettings(UUID player) {
		
		// If the player doesn't exist in the array yet, create a new PlayerSettings for them
		if (player_settings.get(player) == null) {
			
			setPlayerSettings(player, new PlayerSettings());
		}
		
		return player_settings.get(player);
	}
}
