package me.idra.multiblocksystem.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.lists.ListAbstractMultiblocks;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public class FileHandlerWorldMultiblocks {

	static final String RECIPE = "recipe";
	static final String POSITIONS = "positions";
	
	static FileConfiguration world_multiblock_config;
	static File world_multiblock_file; 



	private FileHandlerWorldMultiblocks() {
        // Empty constructor
    }
	
	
	
	public static void loadFile() {
		
		// Load the file
		world_multiblock_file = new File(new File(ManagerPlugin.plugin.getDataFolder(), "data"), "WorldMultiblocks.yml");
	
		// Check the WorldMultiblocks.yml file exists
		if (!world_multiblock_file.exists()) {
			try {
				world_multiblock_file.createNewFile();
			} catch (IOException e) {
				// This should never happen - if it does, we have much bigger problems to worry about
			}
		}
		
		// Load the config
		world_multiblock_config = YamlConfiguration.loadConfiguration(world_multiblock_file);
	}
	
	
	
	public static void saveFile() {
		
		try {
			world_multiblock_config.save(world_multiblock_file);
		} catch (IOException e) {
			Logger.log(
					Logger.getWarning("save-fail-player-data"),
					true);
		}
	}
	

	
	
	public static void deleteMultiblock(int ID) {
		
		// Reload the file, just in case
		loadFile();
		
		// Delete the section that starts with the ID
		world_multiblock_config.set(String.valueOf(ID), null);
		
		// Save the new config
		try {
			world_multiblock_config.save(world_multiblock_file);
		} catch (IOException e) {
			// This should never happen - if it does, we have much bigger problems to worry about
		}
	}
	
	
	
	public static void saveMultiblocks() {
		
		// Reload the file, just in case
		loadFile();
		
		// For every multiblock
		for (BaseWorldMultiblock multiblock : ListWorldMultiblocks.multiblock_objects.values()) {
			
			// Create the config sections if necessary
			ConfigurationSection multiblock_section = world_multiblock_config.getConfigurationSection(String.valueOf(multiblock.ID));
					
			if (multiblock_section == null) {
				world_multiblock_config.createSection(String.valueOf(multiblock.ID));
				multiblock_section = world_multiblock_config.getConfigurationSection(String.valueOf(multiblock.ID));
			}
			
			// Set basic variables
			Set<BlockPosition> blocks_as_array_list = multiblock.blocks.keySet();
			
			multiblock_section.set("Owner", multiblock.owner.toString());
			multiblock_section.set("World", blocks_as_array_list.iterator().next().getWorld().getName());	// no idea wtf I'm doing
			multiblock_section.set("AbstractMultiblock", multiblock.abstract_multiblock.name.toUpperCase());
			multiblock_section.set("FuelTicks", multiblock.fuel_ticks);
			
			// Check current recipe section exists
			ConfigurationSection recipe_section = multiblock_section.getConfigurationSection(RECIPE);
			
			if (recipe_section == null) {
				multiblock_section.createSection(RECIPE);
				recipe_section = multiblock_section.getConfigurationSection(RECIPE);
			}
			
			// Store recipe
			recipe_section.set("Index", multiblock.abstract_multiblock.recipes.indexOf(multiblock.active_recipe));
			if (multiblock.active_recipe != null)
				recipe_section.set("Time", multiblock.active_recipe.time);
			else
				recipe_section.set("Time", 0);
			
			// Check map sections exist
			ConfigurationSection position_section = multiblock_section.getConfigurationSection(POSITIONS);
			
			if (position_section == null) {
				multiblock_section.createSection(POSITIONS);
				position_section = multiblock_section.getConfigurationSection(POSITIONS);
			}
			
			// Store blocks and their respective tags
			for (BlockPosition position : multiblock.blocks.keySet())
				position_section.set(String.valueOf(position.getPosition()), multiblock.blocks.get(position));	// x/y/z : String
		}
		
		// Save the new config
		try {
			world_multiblock_config.save(world_multiblock_file);
		} catch (IOException e) {
			// This should never happen - if it does, we have much bigger problems to worry about
		}
	}
	
	
	
	public static boolean loadMultiblocks() {
		
		// Reload the file, just in case
		loadFile();
				
		// For every multiblock
		for (String id_as_string : world_multiblock_config.getKeys(false)) {
				
			// Convert string to int
			int ID = Integer.parseInt(id_as_string);
			
			// Get the config section
			ConfigurationSection multiblock_section = world_multiblock_config.getConfigurationSection(id_as_string);
			ConfigurationSection position_section = multiblock_section.getConfigurationSection(POSITIONS);
					
			// Get basic variables
			UUID uuid = UUID.fromString(multiblock_section.getString("Owner"));
			String name = multiblock_section.getString("AbstractMultiblock");
			int fuel_ticks = multiblock_section.getInt("FuelTicks");
			AbstractMultiblock abstract_multiblock = ListAbstractMultiblocks.structures.get(name);
					
			// Get block positions
			Map<BlockPosition, String[]> blocks = new HashMap<> ();
			
			// Store blocks and their respective tags
			for (String position_as_string : position_section.getKeys(false)) {
				
				// Convert string to position
				BlockPosition position = new BlockPosition(Bukkit.getWorld(multiblock_section.getString("World")), Long.valueOf(position_as_string));
				List<String> tags =  (ArrayList<String>) position_section.getStringList(position_as_string);	// Long : String
				
				// Add to block position map
				blocks.put(position, tags.toArray(new String[tags.size()]));
			}
			
			// Get current recipe information
			ConfigurationSection recipe_section = multiblock_section.getConfigurationSection(RECIPE);
			
			// Store recipe
			int recipe_index = recipe_section.getInt("Index");
			int recipe_time = recipe_section.getInt("Time");
			
			// Generate WorldMultiblock
			ListWorldMultiblocks.instantiateWorldMultiblock(abstract_multiblock, blocks, uuid, ID, fuel_ticks, recipe_index, recipe_time);
		}
		
		// Successful execution
		return true;
	}
}
