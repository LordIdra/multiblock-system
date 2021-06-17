package me.idra.multiblocksystem.helpers;



import java.util.List;
import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.idra.multiblocksystem.objects.ItemGroup;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;



public class StringConversion {

	private StringConversion() {
		// Empty constructor
	}
	

	public static final String LABEL_LAYER = "layer-";
	

	public static String formatItemName(String name) {
		
		// Remove underscores
		name = name.toLowerCase().replace("_", " ");
		
		// Capitalise each word
		StringBuilder final_name = new StringBuilder();
		String[] words = name.split("\\s");
		
		for (int i = 0; i < words.length; i++) {
			 
			final_name.append(words[i].substring(0, 1).toUpperCase() + words[i].substring(1, words[i].length()));
			
			if (i != words.length - 1)
				final_name.append(" ");
		}
		
		// Return the final name
		return final_name.toString();
	}

	
	
	public static Material idToMaterial(String name) {
		return Material.getMaterial(name.toUpperCase().replace(" ", "_"));
	}
	
	
	public static SlimefunItem idToSlimefunItem(String ID) {
		return SlimefunPlugin.getRegistry().getSlimefunItemIds().get(ID);
	}
	
	
	public static ItemGroup stringToGroup(File file, ConfigurationSection section, String data) {

		// Capitalise the data (easier to deal with this way)
		data = data.toUpperCase();
		String tag = null;

		// Get tag
		if (data.contains("[")) {
			tag = data.substring(data.indexOf("[") + 1, data.indexOf("]"));
			data = data.substring(0, data.indexOf("["));
		}

		// Check which type of item we're dealing with (normal or a group)
		List<ItemStack> item_stack_list = new ArrayList<> ();
		String identifier;

		// Group
		if (data.contains("GROUP")) {
			String group_name = data.substring(6, data.length());
			identifier = group_name;
			item_stack_list = ConfigHelper.loadGroup(group_name);


		// Normal item
		} else {
			identifier = data;
			item_stack_list.add(ItemStackHelper.itemStackFromID(file, section, data));
		}

		// Create AbstractMixeditemStack
		return new ItemGroup(identifier, tag, item_stack_list);
	}



	public static String formatTime(ChatColor primary, ChatColor secondary, int seconds) {

		int minutes = Math.floorDiv(seconds, 60);
		int hours =   Math.floorDiv(minutes, 60);

		if (hours != 0) {
			return "" + primary + hours   		   + secondary + "h" +
				   "" + primary + (minutes % 60)   + secondary + "m" +
				   "" + primary + (seconds % 3600) + secondary + "s";

		} else if (minutes != 0) {
			return "" + primary + minutes 		 + secondary + "m" +
				   "" + primary + (seconds % 60) + secondary + "s";
		
		} else {
			return "" + primary + seconds + secondary + "s";
		}
	}
}
