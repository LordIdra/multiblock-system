package me.idra.multiblocksystem.lists;



import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.helpers.StringConversion;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMixedItemStack;
import me.idra.multiblocksystem.objects.MixedItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ListMaterialGroups {

	public static final String ITEM_GROUPS = "itemGroups";
	
	private ListMaterialGroups() {
		// Empty constructor
	}


	private static Map<String, AbstractMixedItemStack> material_groups = new HashMap<> ();



	public static void initialize() {

		// Check config file exists
		File item_group_file = new File(ManagerPlugin.plugin.getDataFolder(), "item-groups.yml");

		if (item_group_file.exists()) {
			Logger.fileNotFoundError(item_group_file);
			return;
		}

		// Verify config is valid
		FileConfiguration item_group_config = YamlConfiguration.loadConfiguration(item_group_file);
		ConfigurationSection config_section = item_group_config.getConfigurationSection(ITEM_GROUPS);

		if (config_section == null) {
			item_group_config.createSection(ITEM_GROUPS);
			config_section = item_group_config.getConfigurationSection(ITEM_GROUPS);
		}

		// Get each group and add it to the map
		for (String group_name : item_group_config.getKeys(false)) {

			List<String> group_item_names = item_group_config.getStringList(group_name);
			List<MixedItemStack> group_item_stacks = new ArrayList<> ();

			// Convert the item names to MixedItemStacks
			for (String name : group_item_names) {

				MixedItemStack stack = StringConversion.mixedItemStackFromID(item_group_file, item_group_config, name);

				if (stack == null) {
					Logger.configError(Logger.OPTION_INVALID, item_group_file, item_group_config, name);
					continue;
				}

				group_item_stacks.add(stack);
			}

			// Initialize new stack and add it to the map
			// Tag will be handled later
			AbstractMixedItemStack group_item = new AbstractMixedItemStack(group_item_stacks, null, group_name);

			material_groups.put(group_name, group_item);
		}
	}
}