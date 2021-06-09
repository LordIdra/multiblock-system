package me.idra.multiblocksystem.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.idra.multiblocksystem.bases.BaseUserInterface;
import net.md_5.bungee.api.ChatColor;



public class UserInterfaceColorAdjuster extends BaseUserInterface {

	private UserInterfaceSettings parent;
	
	public int r = 0;
	public int g = 0;
	public int b = 0;
	
	
	
	protected void initializeItems() {
		
		setItem(0, Material.ENDER_PEARL, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "Return");
		
		setItem(3, Material.RED_CONCRETE, 
				ChatColor.RED + "" + ChatColor.BOLD + "Red" + ChatColor.RESET + ChatColor.GOLD + " (0-10)", 
				ChatColor.DARK_AQUA + "current value: " + ChatColor.YELLOW + String.valueOf(r),
				ChatColor.GRAY + ">> left click to increase",
				ChatColor.GRAY + ">> right click to decrease");

		setItem(4, Material.GREEN_CONCRETE, 
				ChatColor.GREEN + "" + ChatColor.BOLD + "Green" + ChatColor.RESET + ChatColor.GOLD + " (0-10)", 
				ChatColor.DARK_AQUA + "current value: " + ChatColor.YELLOW + String.valueOf(g),
				ChatColor.GRAY + ">> left click to increase",
				ChatColor.GRAY + ">> right click to decrease");
		
		setItem(5, Material.BLUE_CONCRETE, 
				ChatColor.BLUE + "" + ChatColor.BOLD + "Blue" + ChatColor.RESET + ChatColor.GOLD + " (0-10)", 
				ChatColor.DARK_AQUA + "current value: " + ChatColor.YELLOW + String.valueOf(b),
				ChatColor.GRAY + ">> left click to increase",
				ChatColor.GRAY + ">> right click to decrease");
	}
	
	
	
	public UserInterfaceColorAdjuster(Player player, UserInterfaceSettings parent, int r, int g, int b) {
		super(player, 9, "General Settings");
		
		this.parent = parent;
		this.r = r;
		this.g = g;
		this.b = b;
		
		initializeItems();
	}
	
	

	@Override
	protected void onClick(Player player, int slot, ClickType click) {
		
		switch (slot) {
		
		// Return
		case 0:
			parent.initializeItems();
			parent.display();
			break;
		
		// Red
		case 3:
			if (click.isLeftClick())
				r++;
			else if (click.isRightClick())
				r--;
			break;
			
		// Green
		case 4:
			if (click.isLeftClick())
				g++;
			else if (click.isRightClick())
				g--;
			break;
				
		// Blue
		case 5:
			if (click.isLeftClick())
				b++;
			else if (click.isRightClick())
				b--;
			break;
		}
		
		
		// Range checks
		if (r < 0) r = 0;
		if (r > 10) r = 10;

		if (g < 0) g = 0;
		if (g > 10) g = 10;
		
		if (b < 0) b = 0;
		if (b > 10) b = 10;

		// Reset our items, as settings have been changed
		initializeItems();
		
		// If the player presses esc at this point, the settings won't save, so we need to initialize the parent as well
		parent.initializeItems();
	}
}