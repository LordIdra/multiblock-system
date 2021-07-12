package me.idra.multiblocksystem.helpers;

import me.idra.multiblocksystem.lists.ListItemGroups;
import me.idra.multiblocksystem.objects.ItemGroup;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.io.File;


public class ItemStackHelper {

	private ItemStackHelper() {
		// Empty constructor
	}


	public static ItemStack blockToItemStack(Block block) {

		SlimefunItem slimefun_item = BlockStorage.check(block);
		Material material = block.getType();

		if (slimefun_item != null) {
			return slimefun_item.getItem();

		} else {
			return new ItemStack(material);
		}
	}


	/**
	 * Constructor for creating an ItemStack of either a Vanilla or a Slimefun item.<br>
	 * If an ItemStack cannot be created it will Log the error.
	 *
	 * @param file    Path to Config File
	 * @param section Config Label Path
	 * @param ID      ID for Item to be made. Can be Slimefun 4 or Vanilla Minecraft item
	 * @return ItemStack of either a Vanilla or Slimefun 4 item
	 */
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

	public static ItemGroup groupFromBlock(Block block) {
		ItemStack stack = blockToItemStack(block);

		for (String key : ListItemGroups.material_groups.keySet()) {
			ItemGroup value = ListItemGroups.material_groups.get(key);
			for (ItemStack group_stack : value.stacks) {
				if (SlimefunUtils.isItemSimilar(stack, group_stack, true, false)) {
					return value;
				}
			}
		}

		return null;
	}
}
