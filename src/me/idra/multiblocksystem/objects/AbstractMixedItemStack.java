package me.idra.multiblocksystem.objects;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;



public class AbstractMixedItemStack {

	public String tag = null;
	public String identifier = "";
	public List<MixedItemStack> items = new ArrayList<> ();
	
	
	
	public AbstractMixedItemStack(Material material, String tag, String identifier) {
		
		// Add a single material to our MixedItemStack
		items.add(new MixedItemStack(material));
		
		// Set tag
		this.tag = tag;

		// Identifier
		this.identifier = identifier;
	}
	
	public AbstractMixedItemStack(SlimefunItem slimefun_item, String tag, String identifier) {
		
		// Add single slimefun item
		items.add(new MixedItemStack(slimefun_item));
		
		// Set tag
		this.tag = tag;

		// Identifier
		this.identifier = identifier;

	}

	public AbstractMixedItemStack(List<MixedItemStack> stacks, String tag, String identifier) {
		
		// Set MixedItemStack list
		this.items = stacks;

		// Set tag
		this.tag = tag;

		// Identifier
		this.identifier = identifier;
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
