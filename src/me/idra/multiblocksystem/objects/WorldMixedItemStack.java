package me.idra.multiblocksystem.objects;

import org.bukkit.Location;
import org.bukkit.block.Block;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;



public class WorldMixedItemStack extends MixedItemStack {

	public Location location = null;


	
	public WorldMixedItemStack(Block block) {
		super(block.getType(), BlockStorage.check(block));
		
		location = block.getLocation();
	}
	
	public WorldMixedItemStack(SlimefunItem slimefun_item, Location location) {
		super(slimefun_item);
		this.location = location;
	}
}
