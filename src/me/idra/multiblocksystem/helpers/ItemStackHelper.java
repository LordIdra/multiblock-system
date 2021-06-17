package me.idra.multiblocksystem.helpers;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;



public class ItemStackHelper {
    
    private ItemStackHelper() {
        // Empty constructor
    }



    public static ItemStack blockToItemStack(Block block) {
        

    }



    public static ItemStack itemStackFromID(File file, ConfigurationSection section, String ID) {

		ID = ID.toUpperCase();

		Material material = StringConversion.idToMaterial(ID);
		SlimefunItem slimefun_item = StringConversion.idToSlimefunItem(ID);

		if (slimefun_item != null) {
		return slimefun_item.getItem();

		} else if (material != null) {
			return new ItemStack(material);

		} else {
			Logger.configError(Logger.OPTION_INVALID, file, section, ID);
			return null;
		}
	}



    public static ItemStack itemStackFromID(String ID) {

		ID = ID.toUpperCase();

		Material material = StringConversion.idToMaterial(ID);
		SlimefunItem slimefun_item = StringConversion.idToSlimefunItem(ID);

		if (slimefun_item != null) {
		return slimefun_item.getItem();

		} else if (material != null) {
			return new ItemStack(material);

		} else {
			return null;
		}
	}
}
