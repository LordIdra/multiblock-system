package me.idra.multiblocksystem.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.mrmicky.fastboard.FastBoard;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;



public class InformationScoreboard {
	
	FastBoard board;
	BaseWorldMultiblock multiblock = null;



	public InformationScoreboard(Player player, BaseWorldMultiblock multiblock) {
		board = new FastBoard(player);
		board.updateTitle(ChatColor.GREEN + "" + ChatColor.BOLD + multiblock.abstract_multiblock.name);
		this.multiblock = multiblock;
		update();
	}



	public void update() {

		String completion = ChatColor.GRAY + "[";

		if (multiblock.active_recipe != null) {

			int percentage_complete_20 = ((multiblock.recipe_ticks_remaining * 20) / multiblock.active_recipe.time)
			
			for (int i = 0; i < percentage_complete_20; i++) {

			}

			board.updateLines(
			ChatColor.YELLOW + multiblock.abstract_multiblock.fuelname + ChatColor.BLUE + String.valueOf(Math.floorDiv(multiblock.fuel_ticks, 20)) + ChatColor.YELLOW + "s",
			"");
		}
	}



	public void hide() {
		board.delete();
	}
}