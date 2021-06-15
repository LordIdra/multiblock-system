package me.idra.multiblocksystem.lists;



import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMixedItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ListMaterialGroups {

	private ListMaterialGroups() {
		// Empty constructor
	}



	Map<String, AbstractMixedItemStack> material_groups = new HashMap<> ();



	public void initialize() {

		// Check config file exists
		File item_group_file = new File(ManagerPlugin.plugin.getDataFolder(), "item-groups.yml");

		if (item_group_file.exists()) {
			try {
				item_group_file.createNewFile();
			} catch (IOException e) {
				// This should never happen
			}
		}

		// Verify config is valid
		FileConfiguration item_group_config = YamlConfiguration.loadConfiguration(item_group_file);
		ConfigurationSection config_section = item_group_config.getConfigurationSection("ItemGroups");

		if (config_section == null) {
			item_group_config.createSection("ItemGroups");
			config_section = item_group_config.getConfigurationSection("ItemGroups");
		}

		// Get each group and add it to the map
		for (String group_name : item_group_config.getKeys(false)) {

			List<String> group_item_names = item_group_config.getStringList(group_name);

			// Convert the item names to MixedItemStacks
			for (String name : group_item_names) {
				
			}

			// Initialize new stack and add it to the map
			AbstractMixedItemStack group_item = new AbstractMixedItemStack();
		}
	}
}
