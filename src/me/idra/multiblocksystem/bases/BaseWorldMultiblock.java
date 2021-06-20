package me.idra.multiblocksystem.bases;

import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.MultiblockRecipe;
import me.idra.multiblocksystem.tasks.TaskTickMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.*;


public abstract class BaseWorldMultiblock {
	public final int ID;
	public final UUID owner;
	public final World world;

	public AbstractMultiblock abstract_multiblock;
	public List<BlockPosition> blocks;

	public MultiblockRecipe active_recipe = null;
	public int recipe_ticks_remaining = 0;

	public TaskTickMultiblock tick_task = null;
	public String status = RUNNING;        // is the machine currently processing a recipe?
	public int fuel_ticks = 0;

	protected BaseWorldMultiblock(AbstractMultiblock abstract_multiblock, List<BlockPosition> blocks, UUID owner, int ID) {

		// Set default variables
		this.blocks = blocks;
		this.owner = owner;
		this.ID = ID;

		// Get data from abstract multiblock
		this.abstract_multiblock = abstract_multiblock;
		this.world = this.blocks.get(0).getWorld();

		// Debug
		Logger.log(
				Logger.getInfo("on-assemble")
						.replace("%player%", Bukkit.getOfflinePlayer(owner).getName())
						.replace("%id%", String.valueOf(ID)),
				false);
	}

	public abstract void tick();

	public void startTick(boolean repeat) {

		// Initialize tick task, which will either run just once, or run every X ticks (in accordance with config file)
		if (repeat) {
			tick_task = new TaskTickMultiblock(this);
			tick_task.runTaskTimer(ManagerPlugin.plugin, 0, ManagerPlugin.tick_interval);
		} else {
			tick();
		}
	}

	public void disassemble() {
		// Cancel ticking task
		tick_task.cancel();

		// Delete the stored multiblock
		FileHandlerWorldMultiblocks.deleteMultiblock(ID);

		// The java way of deleting an object: set all references to null and hope for the best
		ListWorldMultiblocks.multiblock_objects.remove(ID);
	}

	public void tickRecipes() {
		// TODO: Implement a ticking system
	}

	public static final String RUNNING = ChatColor.GREEN + "RUNNING";
	public static final String PAUSED_ENERGY_OUTPUT_FULL = ChatColor.RED + "Energy output full";
	public static final String PAUSED_NOT_ENOUGH_ENERGY = ChatColor.RED + "No energy";
	public static final String PAUSED_ITEM_OUTPUT_FULL = ChatColor.RED + "Item output full";
	public static final String PAUSED_NOT_ENOUGH_ITEMS = ChatColor.RED + "No inputs";
	public static final String PAUSED_NO_FUEL = ChatColor.RED + "No fuel";

}
