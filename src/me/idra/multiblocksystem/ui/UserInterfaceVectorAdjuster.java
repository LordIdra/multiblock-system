package me.idra.multiblocksystem.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.idra.multiblocksystem.bases.BaseUserInterface;
import net.md_5.bungee.api.ChatColor;



public class UserInterfaceVectorAdjuster extends BaseUserInterface {

	private final String LIMIT_TEXT = " (5-20)";
	private final String INCREASE_TEXT = ChatColor.GRAY + ">> left click to increase";
	private final String DECREASE_TEXT = ChatColor.GRAY + ">> right click to decrease";
	private final String CURRENT_VALUE_TEXT = ChatColor.DARK_AQUA + "current value: ";

	private UserInterfaceSettings parent;
	
	public int x = 0;
	public int y = 0;
	public int z = 0;
	
	
	
	protected void initializeItems() {
		
		setItem(0, Material.ENDER_PEARL, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "Return");
		
		setItem(3, Material.WHITE_CONCRETE, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "Red" + ChatColor.RESET + ChatColor.GOLD + LIMIT_TEXT, 
				CURRENT_VALUE_TEXT + ChatColor.YELLOW + String.valueOf(x),
				INCREASE_TEXT,
				DECREASE_TEXT);

		setItem(4, Material.WHITE_CONCRETE, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "Green" + ChatColor.RESET + ChatColor.GOLD + LIMIT_TEXT, 
				CURRENT_VALUE_TEXT + ChatColor.YELLOW + String.valueOf(y),
				INCREASE_TEXT,
				DECREASE_TEXT);
		
		setItem(5, Material.WHITE_CONCRETE, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "Blue" + ChatColor.RESET + ChatColor.GOLD + LIMIT_TEXT, 
				CURRENT_VALUE_TEXT + ChatColor.YELLOW + String.valueOf(z),
				INCREASE_TEXT,
				DECREASE_TEXT);
	}
	
	
	
	public UserInterfaceVectorAdjuster(Player player, UserInterfaceSettings parent, int x, int y, int z) {
		super(player, 9, "General Settings");
		
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		
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
				x++;
			else if (click.isRightClick())
				x--;
			break;
			
		// Green
		case 4:
			if (click.isLeftClick())
				y++;
			else if (click.isRightClick())
				y--;
			break;
				
		// Blue
		case 5:
			if (click.isLeftClick())
				z++;
			else if (click.isRightClick())
				z--;
			break;
	
		// Default
		default:
			break;
		}
		
		
		// Range checks
		if (x < 5) x = 5;
		if (x > 20) x = 20;

		if (y < 5) y = 5;
		if (y > 20) y = 20;
		
		if (z < 5) z = 5;
		if (z > 20) z = 20;

		// Reset our items, as settings have been changed
		initializeItems();
		
		// If the player presses esc at this point, the settings won't save, so we need to initialize the parent as well
		parent.initializeItems();
	}
}