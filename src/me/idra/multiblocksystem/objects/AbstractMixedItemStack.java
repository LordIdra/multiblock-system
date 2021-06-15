package me.idra.multiblocksystem.objects;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;



public class AbstractMixedItemStack {

	public String[] tags = null;
	public List<MixedItemStack> items = new ArrayList<> ();
	
	
	
	
	public AbstractMixedItemStack(Material material, String[] tags) {
		
		// Add a single material to our MixedItemStack
		items.add(new MixedItemStack(material));
		
		// Set tags
		this.tags = tags;
	}
	
	public AbstractMixedItemStack(List<Material> materials, String[] tags) {
		
		// For each material
		for (Material material : materials)
			items.add(new MixedItemStack(material));
		
		// Set tags
		this.tags = tags;
	}
	
	public AbstractMixedItemStack(SlimefunItem slimefun_item, String[] tags) {
		
		// Add single slimefun item
		items.add(new MixedItemStack(slimefun_item));
		
		// Set tags
		this.tags = tags;

	}

	public AbstractMixedItemStack(List<MixedItemStack> stacks) {
		
		// Set MixedItemStack list
		this.items = stacks;
	}
	
	
	
	public boolean containsMaterial(Material material) {
		
		for (MixedItemStack item : items) {

			if (item.itemstack != null){
				if (item.itemstack.getType() == material) {
					return true;
				}
			}
		}
		
		return false;
	}
}
