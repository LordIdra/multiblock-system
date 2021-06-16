package me.idra.multiblocksystem.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.MixedItemStack;
import me.idra.multiblocksystem.objects.RecipeMixedItemStack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;


public class ConfigHelper {
	
	public static final String ITEM_GROUPS = "itemGroups";
	
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


    public static Map<String, List<RecipeMixedItemStack>> getInputMap(File file, ConfigurationSection config_inputs) {

        // Create new input map
        Map<String, List<RecipeMixedItemStack>> inputs = new HashMap<> ();

        // Add each pair of tag to input items
        for (String input_tag : config_inputs.getKeys(false)) {

            ConfigurationSection input_section = config_inputs.getConfigurationSection(input_tag);
            Set<String> input_keys = input_section.getKeys(false);
            
            // Check input value is valid
            if (input_keys == null) {
                continue;
            }

            List<RecipeMixedItemStack> input_items = new ArrayList<> ();

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
                RecipeMixedItemStack stack = StringConversion.recipeMixedItemStackFromID(file, config_inputs, id);

                // Check the stack is valid
                if (stack == null) {
                    Logger.configError(Logger.OPTION_INVALID, file, config_inputs, input_key + "." + input_value);
                    continue;
                }

                // Add stack to the array
                stack.amount = amount;
                input_items.add(stack);
            }

            // Add the resulting list of stacks to the map
            inputs.put(input_tag, input_items);
        }

        return inputs;
    }



    public static Map<String, List<RecipeMixedItemStack>> getOutputMap(File file, ConfigurationSection config_outputs) {

        // Create new output map
        Map<String, List<RecipeMixedItemStack>> outputs = new HashMap<> ();

        // Add each pair of tag to output items
        for (String output_tag : config_outputs.getKeys(false)) {

            ConfigurationSection output_section = config_outputs.getConfigurationSection(output_tag);
            Set<String> output_keys = output_section.getKeys(false);
            
            // Check input value is valid
            if (output_keys == null) {
                continue;
            }

            List<RecipeMixedItemStack> output_items = new ArrayList<> ();

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
                RecipeMixedItemStack stack = StringConversion.recipeMixedItemStackFromID(file, config_outputs, id);

                // Check the stack is valid
                if (stack == null) {
                    Logger.configError(Logger.OPTION_INVALID, file, config_outputs, output_key + "." + output_value);
                    continue;
                }

                // Add stack to the array
                stack.amount = amount;
                output_items.add(stack);
            }

            // Add the resulting list of stacks to the map
            outputs.put(output_tag, output_items);
        }

        return outputs;
    }


    
	public static List<MixedItemStack> loadGroup(String name) {

		// Load file
		File group_file = new File(ManagerPlugin.plugin.getDataFolder(), "itemgroups.yml");

		if (!group_file.exists()) {
            Logger.fileNotFoundError(group_file);
			return null;
		}

		// Load config
		FileConfiguration group_config = YamlConfiguration.loadConfiguration(group_file);
		ConfigurationSection group_section = group_config.getConfigurationSection(ITEM_GROUPS);
        List<String> item_names = group_section.getStringList(name.toLowerCase());

        // Convert names to MixedItemStacks
        List<MixedItemStack> item_stacks = new ArrayList<MixedItemStack> ();

        for (String item_name : item_names) {
        
			Material material = StringConversion.idToMaterial(item_name);
			SlimefunItem slimefun_item = StringConversion.idToSlimefunItem(item_name);

			if 		(material != null) 		item_stacks.add(new MixedItemStack(material));
			else if (slimefun_item != null)	item_stacks.add(new MixedItemStack(slimefun_item));
			else 							Logger.configError(Logger.OPTION_INVALID, group_file, group_section, name + "." + item_name);
        }

        return item_stacks;
	}
}
