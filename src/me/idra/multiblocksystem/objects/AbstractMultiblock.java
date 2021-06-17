package me.idra.multiblocksystem.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.idra.multiblocksystem.helpers.ConfigHelper;
import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.ItemStackHelper;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.StringConversion;
import me.idra.multiblocksystem.managers.ManagerPlugin;



public class AbstractMultiblock {

	File multiblock_folder;
	File multiblock_file;
	File structure_file;
	FileConfiguration multiblock_config;
	
	public String name;
	public String description;
	public StructureDescriptor structure;

	public String fuel_name;
	public int max_fuel;
	
	public List<String> inventory_tags = new ArrayList<> ();
	public List<String> fuel_tags = new ArrayList<> ();
	public List<String> energy_tags = new ArrayList<> ();
	public Map<String, List<ItemStack>> position_tags = new HashMap<> ();

	public List<String> fuel_inventories = new ArrayList<> ();

	public Map<String, Object> variables = new HashMap<> ();
	public List<MultiblockRecipe> recipes = new ArrayList<> ();
	public List<MultiblockFuel> fuels = new ArrayList<> ();

	public Class<?> structure_class;
	
	
	
	public AbstractMultiblock(String name) {
		this.name = name;

		//File handling
		// Open multiblock files
		multiblock_folder = new File(new File(ManagerPlugin.plugin.getDataFolder(), "multiblocks"), name);
		multiblock_file = new File(multiblock_folder, "settings.yml");
		structure_file = new File(multiblock_folder, "structure.yml");

		multiblock_config = YamlConfiguration.loadConfiguration(multiblock_file);

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

		// Load multiblock structure
		loadStructure();

		// Get class location, and from that the class itself
		// Don't need to check if the files exist because that's already been done in loadStructure()
		String classname = multiblock_config.getString("ClassName");

		if (classname == null) {
			Logger.configError(Logger.OPTION_NOT_FOUND, multiblock_file, null, "ClassName");
			return;
		}

		String class_location = "me.idra.multiblocksystem.multiblocks." + classname.toUpperCase();
		
		// If the class isn't found, throw a warning
		try {
			structure_class = Class.forName(class_location);
		} catch (ClassNotFoundException e) {
			Logger.log(
					Logger.getWarning("class-not-found")
					.replace("%class%", String.valueOf(class_location)),
					true);
		}
	}
	
	

	public void loadStructure() {
		
		
		
		/*
		 * CONFIG LOADING
		 */
		
		// Get parameters from config
		description = multiblock_config.getString("Description");
		fuel_name = multiblock_config.getString("FuelName");
		max_fuel = multiblock_config.getInt("MaxFuel");
		
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

				List<ItemStack> items = new ArrayList<> ();
				
				// For every position tag, get the items the tag can be, and add them to an array
				for (String item : position_tag_config.getStringList(tag)) {
					
					ItemStack stack = ItemStackHelper.itemStackFromID(multiblock_file, position_tag_config, item);

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
		if ((fuel_config != null) && (!fuel_tag_list.isEmpty())) {

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
				ItemStack stack = ItemStackHelper.itemStackFromID(multiblock_file, fuel_config, id);

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
				Map<String, Map<ItemStack, Integer>> inputs = null;
				Map<String, Map<ItemStack, Integer>> outputs = null;

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

		List<List<List<ItemGroup>>> block_array = new ArrayList<> ();	// y -> x -> z -> block
		
		ConfigurationSection structure_section = YamlConfiguration.loadConfiguration(structure_file);

		for (String name_y : structure_section.getKeys(false)) {

			ConfigurationSection section_y = structure_section.getConfigurationSection(name_y);
			List<List<ItemGroup>> block_array_y = new ArrayList<> ();

			for (String name_x : section_y.getKeys(false)) {

				ConfigurationSection section_x = section_y.getConfigurationSection(name_x);
				List<ItemGroup> block_array_x = new ArrayList<> ();

				for (String name_z : section_x.getKeys(false)) {

					String string_z = section_x.getString(name_z);
					block_array_x.add(StringConversion.groupFromName(structure_file, section_x, string_z));
				}

				block_array_y.add(block_array_x);
			}

			block_array.add(block_array_y);
		}
		
		// Use this to create a new structure descriptor
		structure = new StructureDescriptor(name, block_array);
	}



	/*
	 * STATIC METHODS
	 */
		
	
	public static Map<Block, ItemGroup> getStructureFromStartingPoint(
			Player player,
			Location central_block_location,
			BlockFace central_block_orientation, 
			StructureDescriptor abstract_descriptor) {
		
		// Initialize empty map that will store every block and its corresponding group
		Map<Block, ItemGroup> block_to_group_map = new LinkedHashMap<> ();
		
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
			List<List<ItemGroup>> abstract_array_y = abstract_descriptor.groups.get(y);
			
			for (int x = 0; x < abstract_descriptor.dimension.getBlockX(); x++) {
				List<ItemGroup> abstract_array_x = abstract_array_y.get(x);
				
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
					ItemGroup group = abstract_array_x.get(z);
					
					// If the material is supposed to be air, don't bother checking it - it essentially isn't part of the multiblock
					if (group.containsMaterial(Material.AIR)) {
						continue;
					}
					
					// If not, add the block/item pair to the map
					block_to_group_map.put(block, group);
				}
			}
		}
		
		// Return the final map
		return block_to_group_map;
	}
}
