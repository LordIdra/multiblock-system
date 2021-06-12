package me.idra.multiblocksystem.objects;

import org.bukkit.Material;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

public class RecipeMixedItemStack extends MixedItemStack{

	public RecipeMixedItemStack(Material material) {
		super(material);
	}

	public RecipeMixedItemStack(SlimefunItem slimefun_item) {
		super(slimefun_item);
    }

    public int amount = 0;
}
