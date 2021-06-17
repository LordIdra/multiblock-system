package me.idra.multiblocksystem.objects;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.idra.multiblocksystem.helpers.StringConversion;



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
		String current_string = ChatColor.DARK_RED + "[" + ChatColor.WHITE + StringConversion.formatBlockName(block) + ChatColor.DARK_RED + "]";

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
		
		// For every ItemStack the abstract mixed item stack could be
		for (MixedItemStack abstract_itemstack : should_be_items.items) {
					
			// Compare material-material
			if (!abstract_itemstack.isSlimefunItem() && !current_block.isSlimefunItem()) {
				if (current_block.itemstack.getType() == abstract_itemstack.itemstack.getType())
					return true;
					
			// Compare slimefun-slimefun
			} else if (
				abstract_itemstack.isSlimefunItem() && current_block.isSlimefunItem()
				&& current_block.slimefun_itemstack.getItemId().equals(abstract_itemstack.slimefun_itemstack.getItemId())
			) {
				return true;
			}
		}
		
		// No match found
		return false;
	}
	
	
	
	/*
	 * STATIC METHODS
	 */
	
	public static BlockError getBlockError(WorldMixedItemStack block, AbstractMixedItemStack item, Player player) {

		// For every ItemStack the abstract mixed item stack could be
		for (MixedItemStack abstract_itemstack : item.items) {
			
			// Compare material-material
			if (!abstract_itemstack.isSlimefunItem() && !block.isSlimefunItem()) {
				if (block.itemstack.getType() == abstract_itemstack.itemstack.getType())
					return null;
			
			// Compare slimefun-slimefun
			} else if (
				abstract_itemstack.isSlimefunItem() && block.isSlimefunItem()
				&& block.slimefun_itemstack.getItemId().equals(abstract_itemstack.slimefun_itemstack.getItemId())
			) {
				return null;
			}
		}
		
		// If null hasn't been returned at this stage, there's an error
		return new BlockError(block, item, player);
	}
	
	
	
	public static List<BlockError> getBlockErrorsFromInfoMap(Player player, Map<WorldMixedItemStack, AbstractMixedItemStack> block_map) {
		
		// Initialize empty array of block errors
		List<BlockError> block_errors = new ArrayList<> ();
		
		// For every BlockInfo in the structure
		for (WorldMixedItemStack world_block : block_map.keySet()) {
			
			// Generate block error
			AbstractMixedItemStack abstract_block = block_map.get(world_block);
			BlockError block_error = getBlockError(world_block, abstract_block, player);
			
			// If the error isn't null, add it to the block error array
			if (block_error != null)
				block_errors.add(block_error);
		}
		
		// Literally what it says
		return block_errors;
	}
}