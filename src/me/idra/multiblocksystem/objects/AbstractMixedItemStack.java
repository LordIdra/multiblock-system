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
		
		// For each material
		items.add(new MixedItemStack(slimefun_item));
		
		// Set tags
		this.tags = tags;
	}
	
	
	
	public boolean containsMaterial(Material material) {
		
		for (MixedItemStack item : items) {
			if (item.itemstack.getType() == material) {
				return true;
			}
		}
		
		return false;
	}
}
