package me.idra.multiblocksystem.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.mrmicky.fastboard.FastBoard;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.helpers.StringConversion;



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

		StringBuilder completion = new StringBuilder(ChatColor.GRAY + "[" + ChatColor.RED + "||||||||||||||||||||" + ChatColor.GRAY + "]");

		if (multiblock.active_recipe != null) {

			completion.append(ChatColor.GRAY + "[" + ChatColor.GREEN);
			int percentage_complete_20 = ((multiblock.recipe_ticks_remaining * 20) / multiblock.active_recipe.time);
			
			for (int i = 0; i < 20 - percentage_complete_20; i++) {
				completion.append("|");
			}

			completion.append(ChatColor.DARK_GRAY);

			for (int i = 0; i < percentage_complete_20; i++) {
				completion.append("|");
			}

			completion.append(ChatColor.GRAY + "]" + ChatColor.GREEN);
		}

		int seconds_remaining = Math.floorDiv(multiblock.fuel_ticks, 20);
		int seconds_max = Math.floorDiv(multiblock.abstract_multiblock.max_fuel, 20);
		
		String remaining_text = StringConversion.formatTime(ChatColor.BLUE, ChatColor.DARK_GRAY, seconds_remaining);
		String max_text =   	StringConversion.formatTime(ChatColor.BLUE, ChatColor.DARK_GRAY, seconds_max);

		String fuel_text = remaining_text + ChatColor.YELLOW + "/" + max_text;

		board.updateLines(
			"",
			ChatColor.YELLOW + multiblock.abstract_multiblock.fuel_name + " " + fuel_text,
			ChatColor.YELLOW + "Progress " + ChatColor.BLUE + completion,
			ChatColor.YELLOW + "Status " + ChatColor.BLUE + multiblock.status);
	}



	public void hide() {
		board.delete();
	}
}