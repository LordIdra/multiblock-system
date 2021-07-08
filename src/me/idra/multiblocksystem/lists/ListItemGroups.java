package me.idra.multiblocksystem.lists;



import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.idra.multiblocksystem.helpers.ConfigHelper;
import me.idra.multiblocksystem.helpers.ItemStackHelper;
import me.idra.multiblocksystem.helpers.Logger;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.ItemGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ListItemGroups {

	public static final Map<String, ItemGroup> material_groups = new HashMap<> ();

	private ListItemGroups() {
		// Empty constructor
	}

	public static void initialize() {

		// Check config file exists
		File item_group_file = new File(ManagerPlugin.plugin.getDataFolder(), "item-groups.yml");

		if (item_group_file.exists()) {
			Logger.fileNotFoundError(item_group_file);
			return;
		}

		// Verify config is valid
		FileConfiguration item_group_config = YamlConfiguration.loadConfiguration(item_group_file);
		ConfigurationSection config_section = item_group_config.getConfigurationSection(ConfigHelper.ITEM_GROUPS);

		if (config_section == null) {
			item_group_config.createSection(ConfigHelper.ITEM_GROUPS);
			config_section = item_group_config.getConfigurationSection(ConfigHelper.ITEM_GROUPS);
		}

		// Get each group and add it to the map
		assert config_section != null;
		for (String group_name : config_section.getKeys(false)) {

			List<String> group_item_names = config_section.getStringList(group_name);
			List<ItemStack> group_item_stacks = new ArrayList<> ();

			// Convert the item names to MixedItemStacks
			for (String name : group_item_names) {

				ItemStack stack = ItemStackHelper.itemStackFromID(item_group_file, item_group_config, name);

				if (stack == null) {
					Logger.configError(Logger.OPTION_INVALID, item_group_file, item_group_config, name);
					continue;
				}

				group_item_stacks.add(stack);
			}

			// Initialize new stack and add it to the map
			// Tag will be handled later
			ItemGroup group_item = new ItemGroup(group_name, group_item_stacks);

			material_groups.put(group_name, group_item);
		}
	}
}