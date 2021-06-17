package me.idra.multiblocksystem.objects;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.idra.multiblocksystem.helpers.StringConversion;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;



public class BlockError {

	public Block block;
	public ItemGroup group;
	public Player player;
	
	
	
	public BlockError(Block block, ItemGroup group, Player player) {
		this.block = block;
		this.group = group;
		this.player = player;
	}
	
	
	public String getErrorMessage() {
		
		// Get location as vector
		Vector location_vector = block.getLocation().toVector();
		
		// Block that the location currently is
		String current_string = ChatColor.DARK_RED + "[" + ChatColor.WHITE + StringConversion.formatBlock(block) + ChatColor.DARK_RED + "]";

		// Block that the location should be
		String should_be_string = ChatColor.DARK_RED + "[" + ChatColor.WHITE + StringConversion.formatItemName(group.name) + ChatColor.DARK_RED + "]";

		// Generate the final error string
		return 	ChatColor.DARK_RED +  "x" + ChatColor.YELLOW + location_vector.getBlockX() +
				ChatColor.DARK_RED + " y" + ChatColor.YELLOW + location_vector.getBlockY() +
				ChatColor.DARK_RED + " z" + ChatColor.YELLOW + location_vector.getBlockZ() +
				ChatColor.GOLD + "" + ChatColor.BOLD + " --> " +
				current_string + " should be " + should_be_string;
	}
	
	
	
	public String getErrorTitle(boolean resolved) {
		
		// Figure out colors
		ChatColor primary_color;
		
		if (resolved)
			primary_color = ChatColor.DARK_GREEN;
		else
			primary_color = ChatColor.DARK_RED;
		
		// Generate the final error string
		return primary_color + "[" + ChatColor.WHITE + group.name + primary_color + "]";
	}
	
	
	
	public boolean isResolved() {
		
		// Update the current block
		block = block.getLocation().getWorld().getBlockAt(block.getLocation());
		
		// Compare blocks
		return compareBlockAndGroup(block, group);
	}
	
	
	
	/*
	 * STATIC METHODS
	 */
	
	public static BlockError getBlockError(Block block, ItemGroup group, Player player) {
			
		// Compare block/group to see if there's still an error
		if (compareBlockAndGroup(block, group)) {
			return null;
		} else {
			return new BlockError(block, group, player);
		}
	}
	
	
	
	public static List<BlockError> getBlockErrorsFromInfoMap(Player player, Map<Block, ItemGroup> block_to_group_map) {
		
		// Initialize empty array of block errors
		List<BlockError> block_errors = new ArrayList<> ();
		
		// For every BlockInfo in the structure
		for (Block block : block_to_group_map.keySet()) {
			
			// Generate block error
			ItemGroup group = block_to_group_map.get(block);
			BlockError block_error = getBlockError(block, group, player);
			
			// If the error isn't null, add it to the block error array
			if (block_error != null)
				block_errors.add(block_error);
		}
		
		// Literally what it says
		return block_errors;
	}


	public static boolean compareBlockAndGroup(Block block, ItemGroup group) {

		// For every stack the block could be
		for (ItemStack stack : group.stacks) {
				
			// Get material and slimefun item
			Material material = block.getType();
			SlimefunItem slimefun_item = BlockStorage.check(block);

			// Compare the two stacks, return true if they match
			if (slimefun_item != null) {
				if (SlimefunUtils.isItemSimilar(stack, slimefun_item.getItem(), false)) {
					return true;
				}

			} else {
				if (stack.getType() == material) {
					return true;
				}
			}
		}

		// No match found
		return false;
	}
}