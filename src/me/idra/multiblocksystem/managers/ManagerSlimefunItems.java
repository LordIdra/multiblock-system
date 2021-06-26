package me.idra.multiblocksystem.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.researching.Research;
import me.idra.multiblocksystem.helpers.ItemStackHelper;
import me.idra.multiblocksystem.helpers.Logger;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;



public class ManagerSlimefunItems {

	private ManagerSlimefunItems() {
		// Empty constructor
	}

	public static final String ITEM_FILE_NAME = "items.yml";
	public static final String RESEARCH_FILE_NAME = "researches.yml";

	public static final Map<String, RecipeType> RECIPE_TYPES = new HashMap<> ();


	public static void initialize() {

		RECIPE_TYPES.put("ANCIENT_ALTAR", RecipeType.ANCIENT_ALTAR);
		RECIPE_TYPES.put("ARMOR_FORGE", RecipeType.ARMOR_FORGE);
		RECIPE_TYPES.put("BARTER_DROP", RecipeType.BARTER_DROP);
		RECIPE_TYPES.put("COMPRESSOR", RecipeType.COMPRESSOR);
		RECIPE_TYPES.put("ENHANCED_CRAFTING_TABLE", RecipeType.ENHANCED_CRAFTING_TABLE);
		RECIPE_TYPES.put("FOOD_COMPOSTER", RecipeType.FOOD_COMPOSTER);
		RECIPE_TYPES.put("FOOD_FABRICATOR", RecipeType.FOOD_FABRICATOR);
		RECIPE_TYPES.put("FREEZER", RecipeType.FREEZER);
		RECIPE_TYPES.put("GEO_MINER", RecipeType.GEO_MINER);
		RECIPE_TYPES.put("GOLD_PAN", RecipeType.GOLD_PAN);
		RECIPE_TYPES.put("GRIND_STONE", RecipeType.GRIND_STONE);
		RECIPE_TYPES.put("HEATED_PRESSURE_CHAMBER", RecipeType.HEATED_PRESSURE_CHAMBER);
		RECIPE_TYPES.put("JUICER", RecipeType.JUICER);
		RECIPE_TYPES.put("MAGIC_WORKBENCH", RecipeType.MAGIC_WORKBENCH);
		RECIPE_TYPES.put("MOB_DROP", RecipeType.MOB_DROP);
		RECIPE_TYPES.put("MULTIBLOCK", RecipeType.MULTIBLOCK);
		RECIPE_TYPES.put("NUCLEAR_REACTOR", RecipeType.NUCLEAR_REACTOR);
		RECIPE_TYPES.put("NULL", RecipeType.NULL);
		RECIPE_TYPES.put("ORE_CRUSHER", RecipeType.ORE_CRUSHER);
		RECIPE_TYPES.put("ORE_WASHER", RecipeType.ORE_WASHER);
		RECIPE_TYPES.put("PRESSURE_CHAMBER", RecipeType.PRESSURE_CHAMBER);
		RECIPE_TYPES.put("REFINERY", RecipeType.REFINERY);
		RECIPE_TYPES.put("SMELTERY", RecipeType.SMELTERY);
		
		/*
		 * file handling
		 */

		// Check slimefun folder exists
		File slimefun_folder = new File(ManagerPlugin.plugin.getDataFolder(), "slimefun");

		if (!slimefun_folder.exists()) {
			slimefun_folder.mkdir();
		}

		// Check slimefun items/research files exist
		File slimefun_research_file = new File(slimefun_folder, RESEARCH_FILE_NAME);
		File slimefun_item_file = new File(slimefun_folder, ITEM_FILE_NAME);

		if (!slimefun_research_file.exists()) {
			Logger.fileNotFoundError(slimefun_research_file);
			return;
		}

		if (!slimefun_item_file.exists()) {
			Logger.fileNotFoundError(slimefun_item_file);
			return;
		}

		// Load configs
		FileConfiguration research_config = YamlConfiguration.loadConfiguration(slimefun_research_file);
		FileConfiguration item_config = YamlConfiguration.loadConfiguration(slimefun_item_file);


		/*
		 * category creation
		 */

		NamespacedKey category_id = new NamespacedKey(ManagerPlugin.plugin, "multiblock_items");
		CustomItem category_item = new CustomItem(Material.COMPARATOR, "&6Multiblock Items");
		Category multiblock_category = new Category(category_id, category_item);


		/*
		 * research creation
		 */

		Map<String, Research> researches = new HashMap<> ();
		
		 for (String key : research_config.getKeys(false)) {
			
			// Get variables
			ConfigurationSection research_section = research_config.getConfigurationSection(key);

			 assert research_section != null;
			 int number 	= research_section.getInt("number");
			int xp 		= research_section.getInt("xp");
			String name = research_section.getString("name");

			// Variable checking
			if (number == 0) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, research_section, "number");
				continue;
			}

			if (xp == 0) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, research_section, "xp");
				continue;
			}

			if (name == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, research_section, "name");
				continue;
			}

			// Generate research
			NamespacedKey research_key = new NamespacedKey(ManagerPlugin.plugin, key.toUpperCase());
			Research research = new Research(research_key, number, name, xp);

			researches.put(key.toUpperCase(), research);
		}
		

		/*
		 * item creation
		 */

		for (String key : item_config.getKeys(false)) {

			// Sections
			ConfigurationSection section = item_config.getConfigurationSection(key);
			assert section != null;

			ConfigurationSection display_item_section = section.getConfigurationSection("Item");
			ConfigurationSection recipe_section = section.getConfigurationSection("Recipe");

			if (display_item_section == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, section, null);
				continue;
			}

			if (recipe_section == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, section, null);
				continue;
			}

			// Variables
			String name 				= section.getString("Name");
			String display_item_type 	= display_item_section.getString("type");
			String display_item_id 		= display_item_section.getString("id");
			int amount 			 		= recipe_section.getInt("amount");
			RecipeType recipe_type		= RECIPE_TYPES.get(recipe_section.getString("type"));

			if (name == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, section, "Name");
				continue;
			}

			if (display_item_type == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, display_item_section, "type");
				continue;
			}

			if (display_item_id == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, display_item_section, "id");
				continue;
			}

			if (!display_item_type.equals("NORMAL") && !display_item_type.equals("HEAD")) {
				Logger.configError(Logger.OPTION_INVALID, slimefun_item_file, display_item_section, "type");
				continue;
			}

			if (amount == 0) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, recipe_section, "amount");
				continue;
			}

			if (recipe_type == null) {
				Logger.configError(Logger.OPTION_NOT_FOUND, slimefun_item_file, recipe_section, "type");
				continue;
			}

			display_item_type = display_item_type.toUpperCase();


			// Get recipe items + amount
			ItemStack[] recipe = new ItemStack[] {
				null, null, null,
				null, null, null,
				null, null, null
			};

			ConfigurationSection recipe_items_section = recipe_section.getConfigurationSection("items");

			if (recipe_items_section != null) {

				for (String recipe_key : recipe_items_section.getKeys(false)) {
				
					int index = Integer.parseInt(recipe_key);
					String id = recipe_items_section.getString(recipe_key);
	
					recipe[index] = ItemStackHelper.itemStackFromID(slimefun_item_file, recipe_items_section, id);
	
					if (recipe[index] == null) {
						Logger.configError(Logger.OPTION_INVALID, slimefun_item_file, recipe_items_section, recipe_key);
					}
				}
			}

			// Get lore
			String[] lore = section.getStringList("Lore").toArray(new String[0]);

			// Register item
			SlimefunItemStack slimefun_itemstack;
			if (display_item_type.equals("NORMAL")) {

				Material display_item_material = Material.getMaterial(display_item_id.toUpperCase());

				if (display_item_material == null) {
					Logger.configError(Logger.OPTION_INVALID, slimefun_item_file, display_item_section, "id");
					continue;
				}
				
				slimefun_itemstack = new SlimefunItemStack(
					key,
					display_item_material, 
					name, 
					lore);

			} else {

				slimefun_itemstack = new SlimefunItemStack(
					key,
					display_item_id, 
					name, 
					lore);
			}

			SlimefunItem slimefun_item = new SlimefunItem(multiblock_category, slimefun_itemstack, recipe_type, recipe);
			slimefun_item.register(ManagerPlugin.plugin);

			String research_name = section.getString("Research");
			
			if (research_name != null) {

				Research research = researches.get(research_name.toUpperCase());

				if (research == null) {
					Logger.configError(Logger.OPTION_INVALID, slimefun_item_file, section, "Research");
					continue;
				}

				research.addItems(slimefun_itemstack);
			}
		}


		/*
		 * research registration
		 */

		for (Research research : researches.values()) {
			research.register();
		}
	}
}
