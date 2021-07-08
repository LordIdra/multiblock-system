package me.idra.multiblocksystem.helpers;


import java.util.List;
import java.io.File;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.idra.multiblocksystem.objects.ItemGroup;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;


public class StringConversion {

    private StringConversion() {
        // Empty constructor
    }


    public static Material idToMaterial(String name) {
        return Material.getMaterial(name.toUpperCase().replace(" ", "_"));
    }

    public static SlimefunItem idToSlimefunItem(String ID) {
        return SlimefunPlugin.getRegistry().getSlimefunItemIds().get(ID);
    }


    public static ItemGroup groupFromName(File file, ConfigurationSection section, String data) {

        // Capitalise the data (easier to deal with this way)
        data = data.toUpperCase();
        String tag = null;

        // Get tag
        if (data.contains("[")) {
            tag = data.substring(data.indexOf("[") + 1, data.indexOf("]"));
            data = data.substring(0, data.indexOf("["));
        }

        // Check which type of item we're dealing with (normal or a group)
        List<ItemStack> item_stack_list = new ArrayList<>();
        String identifier;

        // Group
        if (data.contains("GROUP")) {
            String group_name = data.substring(6);
            identifier = group_name;
            item_stack_list = ConfigHelper.loadGroup(group_name);

        // Normal item
        } else {
            identifier = data;
            item_stack_list.add(ItemStackHelper.itemStackFromID(file, section, data));
        }

        // Create AbstractMixeditemStack
        return new ItemGroup(identifier, tag, item_stack_list);
    }


    public static String formatItemName(String name) {

        // Remove underscores
        name = name.toLowerCase().replace("_", " ");

        // Capitalise each word
        StringBuilder final_name = new StringBuilder();
        String[] words = name.split("\\s");

        for (int i = 0; i < words.length; i++) {

            final_name.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1));

            if (i != words.length - 1)
                final_name.append(" ");
        }

        // Return the final name
        return final_name.toString();
    }


    public static String formatBlock(Block block) {

        // Get name
        SlimefunItem slimefun_item = BlockStorage.check(block);
        Material material = block.getType();
        String name;

        if (slimefun_item != null) {
            name = slimefun_item.getId();

        } else {
            name = material.name();
        }

        // Return the final name
        return formatItemName(name);
    }
}
