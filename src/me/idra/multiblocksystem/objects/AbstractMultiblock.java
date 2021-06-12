package me.idra.multiblocksystem.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.idra.multiblocksystem.helpers.ConfigHelper;
import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.StringConversion;
import me.idra.multiblocksystem.managers.ManagerPlugin;



public class AbstractMultiblock {
	
	public String name;
	public String description;
	public StructureDescriptor structure;
	
	public List<String> inventory_tags = new ArrayList<> ();
	public List<String> fuel_tags = new ArrayList<> ();
	public List<String> energy_tags = new ArrayList<> ();
	public Map<String, List<MixedItemStack>> position_tags = new HashMap<> ();

	public List<String> fuel_inventories = new ArrayList<> ();

	public Map<String, Object> variables = new HashMap<> ();
	public List<MultiblockRecipe> recipes = new ArrayList<> ();
	public List<MultiblockFuel> fuels = new ArrayList<> ();
	
	
	
	public AbstractMultiblock(String name) {
		this.name = name;
		loadStructure();
	}
	
	

	public void loadStructure() {
		
		/*
		 * FILE HANDLING
		 */
		
		// Open multiblock files
		File multiblock_folder = new File(new File(ManagerPlugin.plugin.getDataFolder(), "multiblocks"), name);
		File multiblock_file = new File(multiblock_folder, "settings.yml");
		File structure_file = new File(multiblock_folder, "structure.yml");
		
		// Check the files actually exist
		if (!multiblock_file.exists()) {
			Logger.log(
					Logger.getWarning("multiblock-file-not-found")
					.replace(ConstantPlaceholders.NAME, String.valueOf(name)),
					true);
			return;
		}
		
		if (!structure_file.exists()) {
			Logger.log(
					Logger.getWarning("structure-file-not-found")
					.replace(ConstantPlaceholders.NAME, String.valueOf(name)),
					true);
			return;
		}
		
		
		
		/*
		 * CONFIG LOADING
		 */
		
		// Get config from multiblock file
		FileConfiguration multiblock_config = YamlConfiguration.loadConfiguration(multiblock_file);
		
		// Get parameters from said config
		description = multiblock_config.getString("Description");
		
		// Get certain sections we need to read
		List<String> inventory_tag_list = multiblock_config.getStringList("InventoryTags");
		List<String> fuel_tag_list = multiblock_config.getStringList("FuelTags");
		List<String> energy_tag_list = multiblock_config.getStringList("EnergyTags");

		ConfigurationSection position_tag_config = multiblock_config.getConfigurationSection("PositionTags");
		ConfigurationSection variable_config = multiblock_config.getConfigurationSection("Variables");
		ConfigurationSection fuel_config = multiblock_config.getConfigurationSection("Fuels");
		ConfigurationSection recipe_config = multiblock_config.getConfigurationSection("Recipes");
		


		/*
		 * TAG HANDLING
		 */

		// Inventory tags
		if (inventory_tag_list != null) {
			for (String tag : inventory_tag_list) {
				inventory_tags.add(tag);
			}
		}	

		// Fuel tags
		if (fuel_tag_list != null) {
			for (String tag : fuel_tag_list) {
				fuel_tags.add(tag);
			}
		}	

		// Energy tags
		if (energy_tag_list != null) {
			for (String tag : energy_tag_list) {
				energy_tags.add(tag);
			}
		}

		// Position tags
		if (position_tag_config != null) {
			for (String tag : position_tag_config.getKeys(false)) {

				List<MixedItemStack> items = new ArrayList<> ();
				
				// For every position tag, get the items the tag can be, and add them to an array
				for (String item : position_tag_config.getStringList(tag)) {
					
					MixedItemStack stack = StringConversion.mixedItemStackFromID(item);

					if (stack == null) {
						Logger.configError(Logger.OPTION_INVALID, multiblock_file, position_tag_config, tag);
						continue;
					}

					items.add(stack);
				}

				// Create the position tag itself
				position_tags.put(tag, items);
			}
		}
		
		// Get variables
		if (variable_config != null) {
			for (String variable_name : variable_config.getKeys(false)) {
				variables.put(variable_name, variable_config.get(variable_name));
			}
		}
		


		/*
		 * FUEL
		 */

		// Load fuels
		if ((fuel_config != null) && (fuel_tag_list.size() != 0)) {

			for (String key : fuel_config.getKeys(false)) {

				// Seperate string into time and ID
				String[] split_string = fuel_config.getString(key).split("\\s");

				if (split_string.length != 2) {
					Logger.configError(Logger.OPTION_INVALID, multiblock_file, fuel_config, key);
					continue;
				}

				int time = Integer.parseInt(split_string[0]);
				String id = split_string[1];

				// Create MixedItemStack
				MixedItemStack stack = StringConversion.mixedItemStackFromID(id);

				if (stack == null) {
					Logger.configError(Logger.OPTION_INVALID, multiblock_file, fuel_config, key);
					continue;
				}

				// Finally, create the fuel
				fuels.add(new MultiblockFuel(stack, time));
			}
		}
		


		/*
		 * RECIPES
		 */

		// Load recipes
		if (recipe_config != null) {
			for (String key : recipe_config.getKeys(false)) {
				
				// Get config sections
				ConfigurationSection current_recipe = recipe_config.getConfigurationSection(key);
				ConfigurationSection config_energy = current_recipe.getConfigurationSection("ENERGY");
				ConfigurationSection config_inputs = current_recipe.getConfigurationSection("INPUT");
				ConfigurationSection config_outputs = current_recipe.getConfigurationSection("OUTPUT");

				// Initialize variables
				int time = current_recipe.getInt("TIME");
				Map<String, Integer> energy = null;
				Map<String, List<MixedItemStack>> inputs = null;
				Map<String, List<MixedItemStack>> outputs = null;

				// Check recipe time is valid
				if (time == 0) {
					Logger.configError(Logger.OPTION_INVALID, multiblock_file, current_recipe, "TIME");
				}

				// ENERGY
				if (config_energy != null) {
					energy = ConfigHelper.getEnergyMap(multiblock_file, config_energy);
				}

				// INPUT
				if (config_inputs != null) {
					inputs = ConfigHelper.getInputMap(multiblock_file, config_inputs);
				}


				// OUTPUT
				if (config_outputs != null) {
					outputs = ConfigHelper.getOutputMap(multiblock_file, config_outputs);
				}

				// Combine all the above into a new recipe
				recipes.add(new MultiblockRecipe(inputs, outputs, energy, time));
			}
		}

		
		/*
		 * STRUCTURE LOADING
		 */

		// Dimensions
		int dimension_x = 0;
		int dimension_z = 0;

		// Generate empty array which will store the block data strings for each layer
		List<List<String>> structure_array = new ArrayList<> ();

		try {
			try (Scanner structure_file_scanner = new Scanner(structure_file).useDelimiter("\\s+")) {
					
				// Get dimension X
				if (!structure_file_scanner.next().equalsIgnoreCase("DIMENSION-X:")) {
					Logger.configError(Logger.OPTION_NOT_FOUND, structure_file, null, "DIMENSION-X");
					return;
					
				} else {
					dimension_x = structure_file_scanner.nextInt();
				}
				
				// Get dimension Z
				if (!structure_file_scanner.next().equalsIgnoreCase("DIMENSION-Z:")) {
					Logger.configError(Logger.OPTION_NOT_FOUND, structure_file, null, "DIMENSION-Z");
					return;
					
				} else {
					dimension_z = structure_file_scanner.nextInt();
				}
				
				// For each block data string in the file
				while (structure_file_scanner.hasNext()) {
					
					// Get block data string
					String word = structure_file_scanner.next();
					
					// If we're beginning a new layer
					if (word.equals("LAYER:")) {
						
						// Add a new array to store block data strings for this layer
						structure_array.add(new ArrayList<>());
						continue;
					}
					
					// If we're not beginning a new layer, add the block data string to the layer's array
					List<String> current_array_list = structure_array.get(structure_array.size()-1);
					current_array_list.add(word);
				}
			}
		} catch (Exception e) {
			return;
		}
		


		/*
		 * CREATE DESCRIPTOR
		 */

		// Convert the data from the structure arrays to AbstractMixedItemStacks
		List<List<List<AbstractMixedItemStack>>> block_array = new ArrayList<> ();
		
		for (int y = 0; y < structure_array.size(); y++) {
			block_array.add(new ArrayList<> ());
			
			for (int x = 0; x < dimension_x; x++) {
				block_array.get(y).add(new ArrayList<> ());
				
				for (int z = 0; z < dimension_z; z++) {
					
					// Get the array we're dealing with
					List<List<AbstractMixedItemStack>> block_array_y = block_array.get(y);
					List<AbstractMixedItemStack> block_array_x = block_array_y.get(x);
					
					// Convert item info string to abstract mixed item stack
					AbstractMixedItemStack block = StringConversion.stringToAbstractMixedItemStack(structure_file, y, structure_array.get(y).get((z * dimension_x) + x));
					block_array_x.add(block);
					
					// Set the arrays again (not 100% sure if this is necessary)
					block_array_y.set(x, block_array_x);
					block_array.set(y, block_array_y);
				}
			}
		}
		
		// Use this to create a new structure descriptor
		structure = new StructureDescriptor(
				dimension_x,
				dimension_z,
				structure_array.size(),
				block_array);
	}



	/*
	 * STATIC METHODS
	 */
		
	
	public static Map<WorldMixedItemStack, AbstractMixedItemStack> getStructureFromStartingPoint(
			Player player,
			Location central_block_location,
			BlockFace central_block_orientation, 
			StructureDescriptor abstract_descriptor) {
		
		// Initialize empty map that will store every BlockInfo and its corresponding ItemInfo
		Map<WorldMixedItemStack, AbstractMixedItemStack> world_to_abstract_map = new LinkedHashMap<> ();
		
		// Figure out block locations
		Location starting_point = central_block_location.clone();
		Vector relative_corner_location = abstract_descriptor.central_block.clone();
		Vector relative_offset = abstract_descriptor.dimension.clone();
		Location end_point;
		
		// Account for orientation using a huge if-else block
		if (central_block_orientation == BlockFace.SOUTH) {
			
			starting_point.subtract(relative_corner_location);
			
			end_point = starting_point.clone();
			end_point.subtract(relative_offset);
			
		
		} else if (central_block_orientation == BlockFace.NORTH) {
			
			relative_corner_location.setZ(-abstract_descriptor.central_block.getBlockZ());
			starting_point.subtract(relative_corner_location);
			
			end_point = starting_point.clone();
			relative_offset.setZ(-starting_point.getBlockZ());
			end_point.subtract(relative_offset);
			
			
		} else if (central_block_orientation == BlockFace.EAST) {
			
			int temp_x = relative_corner_location.getBlockX();
			relative_corner_location.setX(relative_corner_location.getBlockZ());
			relative_corner_location.setZ(temp_x);
			starting_point.subtract(relative_corner_location);
			
			end_point = starting_point.clone();
			temp_x = relative_offset.getBlockX();
			relative_offset.setX(relative_offset.getBlockZ());
			relative_offset.setZ(temp_x);
			end_point.subtract(relative_offset);
			
			
		} else if (central_block_orientation == BlockFace.WEST) {
			
			relative_corner_location.setZ(-abstract_descriptor.central_block.getBlockZ());
			int temp_x = relative_corner_location.getBlockX();
			relative_corner_location.setX(relative_corner_location.getBlockZ());
			relative_corner_location.setZ(temp_x);
			starting_point.subtract(relative_corner_location);
			
			end_point = starting_point.clone();
			relative_offset.setZ(-starting_point.getBlockZ());
			temp_x = relative_offset.getBlockX();
			relative_offset.setX(relative_offset.getBlockZ());
			relative_offset.setZ(temp_x);
			end_point.subtract(relative_offset);
		}
		
		
		
		// Enter block checking loop
		for (int y = 0; y < abstract_descriptor.dimension.getBlockY(); y++) {
			List<List<AbstractMixedItemStack>> abstract_array_y = abstract_descriptor.blocks.get(y);
			
			for (int x = 0; x < abstract_descriptor.dimension.getBlockX(); x++) {
				List<AbstractMixedItemStack> abstract_array_x = abstract_array_y.get(x);
				
				for (int z = 0; z < abstract_descriptor.dimension.getBlockZ(); z++) {
					
					// Get the block at the XYZ relative to the predetermined starting point
					Location block_location = null;
					
					if (central_block_orientation == BlockFace.SOUTH)
						block_location = starting_point.clone().add(new Location(player.getWorld(), x, y, z));
					else if (central_block_orientation == BlockFace.NORTH)
						block_location = starting_point.clone().add(new Location(player.getWorld(), x, y, -z));
					else if (central_block_orientation == BlockFace.EAST)
						block_location = starting_point.clone().add(new Location(player.getWorld(), z, y, x));
					else if (central_block_orientation == BlockFace.WEST)
						block_location = starting_point.clone().add(new Location(player.getWorld(), -z, y, x));
					
					// Get the block itself, then generate ItemInfo and BlockInfo
					Block block = player.getWorld().getBlockAt(block_location);
					WorldMixedItemStack world_block = new WorldMixedItemStack(block);
					AbstractMixedItemStack abstract_block = abstract_array_x.get(z);
					
					// If the material is supposed to be air, don't bother checking it - it essentially isn't part of the multiblock
					if (abstract_block.containsMaterial(Material.AIR))
						continue;
					
					// If not, add the block/item pair to the map
					world_to_abstract_map.put(world_block, abstract_block);
				}
			}
		}
		
		// Return the final map
		return world_to_abstract_map;
	}
}
