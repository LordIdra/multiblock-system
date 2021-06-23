package me.idra.multiblocksystem.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.mrmicky.fastboard.FastBoard;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;



public class InformationScoreboard {
	
	FastBoard board;
	BaseWorldMultiblock multiblock;



	public InformationScoreboard(Player player, BaseWorldMultiblock multiblock) {
		board = new FastBoard(player);
		board.updateTitle(ChatColor.GREEN + "" + ChatColor.BOLD + multiblock.abstract_multiblock.name_of_structure_block);
		this.multiblock = multiblock;
		update();
	}



	public void update() {

		StringBuilder completion = new StringBuilder();

		if (multiblock.active_recipe != null) {

			completion.append(ChatColor.GRAY).append("[").append(ChatColor.GREEN);
			int percentage_complete_20 = ((multiblock.recipe_ticks_remaining * 20) / multiblock.active_recipe.crafting_time);

			completion.append("|".repeat(Math.max(0, 20 - percentage_complete_20)));

			completion.append(ChatColor.DARK_GRAY);

			completion.append("|".repeat(Math.max(0, percentage_complete_20)));

			completion.append(ChatColor.GRAY).append("]").append(ChatColor.GREEN);

		} else {
			completion = new StringBuilder(ChatColor.GRAY + "[" + ChatColor.RED + "||||||||||||||||||||" + ChatColor.GRAY + "]");
		}

		board.updateLines(
			"",
			ChatColor.YELLOW + "Progress " + ChatColor.BLUE + completion,
			ChatColor.YELLOW + "Status " + ChatColor.BLUE + multiblock.status);
	}



	public void hide() {
		board.delete();
	}
}