package me.idra.multiblocksystem.objects;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
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
		return primary_color + "[" + ChatColor.WHITE + StringConversion.formatItemName(group.name) + primary_color + "]";
	}
	
	
	
	public boolean isResolved() {
		
		// Update the current block
		World world = block.getLocation().getWorld();

		if (world != null) {
			block = world.getBlockAt(block.getLocation());
			return compareBlockAndGroup(block, group);
		}

		return false;
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
				
			// Get materials and slimefun items
			Material block_material = block.getType();
			SlimefunItem block_slimefun_item = BlockStorage.check(block);

			Material group_material = stack.getType();
			SlimefunItem group_slimefun_item = SlimefunItem.getByItem(stack);

			// Compare the two stacks, return true if they match
			if (block_slimefun_item != null
				|| group_slimefun_item != null) {
					
				if (block_slimefun_item != null 
					&& SlimefunUtils.isItemSimilar(stack, block_slimefun_item.getItem(), true)) {
					return true;
				}

			} else {
				if (group_material == block_material) {
					return true;
				}
			}
		}

		// No match found
		return false;
	}
}