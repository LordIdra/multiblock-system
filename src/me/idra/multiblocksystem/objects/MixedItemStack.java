package me.idra.multiblocksystem.objects;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.idra.multiblocksystem.helpers.StringConversion;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;



public class MixedItemStack {

	public Location location = null;
	public String[] tags = null;
	public ItemStack itemstack = null;
	public SlimefunItemStack slimefun_itemstack = null;
	
	
	
	MixedItemStack(Material material) {
		
		// Valid item
		if (material != null) {
			this.itemstack = new ItemStack(material);
		
		// Invalid item; set the type to air
		} else {
			this.itemstack = new ItemStack(Material.AIR);
		}
	}
	
	
	MixedItemStack(SlimefunItem slimefun_item) {
		
		// Valid item
		if (slimefun_item != null) {
			this.slimefun_itemstack = new SlimefunItemStack(slimefun_item.getId(), slimefun_item.getItem());
		
		// Invalid item; set the type to air
		} else {
			this.itemstack = new ItemStack(Material.AIR);
		}
	}
	
	
	MixedItemStack(Material material, int amount) {
		
		// Valid item
		if (material != null) {
			this.itemstack = new ItemStack(material, amount);
		
		// Invalid item; set the type to air
		} else {
			this.itemstack = new ItemStack(Material.AIR);
		}
	}
	
	
	
	MixedItemStack(SlimefunItem slimefun_item, int amount) {
		
		// Valid item
		if (slimefun_item != null) {
			this.slimefun_itemstack = new SlimefunItemStack(new SlimefunItemStack(slimefun_item.getId(), slimefun_item.getItem()), amount);
		
		// Invalid item; set the type to air
		} else {
			this.itemstack = new ItemStack(Material.AIR);
		}
	}
	
	
	public MixedItemStack(Material material, SlimefunItem slimefun_item) {
		
		// Initializer for blocks that could either be slimefun or normal items
		// Slimefun item
		if (slimefun_item != null) {
			this.slimefun_itemstack = new SlimefunItemStack(slimefun_item.getId(), slimefun_item.getItem());
		
		// Normal item
		} else if (material != null) {
			this.itemstack = new ItemStack(material);
		// Invalid item; set the type to air
		} else {
			this.itemstack = new ItemStack(Material.AIR);
		}
	}
	
	
	
	public boolean isSlimefunItem() {
		
		// Returns true only if slimefun item is not null
		if (slimefun_itemstack == null)
			return false;
		else
			return true;
	}
	
	
	
	public ItemStack asItemStack() {
		
		// Either returns the ItemStack or converts the SlimefunItemStack to a regular ItemStack and returns it 
		if (isSlimefunItem())
			return (ItemStack) slimefun_itemstack;
		else
			return itemstack;
	}
	
	
	
	public String getDisplayName() {
		
		// Return either the Material or SlimefunItem[Stack] display name, according to which type the mixed item holds
		if (isSlimefunItem())
			return slimefun_itemstack.getItem().getItemName();
		else
			return StringConversion.materialToString(itemstack.getType());
	}
	
	
	
	public void addToInventory(Inventory inv) {
		
		if (isSlimefunItem())
			inv.addItem(itemstack);
		else
			inv.addItem(slimefun_itemstack);
	}



	public boolean compareType(ItemStack other_stack) {

		// Compare - normal
		if (!isSlimefunItem()) {
			if (other_stack.getType() == itemstack.getType())
				return true;
		
		// Compare - slimefun
		} else {

			if (SlimefunItem.getByItem(other_stack) == null)
				return false;

			if (SlimefunItem.getByItem(other_stack).getId().equals(slimefun_itemstack.getItemId()))
				return true;
		}

		return false;
	}
	
	
	
	/*
	 * STATIC METHODS
	 */

	public static List<MixedItemStack> fromMaterialArray(List<Material> materials) {
		
		// Start building an array of item stacks
		List<MixedItemStack> item_stacks = new ArrayList<> ();
		
		// Generate item stack for each material
		for (Material material : materials)
			item_stacks.add(new MixedItemStack(material));
		
		// Return array of item stacks
		return item_stacks;
	}
	
	
	public static List<MixedItemStack> arrayFromSlimefunID(String slimefun_ID) {
		
		// Start building an array of item stacks
		List<MixedItemStack> item_stacks = new ArrayList<> ();
		
		// Generate single item stack for the provided inputs
		item_stacks.add(new MixedItemStack(StringConversion.idToSlimefunItem(slimefun_ID)));
		
		// Return array of item stacks
		return item_stacks;
	}
}
