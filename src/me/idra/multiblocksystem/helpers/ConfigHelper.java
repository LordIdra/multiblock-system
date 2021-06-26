package me.idra.multiblocksystem.helpers;

import me.idra.multiblocksystem.managers.ManagerPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;


public class ConfigHelper {

	public static final String ITEM_GROUPS = "ItemGroups";

	private ConfigHelper() {
	}

	public static List<ItemStack> getItemStackList(File file, ConfigurationSection config_outputs) {
		List<ItemStack> outputs = new ArrayList<>();
		Set<String> output_item_keys = config_outputs.getKeys(false);

		for (String item_key : output_item_keys) {
			String output_amount_and_item = config_outputs.getString(item_key);

			if (output_amount_and_item == null || output_amount_and_item.isBlank()) {
				Logger.configError(Logger.OPTION_INVALID, file, config_outputs, item_key + "." + output_amount_and_item);
				return new ArrayList<>();
			}

			String[] split_string = output_amount_and_item.split("\\s");

			if (split_string.length == 2) {
				String id = split_string[1];
				ItemStack stack = ItemStackHelper.itemStackFromID(file, config_outputs, id);

				if (stack != null) {
					outputs.add(stack);
				} else {
					Logger.configError(Logger.OPTION_INVALID, file, config_outputs, item_key + "." + output_amount_and_item);
				}

			} else {
				Logger.configError(Logger.OPTION_INVALID, file, config_outputs, item_key);
			}
		}
		return outputs;
	}

	public static List<ItemStack> loadGroup(String name) {

		// Load file
		File group_file = new File(ManagerPlugin.plugin.getDataFolder(), "itemgroups.yml");

		if (!group_file.exists()) {
			Logger.fileNotFoundError(group_file);
			return new ArrayList<>();
		}

		// Load config
		FileConfiguration group_config = YamlConfiguration.loadConfiguration(group_file);
		ConfigurationSection group_section = group_config.getConfigurationSection(ITEM_GROUPS);
		Logger.log(name, true);
		List<String> item_names = group_section.getStringList(name.toLowerCase());

		// Convert names to MixedItemStacks
		List<ItemStack> item_stacks = new ArrayList<>();

		for (String item_name : item_names) {
			item_stacks.add(ItemStackHelper.itemStackFromID(group_file, group_section, item_name));
		}

		return item_stacks;
	}
}
