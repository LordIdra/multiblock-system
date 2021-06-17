package me.idra.multiblocksystem.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.idra.multiblocksystem.managers.ManagerPlugin;



public class ConfigHelper {
	
	public static final String ITEM_GROUPS = "ItemGroups";
	
	private ConfigHelper() {}
    
    public static Map<String, Integer> getEnergyMap(File file, ConfigurationSection config_energy) {

        // Create new energy map
        Map<String, Integer> energy = new HashMap<> ();

        // Add each pair of tag to energy
        for (String energy_tag : config_energy.getKeys(false)) {

            int energy_value = config_energy.getInt(energy_tag);
            
            // Check energy value is valid
            if (energy_value == 0) {
                Logger.configError(Logger.OPTION_INVALID, file, config_energy, energy_tag);
                continue;
            }

            energy.put(energy_tag, energy_value);
        }

        return energy;
    }


    public static Map<String, Map<ItemStack, Integer>> getInputMap(File file, ConfigurationSection config_inputs) {

        // Create new input map
        Map<String, Map<ItemStack, Integer>> inputs = new HashMap<> ();

        // Add each pair of tag to input items
        for (String input_tag : config_inputs.getKeys(false)) {

            ConfigurationSection input_section = config_inputs.getConfigurationSection(input_tag);
            Set<String> input_keys = input_section.getKeys(false);
            
            // Check input value is valid
            if (input_keys == null) {
                continue;
            }

            Map<ItemStack, Integer> input_items = new HashMap<> ();

            // Convert each input value to a MixedItemStack
            for (String input_key : input_keys) {

                // Get value from key
                String input_value = input_section.getString(input_key);

                // Seperate string into amount and ID
                String[] split_string = input_value.split("\\s");

                if (split_string.length != 2) {
                    Logger.configError(Logger.OPTION_INVALID, file, config_inputs, input_value);
                    continue;
                }

                // Get amount and ID from the resulting two strings
                int amount = Integer.parseInt(split_string[0]);
                String id = split_string[1];
                ItemStack stack = ItemStackHelper.itemStackFromID(file, config_inputs, id);

                // Check the stack is valid
                if (stack == null) {
                    Logger.configError(Logger.OPTION_INVALID, file, config_inputs, input_key + "." + input_value);
                    continue;
                }

                // Add stack to the map
                input_items.put(stack, amount);
            }

            // Add the resulting list of stacks to the map
            inputs.put(input_tag, input_items);
        }

        return inputs;
    }



    public static Map<String, Map<ItemStack, Integer>> getOutputMap(File file, ConfigurationSection config_outputs) {

        // Create new output map
        Map<String, Map<ItemStack, Integer>> outputs = new HashMap<> ();

        // Add each pair of tag to output items
        for (String output_tag : config_outputs.getKeys(false)) {

            ConfigurationSection output_section = config_outputs.getConfigurationSection(output_tag);
            Set<String> output_keys = output_section.getKeys(false);
            
            // Check input value is valid
            if (output_keys == null) {
                continue;
            }

            Map<ItemStack, Integer> output_items = new HashMap<> ();

            // Convert each output value to a MixedItemStack
            for (String output_key : output_keys) {

                // Get value from key
                String output_value = output_section.getString(output_key);

                // Seperate string into amount and ID
                String[] split_string = output_value.split("\\s");

                if (split_string.length != 2) {
                    Logger.configError(Logger.OPTION_INVALID, file, config_outputs, output_key);
                    continue;
                }

                // Get amount and ID from the resulting two strings
                int amount = Integer.parseInt(split_string[0]);
                String id = split_string[1];
                ItemStack stack = ItemStackHelper.itemStackFromID(file, config_outputs, id);

                // Check the stack is valid
                if (stack == null) {
                    Logger.configError(Logger.OPTION_INVALID, file, config_outputs, output_key + "." + output_value);
                    continue;
                }

                // Add stack to the map
                output_items.put(stack, amount);
            }

            // Add the resulting list of stacks to the map
            outputs.put(output_tag, output_items);
        }

        return outputs;
    }


    
	public static List<ItemStack> loadGroup(String name) {

		// Load file
		File group_file = new File(ManagerPlugin.plugin.getDataFolder(), "itemgroups.yml");

		if (!group_file.exists()) {
            Logger.fileNotFoundError(group_file);
			return new ArrayList<> ();
		}

		// Load config
		FileConfiguration group_config = YamlConfiguration.loadConfiguration(group_file);
		ConfigurationSection group_section = group_config.getConfigurationSection(ITEM_GROUPS);
        Logger.log(name, true);
        List<String> item_names = group_section.getStringList(name.toLowerCase());

        // Convert names to MixedItemStacks
        List<ItemStack> item_stacks = new ArrayList<> ();

        for (String item_name : item_names) {
			item_stacks.add(ItemStackHelper.itemStackFromID(group_file, group_section, item_name));
        }

        return item_stacks;
	}
}
