package me.idra.multiblocksystem.lists;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.idra.multiblocksystem.objects.InformationScoreboard;


public class ListInformationScoreBoards {

	private ListInformationScoreBoards() {
		// Empty constructor
	}

	// Map
    public static Map<Player, InformationScoreboard> playerScoreBoard = new HashMap<>();
	
    public static void updateScoreboard() {
    	
    	playerScoreBoard.forEach((key, value) -> value.update());
    	
    }
    
    public static void toggleScoreBoards(Player player) {
    	
    	playerScoreBoard.remove(player);
    	
    }

}
