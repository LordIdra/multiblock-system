package me.idra.multiblocksystem.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.researching.Research;
import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.StringConversion;
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

	public static final Map<String, RecipeType> RECIPE_TYPES = new HashMap<> () {{

		put("ANCIENT_ALTAR", RecipeType.ANCIENT_ALTAR);
		put("ARMOR_FORGE", RecipeType.ARMOR_FORGE);
		put("BARTER_DROP", RecipeType.BARTER_DROP);
		put("COMPRESSOR", RecipeType.COMPRESSOR);
		put("ENHANCED_CRAFTING_TABLE", RecipeType.ENHANCED_CRAFTING_TABLE);
		put("FOOD_COMPOSTER", RecipeType.FOOD_COMPOSTER);
		put("FOOD_FABRICATOR", RecipeType.FOOD_FABRICATOR);
		put("FREEZER", RecipeType.FREEZER);
		put("GEO_MINER", RecipeType.GEO_MINER);
		put("GOLD_PAN", RecipeType.GOLD_PAN);
		put("GRIND_STONE", RecipeType.GRIND_STONE);
		put("HEATED_PRESSURE_CHAMBER", RecipeType.HEATED_PRESSURE_CHAMBER);
		put("JUICER", RecipeType.JUICER);
		put("MAGIC_WORKBENCH", RecipeType.MAGIC_WORKBENCH);
		put("MOB_DROP", RecipeType.MOB_DROP);
		put("MULTIBLOCK", RecipeType.MULTIBLOCK);
		put("NUCLEAR_REACTOR", RecipeType.NUCLEAR_REACTOR);
		put("NULL", RecipeType.NULL);
		put("ORE_CRUSHER", RecipeType.ORE_CRUSHER);
		put("ORE_WASHER", RecipeType.ORE_WASHER);
		put("PRESSURE_CHAMBER", RecipeType.PRESSURE_CHAMBER);
		put("REFINERY", RecipeType.REFINERY);
		put("SMELTERY", RecipeType.SMELTERY);
	}};


	public static void initialize() {

		/*
		 * file handling
		 */

		// Check slimefun folder exists
		File slimefun_folder = new File(ManagerPlugin.plugin.getDataFolder(), "slimefun");

		if (!slimefun_folder.exists()) {
			slimefun_folder.mkdir();
		}

		// Check slimefun items/research files exist
		File slimefun_research_file = new File(slimefun_folder, "researches.yml");
		File slimefun_item_file = new File(slimefun_folder, "items.yml");

		if (!slimefun_research_file.exists()) {
			try {
				slimefun_research_file.createNewFile();
			} catch (IOException e) {
				// This should never happen - if it does, we have much bigger problems to worry about
			}
		}

		if (!slimefun_item_file.exists()) {
			try {
				slimefun_item_file.createNewFile();
			} catch (IOException e) {
				// This should never happen - if it does, we have much bigger problems to worry about
			}
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

			int number = research_section.getInt("number");
			int xp = research_section.getInt("xp");
			String name = research_section.getString("name");

			// Variable checking
			if (number == 0) 		researchOptionNotFound(key, "number");
			if (xp == 0) 		researchOptionNotFound(key, "xp");
			if (name == null) 	researchOptionNotFound(key, "name");
			
			// Generate research
			NamespacedKey research_key = new NamespacedKey(ManagerPlugin.plugin, key);
			Research research = new Research(research_key, number, name, xp);

			researches.put(key, research);
		}
		

		/*
		 * item creation
		 */

		for (String key : item_config.getKeys(false)) {

			// Sections
			ConfigurationSection section = item_config.getConfigurationSection(key);
			ConfigurationSection display_item_section = section.getConfigurationSection("item");
			ConfigurationSection recipe_section = section.getConfigurationSection("recipe");

			if (display_item_section == null)	itemOptionNotFound(key, "item");
			if (recipe_section == null)			itemOptionNotFound(key, "recipe");

			ConfigurationSection recipe_items_section = recipe_section.getConfigurationSection("items");

			if (recipe_items_section == null)	itemOptionNotFound(key, "items");

			// Variables
			String name = section.getString("name");
			
			String display_item_type = display_item_section.getString("type").toUpperCase();
			String display_item_id = display_item_section.getString("id");

			int amount = recipe_section.getInt("amount");

			RecipeType recipe_type = RECIPE_TYPES.get(recipe_section.getString("type"));

			if (name == null)							itemOptionNotFound(key, "name");
			if (display_item_id == null)				itemOptionNotFound(key, "id");
			if (display_item_type == null
				|| (!display_item_type.equals("NORMAL")
				&&  !display_item_type.equals("HEAD")))	itemOptionNotFound(key, "type");
			if (amount == 0) 							itemOptionNotFound(key, "amount");


			// Get recipe items + amount
			ItemStack[] recipe = new ItemStack[] {
				null, null, null,
				null, null, null,
				null, null, null
			};

			for (String recipe_key : recipe_items_section.getKeys(false)) {
				
				int index = Integer.valueOf(recipe_key);
				String id = recipe_items_section.getString(recipe_key);

				recipe[index] = StringConversion.itemStackFromString(id);

				if (recipe[index] == null) {
					Logger.configError(Logger.OPTION_INVALID, slimefun_item_file, recipe_items_section, recipe_key);
				}
			}

			// Get lore
			String[] lore = (String[]) section.getStringList("lore").toArray(new String[section.getStringList("lore").size()]);

			// Register item
			SlimefunItemStack slimefun_itemstack = null;
			if (display_item_type.equals("NORMAL")) {
				
				slimefun_itemstack = new SlimefunItemStack(
					key,
					Material.getMaterial(display_item_id.toUpperCase()), 
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
		}


		/*
		 * research registration
		 */

		for (Research research : researches.values()) {
			research.register();
		}
	}



	private static void researchOptionNotFound(String name, String type) {

		Logger.log(
			Logger.getWarning("config-option-not-found")
			.replace(ConstantPlaceholders.TYPE, type)
			.replace(ConstantPlaceholders.NAME, name)
			.replace(ConstantPlaceholders.FILE, RESEARCH_FILE_NAME),
			true);
	}

	private static void itemOptionNotFound(String name, String type) {

		Logger.log(
			Logger.getWarning("config-option-not-found")
			.replace(ConstantPlaceholders.TYPE, type)
			.replace(ConstantPlaceholders.NAME, name)
			.replace(ConstantPlaceholders.FILE, ITEM_FILE_NAME),
			true);
	}
}
