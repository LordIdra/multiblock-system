package me.idra.multiblocksystem.objects;

import me.idra.multiblocksystem.helpers.*;
import me.idra.multiblocksystem.managers.ManagerPlugin;
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

import java.io.File;
import java.util.*;


public class AbstractMultiblock {
	File multiblock_folder;
	File multiblock_file;
	File structure_file;
	FileConfiguration multiblock_config;

	public String name_of_structure_block;
	public String multiblock_structure_description;
	public StructureDescriptor structure; // Structural Description of Block Arrangement (This comes from the Structure.yml files)

	public String fuel_display_name;

	public Map<String, Object> variables = new HashMap<>();
	public List<MultiblockRecipe> recipes = new ArrayList<>();
	public List<MultiblockFuel> fuels = new ArrayList<>();

	public Class<?> structure_class;


	public AbstractMultiblock(String name_of_structure_block) {
		this.name_of_structure_block = name_of_structure_block;

		// File handling
		// Open multiblock files
		multiblock_folder = new File(new File(ManagerPlugin.plugin.getDataFolder(), "multiblocks"), name_of_structure_block);
		multiblock_file = new File(multiblock_folder, "settings.yml");
		structure_file = new File(multiblock_folder, "structure.yml");

		multiblock_config = YamlConfiguration.loadConfiguration(multiblock_file);

		// Check the files actually exist
		if (!multiblock_file.exists()) {
			Logger.log(
					Logger.getWarning("multiblock-file-not-found")
							.replace(ConstantPlaceholders.NAME, String.valueOf(name_of_structure_block)),
					true);
			return;
		}

		if (!structure_file.exists()) {
			Logger.log(
					Logger.getWarning("structure-file-not-found")
							.replace(ConstantPlaceholders.NAME, String.valueOf(name_of_structure_block)),
					true);
			return;
		}

		// Load multiblock structure
		loadStructure();

		// Get class location, and from that the class itself
		// Don't need to check if the files exist because that's already been done in loadStructure()
		String class_name = multiblock_config.getString("ClassName");

		if (class_name == null || class_name.isBlank()) {
			Logger.configError(Logger.OPTION_NOT_FOUND, multiblock_file, null, "ClassName");
			return;
		}

		String class_location = "me.idra.multiblocksystem.multiblocks." + class_name.toUpperCase();

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

		// CONFIG LOADING

		// Get parameters from config
		multiblock_structure_description = multiblock_config.getString("Description");
		fuel_display_name = multiblock_config.getString("FuelName");


		ConfigurationSection variable_config = multiblock_config.getConfigurationSection("Variables");
		ConfigurationSection fuel_config = multiblock_config.getConfigurationSection("Fuels");
		ConfigurationSection recipe_config = multiblock_config.getConfigurationSection("Recipes");


		// Get variables
		if (variable_config != null) {
			for (String variable_name : variable_config.getKeys(false)) {
				variables.put(variable_name, variable_config.get(variable_name));
			}
		}


		// Load fuels
		if (fuel_config != null) {
			loadFuel(fuel_config);
		}

		// Load recipes
		if (recipe_config != null) {
			loadRecipes(recipe_config);
		}

		// STRUCTURE LOADING

		List<List<List<ItemGroup>>> block_array = new ArrayList<>();    // y -> x -> z -> block

		ConfigurationSection structure_section = YamlConfiguration.loadConfiguration(structure_file);

		for (String name_y : structure_section.getKeys(false)) {

			ConfigurationSection section_y = structure_section.getConfigurationSection(name_y);
			List<List<ItemGroup>> block_array_y = new ArrayList<>();

			for (String name_x : section_y.getKeys(false)) {

				ConfigurationSection section_x = section_y.getConfigurationSection(name_x);
				List<ItemGroup> block_array_x = new ArrayList<>();

				for (String name_z : section_x.getKeys(false)) {

					String string_z = section_x.getString(name_z);
					block_array_x.add(StringConversion.groupFromName(structure_file, section_x, string_z));
				}

				block_array_y.add(block_array_x);
			}

			block_array.add(block_array_y);
		}

		// Use this to create a new structure descriptor
		structure = new StructureDescriptor(name_of_structure_block, block_array);
	}


	private void loadFuel(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {

			// Seperate string into time and ID
			String fuel_and_ticks = config.getString(key);
			assert fuel_and_ticks != null;
			String[] split_string = fuel_and_ticks.split("\\s"); // Time, Fuel

			if (split_string.length == 2) {
				int time = Integer.parseInt(split_string[0]);
				String id = split_string[1];

				// Create MixedItemStack
				ItemStack stack = ItemStackHelper.itemStackFromID(multiblock_file, config, id);

				if (stack != null) { // Finally, create the fuel
					fuels.add(new MultiblockFuel(stack, time));
				} else {
					Logger.configError(Logger.OPTION_INVALID, multiblock_file, config, key);
				}

			} else {
				Logger.configError(Logger.OPTION_INVALID, multiblock_file, config, key);
			}

		}
	}

	private void loadRecipes(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {

			// Get config sections
			ConfigurationSection current_recipe = config.getConfigurationSection(key);
			ConfigurationSection config_inputs, config_outputs;
			if (current_recipe != null) {
				config_inputs = current_recipe.getConfigurationSection("INPUT");
				config_outputs = current_recipe.getConfigurationSection("OUTPUT");
			} else {
				return;
			}

			// Initialize variables
			int time = current_recipe.getInt("TIME");
			int energy = current_recipe.getInt("ENERGY");
			List<ItemStack> inputs;
			List<ItemStack> outputs;

			// Check recipe time is valid
			if (time <= 0) {
				Logger.configError(Logger.OPTION_INVALID, multiblock_file, current_recipe, "TIME");
			}

			inputs = null;
			outputs = null;

			if (config_inputs == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, multiblock_file, current_recipe, "INPUTS");

			} else if (config_outputs == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, multiblock_file, current_recipe, "OUTPUTS");

			} else {
				inputs = ConfigHelper.getItemStackList(multiblock_file, config_inputs);
				outputs = ConfigHelper.getItemStackList(multiblock_file, config_outputs);
			}

			// Combine all the above into a new recipe
			recipes.add(new MultiblockRecipe(inputs, outputs, energy, time));
		}
	}

	// STATIC METHODS BELOW

	public static Map<Block, ItemGroup> getStructureFromStartingPoint(
			Player player,
			Location central_block_location,
			BlockFace central_block_orientation,
			StructureDescriptor abstract_descriptor) {

		// Initialize empty map that will store every block and its corresponding group
		Map<Block, ItemGroup> block_to_group_map = new LinkedHashMap<>();

		// Figure out block locations
		Location starting_point = central_block_location.clone();
		Vector relative_corner_location = abstract_descriptor.central_block.clone();
		Vector relative_offset = abstract_descriptor.structure_dimensions.clone();
		Location end_point;

		// Account for orientation using a huge if-else block
		switch (central_block_orientation) {
			case SOUTH -> {
				starting_point.subtract(relative_corner_location);
				end_point = starting_point.clone();
				end_point.subtract(relative_offset);
			}
			case NORTH -> {
				relative_corner_location.setZ(-abstract_descriptor.central_block.getBlockZ());
				starting_point.subtract(relative_corner_location);
				end_point = starting_point.clone();
				relative_offset.setZ(-starting_point.getBlockZ());
				end_point.subtract(relative_offset);
			}
			case EAST -> {
				int temp_x = relative_corner_location.getBlockX();
				relative_corner_location.setX(relative_corner_location.getBlockZ());
				relative_corner_location.setZ(temp_x);
				starting_point.subtract(relative_corner_location);
				end_point = starting_point.clone();
				temp_x = relative_offset.getBlockX();
				relative_offset.setX(relative_offset.getBlockZ());
				relative_offset.setZ(temp_x);
				end_point.subtract(relative_offset);
			}
			case WEST -> {
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
		}


		// Enter block checking loop
		for (int y = 0; y < abstract_descriptor.structure_dimensions.getBlockY(); y++) {
			List<List<ItemGroup>> abstract_array_y = abstract_descriptor.groups.get(y);

			for (int x = 0; x < abstract_descriptor.structure_dimensions.getBlockX(); x++) {
				List<ItemGroup> abstract_array_x = abstract_array_y.get(x);

				for (int z = 0; z < abstract_descriptor.structure_dimensions.getBlockZ(); z++) {

					// Get the block at the XYZ relative to the predetermined starting point
					Location block_location = switch (central_block_orientation) {
						case SOUTH -> starting_point.clone().add(new Location(player.getWorld(), x, y, z));
						case NORTH -> starting_point.clone().add(new Location(player.getWorld(), x, y, -z));
						case EAST -> starting_point.clone().add(new Location(player.getWorld(), z, y, x));
						case WEST -> starting_point.clone().add(new Location(player.getWorld(), -z, y, x));
						default -> null;
					};

					// Get the block itself, then generate ItemInfo and BlockInfo
					Block block = player.getWorld().getBlockAt(block_location);
					ItemGroup group = abstract_array_x.get(z);

					// If the material is supposed to be air, don't bother checking it - it essentially isn't part of the multiblock
					if (!group.containsMaterial(Material.AIR)) {// If not, add the block/item pair to the map
						block_to_group_map.put(block, group);
					}

				}
			}
		}

		// Return the final map
		return block_to_group_map;
	}
}
