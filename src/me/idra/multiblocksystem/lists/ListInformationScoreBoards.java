package me.idra.multiblocksystem.lists;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.objects.InformationScoreboard;


public class ListInformationScoreBoards {

	private ListInformationScoreBoards() {
		// Empty constructor
	}

	// Map
    private static Map<UUID, InformationScoreboard> playerScoreBoard = new HashMap<>();
	
    public static void updateScoreboard() {
    	
    	playerScoreBoard.forEach((key, value) -> value.update());
    	
    }

	
    
    public static void toggleScoreBoard(Player player, BaseWorldMultiblock multiblock) {
    	
    	if (playerScoreBoard.containsKey(player.getUniqueId())) {
    		playerScoreBoard.get(player.getUniqueId()).hide();
    		playerScoreBoard.remove(player.getUniqueId());

    	} else {
			if (multiblock != null) {
    			playerScoreBoard.put(player.getUniqueId(), new InformationScoreboard(player, multiblock));
			}
    	}
    }
}
