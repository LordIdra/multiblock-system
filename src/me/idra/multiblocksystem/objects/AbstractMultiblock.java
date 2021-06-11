package me.idra.multiblocksystem.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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

import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.StringConversion;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;



public class AbstractMultiblock {
	
	public String name;
	public String description;
	public StructureDescriptor structure;
	
	public List<String> tags = new ArrayList<> ();
	public Map<String, Object> variables = new HashMap<> ();
	public List<MultiblockRecipe> recipes = new ArrayList<> ();
	public List<MultiblockFuel> fuels = new ArrayList<> ();
	
	
	
	public AbstractMultiblock(String name) {
		this.name = name;
		loadStructure();
	}
	
	

	public void loadStructure() {
		
		/*
		 * file handling
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
		 * config handling
		 */
		
		// Get config from multiblock file
		FileConfiguration multiblock_config = YamlConfiguration.loadConfiguration(multiblock_file);
		
		// Get parameters from said config
		description = multiblock_config.getString("Description");
		
		// Get certain sections we need to read
		List<String> tag_config = multiblock_config.getStringList("RequiredTags");
		ConfigurationSection variable_config = multiblock_config.getConfigurationSection("Variables");
		ConfigurationSection fuel_config = multiblock_config.getConfigurationSection("Fuels");
		ConfigurationSection recipe_config = multiblock_config.getConfigurationSection("Recipes");
		
		// Get required tags
		if (tag_config != null)
			for (String tag : tag_config)
				tags.add(tag);
		
		// Get variables
		if (variable_config != null)
			for (String variable_name : variable_config.getKeys(false))
				variables.put(variable_name, variable_config.get(variable_name));
		
		// Load fuels
		if (fuel_config != null) {
			for (String key : fuel_config.getKeys(false)) {
				
				ConfigurationSection current_fuel = fuel_config.getConfigurationSection(key);
				
				fuels.add(new MultiblockFuel(fuelMixedItemStackFromConfigSection(multiblock_file, current_fuel), current_fuel.getInt("time")));
			}
		}
		
		// Load recipes
		if (recipe_config != null) {
			
			for (String key : recipe_config.getKeys(false)) {
				
				// Get specific config sections
				ConfigurationSection current_recipe = recipe_config.getConfigurationSection(key);
				ConfigurationSection config_item_in = current_recipe.getConfigurationSection("ItemIn");
				ConfigurationSection config_item_out = current_recipe.getConfigurationSection("ItemOut");
				
				// ItemIn
				List<MixedItemStack> item_in = new ArrayList<> ();

				for (String item : config_item_in.getKeys(false))
					item_in.add(recipeMixedItemStackFromConfigSection(multiblock_file, config_item_in.getConfigurationSection(item)));
					
				// ItemOut
				List<MixedItemStack> item_out = new ArrayList<> ();

				for (String item : config_item_out.getKeys(false))
					item_out.add(recipeMixedItemStackFromConfigSection(multiblock_file, config_item_out.getConfigurationSection(item)));
				
				// Energy + time
				int energy_in = current_recipe.getInt("EnergyIn");
				int energy_out = current_recipe.getInt("EnergyOut");
				int time = current_recipe.getInt("Time");
				
				// Generate final recipe
				recipes.add(new MultiblockRecipe(item_in, item_out, energy_in, energy_out, time));
			}
		}

		
		/*
		 * structure handling
		 */

		// Dimensions
		int dimension_x = 0;
		int dimension_z = 0;

		// Generate empty array which will store the block data strings for each layer
		List<List<String>> structure_array = new ArrayList<> ();

		try {
			try (Scanner structure_file_scanner = new Scanner(structure_file).useDelimiter("\\s+")) {
					
				// Get dimension X
				if (!structure_file_scanner.next().toUpperCase().equals("DIMENSION-X:")) {
					Logger.configError(Logger.OPTION_NOT_FOUND, structure_file, null, "DIMENSION-X");
					return;
					
				} else {
					dimension_x = structure_file_scanner.nextInt();
				}
				
				// Get dimension Z
				if (!structure_file_scanner.next().toUpperCase().equals("DIMENSION-Z:")) {
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
						structure_array.add(new ArrayList<String>());
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
			
		// Convert the data from the structure arrays to item-infos
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
		
	private MixedItemStack recipeMixedItemStackFromConfigSection(File recipe_file, ConfigurationSection recipe_config_section) {
		
		// Get attributes from config section
		String type = recipe_config_section.getString("type").toUpperCase(); 
		String id = recipe_config_section.getString("id").toUpperCase(); 
		int amount = recipe_config_section.getInt("amount");
		
		if (type.equals("NORMAL")) {
			
			// Convert ID to material
			Material material = StringConversion.idToMaterial(id);
			
			if (material == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, recipe_file, recipe_config_section, "id");
				return null;
			}
			
			// Convert attributes to MixedItemStack
			return new MixedItemStack(material, amount);
			
					
		} else if (type.equals("SLIMEFUN")) {
			
			// Convert ID to slimefun item
			SlimefunItem slimefun_item = StringConversion.idToSlimefunItem(id);
			
			if (slimefun_item == null) {
				Logger.configError(Logger.OPTION_INVALID, recipe_file, recipe_config_section, "id");
				return null;
			}
						
			// Convert attributes to MixedItemStack
			return new MixedItemStack(slimefun_item, amount);
			
			
		} else {
			Logger.configError(Logger.OPTION_INVALID, recipe_file, recipe_config_section, "type");
			return null;
		}
	}
	
	private MixedItemStack fuelMixedItemStackFromConfigSection(File file, ConfigurationSection fuel_config_section) {
		
		// Get attributes from config section
		String type = fuel_config_section.getString("type").toUpperCase(); 
		String id = fuel_config_section.getString("id").toUpperCase(); 
		
		if (type.equals("NORMAL")) {
			
			// Convert ID to material
			Material material = StringConversion.idToMaterial(id);
			
			if (material == null) {
				Logger.configError(Logger.OPTION_INVALID, file, fuel_config_section, "id");
				return null;
			}
			
			// Convert attributes to MixedItemStack
			return new MixedItemStack(material, 1);
			
					
		} else if (type.equals("SLIMEFUN")) {
			
			// Convert ID to slimefun item
			SlimefunItem slimefun_item = StringConversion.idToSlimefunItem(id);
			
			if (slimefun_item == null) {
				Logger.configError(Logger.OPTION_INVALID, file, fuel_config_section, "id");
				return null;
			}
						
			// Convert attributes to MixedItemStack
			return new MixedItemStack(slimefun_item, 1);
			
			
		} else {
			Logger.log(Logger.getWarning("invalid-itemstack-type")
					   .replace(ConstantPlaceholders.MULTIBLOCK, name),
					   true);
			return null;
		}
	}
		
	
	public static Map<WorldMixedItemStack, AbstractMixedItemStack> getStructureFromStartingPoint(
			Player player,
			Location central_block_location,
			BlockFace central_block_orientation, 
			StructureDescriptor abstract_descriptor) {
		
		// Initialize empty map that will store every BlockInfo and its corresponding ItemInfo
		Map<WorldMixedItemStack, AbstractMixedItemStack> world_to_abstract_map = new HashMap<> ();
		
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
