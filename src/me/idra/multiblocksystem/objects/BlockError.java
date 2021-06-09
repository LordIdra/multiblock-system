package me.idra.multiblocksystem.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.idra.multiblocksystem.helpers.StringConversion;
import me.idra.multiblocksystem.lists.ListVariantPrefixes;



public class BlockError {

	public WorldMixedItemStack current_item;
	public AbstractMixedItemStack should_be_items;
	public Player player;
	
	
	
	public BlockError(WorldMixedItemStack current_item, AbstractMixedItemStack should_be_items, Player player) {
		this.current_item = current_item;
		this.should_be_items = should_be_items;
		this.player = player;
	}
	
	
	public String getErrorMessage() {
		
		// Get location as vector
		Vector location_vector = current_item.location.toVector();
		
		// Block that the location currently is
		String current_string = ChatColor.DARK_RED + "[" + ChatColor.WHITE + current_item.getDisplayName() + ChatColor.DARK_RED + "]";

		// Block(s) that the location should be
		ArrayList<String> should_be_array = new ArrayList<String> ();
		
		for (int i = 0; i < should_be_items.items.size(); i++) {
			
			MixedItemStack item_stack = should_be_items.items.get(i);
			should_be_array.add(ChatColor.WHITE + ListVariantPrefixes.removePrefix(item_stack.getDisplayName()));
		}
		
		// Remove duplicates (OAK_FENCE, BIRCH_FENCE) will have become (FENCE, FENCE)
		should_be_array = StringConversion.removeDuplicates(should_be_array);
		
		// Convert array to single string	
		String should_be_string = ChatColor.DARK_RED + "[";;
		
		for (int i = 0; i < should_be_array.size(); i++) {
			
			should_be_string += should_be_array.get(i);
			
			if (i != should_be_array.size() - 1)
				should_be_string += (ChatColor.DARK_RED + ", ");
		}
		
		should_be_string += ChatColor.DARK_RED + "]";

		// Generate the final error string
		return new String  (ChatColor.DARK_RED +  "x" + ChatColor.YELLOW + location_vector.getBlockX() +
							ChatColor.DARK_RED + " y" + ChatColor.YELLOW + location_vector.getBlockY() +
							ChatColor.DARK_RED + " z" + ChatColor.YELLOW + location_vector.getBlockZ() +
							ChatColor.GOLD + "" + ChatColor.BOLD + " --> " +
							current_string + " should be " + should_be_string);
	}
	
	
	
	public String getErrorTitle(boolean resolved) {
		
		// Figure out colors
		ChatColor primary_color;
		
		if (resolved)
			primary_color = ChatColor.DARK_GREEN;
		else
			primary_color = ChatColor.DARK_RED;
		
		// Block(s) that the location should be
		ArrayList<String> should_be_array = new ArrayList<String> ();
		
		for (int i = 0; i < should_be_items.items.size(); i++) {
			
			MixedItemStack item_stack = should_be_items.items.get(i);
			should_be_array.add(ChatColor.WHITE + ListVariantPrefixes.removePrefix(item_stack.getDisplayName()));
		}
		
		// Remove duplicates (OAK_FENCE, BIRCH_FENCE) will have become (FENCE, FENCE)
		should_be_array = StringConversion.removeDuplicates(should_be_array);
		
		// Convert array to single string
		String should_be_string = primary_color + "[";;
		
		for (int i = 0; i < should_be_array.size(); i++) {
			
			should_be_string += should_be_array.get(i);
			
			if (i != should_be_array.size() - 1)
				should_be_string += (primary_color + ", ");
		}
		
		should_be_string += primary_color + "]";
		
		// Generate the final error string
		return should_be_string;
	}
	
	
	
	public boolean isResolved() {
		
		// Update the current MixedItemStack
		Block block = current_item.location.getWorld().getBlockAt(current_item.location);
		WorldMixedItemStack current_block = new WorldMixedItemStack(block);
		
		// For every ItemStack the abstract mixed item stack could be
		for (MixedItemStack abstract_itemstack : should_be_items.items) {
					
			// Compare material-material
			if (!abstract_itemstack.isSlimefunItem() && !current_block.isSlimefunItem()) {
				if (current_block.itemstack.getType() == abstract_itemstack.itemstack.getType())
					return true;
					
			// Compare slimefun-slimefun
			} else if (abstract_itemstack.isSlimefunItem() && current_block.isSlimefunItem()) {
				if (current_block.slimefun_itemstack.getItemId().equals(abstract_itemstack.slimefun_itemstack.getItemId()))
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
			} else if (abstract_itemstack.isSlimefunItem() && block.isSlimefunItem()) {
				if (block.slimefun_itemstack.getItemId().equals(abstract_itemstack.slimefun_itemstack.getItemId()))
					return null;
			}
		}
		
		// If null hasn't been returned at this stage, there's an error
		return new BlockError(block, item, player);
	}
	
	
	
	public static ArrayList<BlockError> getBlockErrorsFromInfoMap(Player player, HashMap<WorldMixedItemStack, AbstractMixedItemStack> block_map) {
		
		// Initialize empty array of block errors
		ArrayList<BlockError> block_errors = new ArrayList<BlockError> ();
		
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