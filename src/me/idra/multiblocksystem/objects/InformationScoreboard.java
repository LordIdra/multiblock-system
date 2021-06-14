package me.idra.multiblocksystem.objects;

import org.bukkit.entity.Player;

import fr.mrmicky.fastboard.FastBoard;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;



public class InformationScoreboard {
	
	FastBoard board;
	BaseWorldMultiblock multiblock = null;



	public InformationScoreboard(Player player, BaseWorldMultiblock multiblock) {
		board = new FastBoard(player);
		this.multiblock = multiblock;
		update();
	}



	public void update() {
		board.updateLines(
			multiblock.abstract_multiblock.name,
			"test"
		);
	}



	public void hide() {
		board.delete();
	}
}