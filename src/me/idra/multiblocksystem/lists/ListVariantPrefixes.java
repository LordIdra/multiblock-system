package me.idra.multiblocksystem.lists;

import java.util.HashMap;

public class ListVariantPrefixes {

	private ListVariantPrefixes() {
		// Empty constructor
	}


	
	public static String[] prefix_normal_wood = new String[] {
			"OAK",
			"SPRUCE",
			"BIRCH",
			"JUNGLE",
			"ACACIA",
			"DARK_OAK"
	};
	
	public static String[] prefix_nether_wood = new String[] {
			"CRIMSON",
			"WARPED"
	};
	
	public static String[] prefix_color = new String[] {
			"WHITE",
			"ORANGE",
			"MAGENTA",
			"LIGHT_BLUE",
			"YELLOW",
			"LIME",
			"PINK",
			"GRAY",
			"LIGHT_GRAY",
			"CYAN",
			"PURPLE",
			"BLUE",
			"BROWN",
			"GREEN",
			"RED",
			"BLACK"
	};
	
	public static HashMap<String, String[]> prefix_groups = new HashMap<String, String[]> ();
	
	
	
	public static void initialize() {
		
		// Setup prefix group hashmap
		prefix_groups.put("NORMAL_WOOD", prefix_normal_wood);
		prefix_groups.put("NETHER_WOOD", prefix_nether_wood);
		prefix_groups.put("COLOR",       prefix_color);
	}
	
	
	public static String removePrefixIfInArray(String[] prefix_list, String material) {
		
		// Check that the material name even contains a prefix
		if (!material.contains(" "))
			return material;
		
		// For every possible prefix
		for (String possible_prefix : prefix_list) {
			
			// If the material name length is long enough to check, and if the prefixes match
			if (
				material.length() >= possible_prefix.length()
				&& material.substring(0, possible_prefix.length()).toUpperCase().replace(" ", "_").equals(possible_prefix)
			) {

				// Delete the prefix and return the remaining string
				return material.substring(possible_prefix.length() + 1, material.length());
			}
		}
		
		// Not found; return original string
		return material;
	}
	
	
	
	public static String removePrefix(String material) {
		
		// Remove every possible type of prefix
		for (String[] prefix_group : prefix_groups.values())
			material = removePrefixIfInArray(prefix_group, material);
		
		// Return final string
		return material;
	}
}
