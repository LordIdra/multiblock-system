package me.idra.multiblocksystem.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import me.idra.multiblocksystem.objects.RecipeMixedItemStack;



public class ConfigHelper {
	
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
            for (String input_key : config_inputs.getConfigurationSection(input_tag).getKeys(false)) {

                List<String> input_values = config_inputs.getStringList(input_key);
                
                // Check input value is valid
                if (input_values == null) {
                    continue;
                }

                List<RecipeMixedItemStack> input_items = new ArrayList<> ();

                // Convert each input value to a MixedItemStack
                for (String input_value : input_values) {

                    // Seperate string into amount and ID
                    String[] split_string = input_value.split("\\s");

                    if (split_string.length != 2) {
                        Logger.configError(Logger.OPTION_INVALID, file, config_inputs, input_key);
                        continue;
                    }

                    // Get amount and ID from the resulting two strings
                    int amount = Integer.parseInt(split_string[0]);
                    String id = split_string[1];
                    RecipeMixedItemStack stack = StringConversion.recipeMixedItemStackFromID(id);

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
        }

        return inputs;
    }



    public static Map<String, List<RecipeMixedItemStack>> getOutputMap(File file, ConfigurationSection config_outputs) {

        // Create new output map
        Map<String, List<RecipeMixedItemStack>> outputs = new HashMap<> ();

        // Add each pair of tag to output items
        for (String output_tag : config_outputs.getKeys(false)) {
            for (String output_key : config_outputs.getConfigurationSection(output_tag).getKeys(false)) {

                List<String> output_values = config_outputs.getStringList(output_key);
                
                // Check output value is valid
                if (output_values == null) {
                    continue;
                }

                List<RecipeMixedItemStack> output_items = new ArrayList<> ();

                // Convert each output value to a MixedItemStack
                for (String output_value : output_values) {

                    // Seperate string into amount and ID
                    String[] split_string = output_value.split("\\s");

                    if (split_string.length != 2) {
                        Logger.configError(Logger.OPTION_INVALID, file, config_outputs, output_key);
                        continue;
                    }

                    // Get amount and ID from the resulting two strings
                    int amount = Integer.parseInt(split_string[0]);
                    String id = split_string[1];
                    RecipeMixedItemStack stack = StringConversion.recipeMixedItemStackFromID(id);

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
                outputs.put(output_key, output_items);
            }
        }

        return outputs;
    }
}
