package me.idra.multiblocksystem.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import org.bukkit.Material;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.idra.multiblocksystem.lists.ListVariantPrefixes;
import me.idra.multiblocksystem.objects.AbstractMixedItemStack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;



public class StringConversion {

	

	private StringConversion() {
		// Empty constructor
	}

	
	public static ArrayList<String> removeDuplicates(ArrayList<String> array) {
		
		// Convert array to set
		HashSet<String> set = new HashSet<String>();

		for(int i = 0; i < array.size(); i++)
		  set.add(array.get(i));
		
		// Convert set to array
		ArrayList<String> new_array = new ArrayList<String> ();
		new_array.addAll(set);
		
		return new_array;
	}
	
	
	
	public static String materialToString(Material material) {
		
		// Get material name
		String name = material.name();
		
		// Remove underscores
		name = name.toLowerCase().replace("_", " ");
		
		// Capitalise each word
		String final_name = "";
		String words[] = name.split("\\s");
		
		for (int i = 0; i < words.length; i++) {
			
			final_name += words[i].substring(0, 1).toUpperCase() + words[i].substring(1, words[i].length());  
			
			if (i != words.length - 1)
				final_name += " ";
		}
		
		// Return the final name
		return final_name;
	}
	
	
	public static Material idToMaterial(String name) {
		return Material.getMaterial(name.toUpperCase().replace(" ", "_"));
	}
	
	
	public static SlimefunItem idToSlimefunItem(String ID) {
		return SlimefunPlugin.getRegistry().getSlimefunItemIds().get(ID);
	}
	
	
	public static AbstractMixedItemStack stringToAbstractMixedItemStack(String block_data) {

		// Get material and slimefun data (if the latter exists)
		boolean is_slimefun_item = false;
		Material material = null;
		SlimefunItem slimefun_item = null;
		ArrayList<String> tag_data = new ArrayList<String> ();
		
		// Before that, we have to handle tags though (if they exist)
		if (block_data.contains("[")) {
			
			// Get a string of tags, separated by commas
			String tag_string = block_data.substring(block_data.indexOf("[")+1, block_data.indexOf("]"));

			try (Scanner tag_scanner = new Scanner(tag_string).useDelimiter(",");) {
				
				
				// Add tags to tag data
				while (tag_scanner.hasNext()) {
					tag_data.add(tag_scanner.next());
				}
			
			}
			
			// Get rid of the tags from the original string so we can process the material or slimefun item
			block_data = block_data.substring(0, block_data.indexOf("["));
		}
		
		
		// If it's a slimefun item
		if (block_data.contains("sf:")) {
			
			// Get slimefun item
			is_slimefun_item = true;
			slimefun_item = idToSlimefunItem(block_data.substring(block_data.indexOf(":")+1, block_data.length()).toUpperCase());
		
		// If it's a vanilla material
		} else {
			
			// Get material
			material = idToMaterial(block_data.toUpperCase());
		}
		
		
		// Check the slimefun ID is valid
		if (is_slimefun_item  && (slimefun_item == null)) {
				
			// Display a console error and add substitute air in instead
			Logger.log(
					Logger.getWarning("slimefun-item-invalid")
					.replace("%slimefun-item%", block_data),
					true);
				
			// Just return the MixedItemStack as air straight away
			return new AbstractMixedItemStack(Material.AIR, null);
		}
		
		
		// Start setting up prefixes
		ArrayList<Material> materials = new ArrayList<Material> ();
		boolean uses_prefix_variant = false;
		
		// For every tag
		for (String tag : tag_data) {
			
			// If the tag specifies a prefix group
			if (ListVariantPrefixes.prefix_groups.containsKey(tag.toUpperCase())) {
				
				// For every prefix in the group, add prefix + "_" +  suffix
				for (String prefix : ListVariantPrefixes.prefix_groups.get(tag.toUpperCase())) {
					
					String prefix_material_name = prefix + "_" + block_data.toUpperCase();
					Material prefix_material = Material.getMaterial(prefix_material_name);
					
					if (prefix_material != null)
						materials.add(prefix_material);
					else
						Logger.log(
								Logger.getWarning("material-invalid")
								.replace("%material%", prefix_material_name),
								true);
				}
				
				// We've already added materials, so no need to try to generate one solely from the material data (would throw an error anyways)
				uses_prefix_variant = true;
			}
		}
		
		
		if (!uses_prefix_variant) {
			
			// Check the material is valid
			if (!is_slimefun_item && (material == null)) {
					
				// Display a console error and add substitute air in instead
				Logger.log(
						Logger.getWarning("material-invalid")
						.replace("%material%", block_data),
						true);
					
				// Just return the MixedItemStack as air straight away
				return new AbstractMixedItemStack(Material.AIR, null);
			}
		
			// Add the default material to the array
			materials.add(material);
		}
		
		// Generate new item info using the data we just gathered
		if (is_slimefun_item)
			return new AbstractMixedItemStack(slimefun_item, tag_data.toArray(new String[tag_data.size()]));
		else
			return new AbstractMixedItemStack(materials, tag_data.toArray(new String[tag_data.size()]));
	}
}
