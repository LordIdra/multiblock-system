package me.idra.multiblocksystem.objects;

import org.bukkit.entity.Player;

import fr.mrmicky.fastboard.FastBoard;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;



public class InformationScoreboard {
	
	FastBoard board;



	InformationScoreboard(Player player, BaseWorldMultiblock multiblock) {
		board = new FastBoard(player);
		update();
	}



	public void update() {

	}



	public void hide() {
		board.delete();
	}
}