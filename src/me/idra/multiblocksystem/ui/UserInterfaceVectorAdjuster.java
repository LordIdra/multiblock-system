package me.idra.multiblocksystem.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.idra.multiblocksystem.bases.BaseUserInterface;
import net.md_5.bungee.api.ChatColor;



public class UserInterfaceVectorAdjuster extends BaseUserInterface {

	private static final String LIMIT_TEXT = " (5-20)";
	private static final String INCREASE_TEXT = ChatColor.GRAY + ">> left click to increase";
	private static final String DECREASE_TEXT = ChatColor.GRAY + ">> right click to decrease";
	private static final String CURRENT_VALUE_TEXT = ChatColor.DARK_AQUA + "current value: ";

	private final UserInterfaceSettings parent;
	
	public int x;
	public int y;
	public int z;
	
	
	
	protected void initializeItems() {
		
		setItem(0, Material.ENDER_PEARL, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "Return");
		
		setItem(3, Material.WHITE_CONCRETE, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "x" + ChatColor.RESET + ChatColor.GOLD + LIMIT_TEXT, 
				CURRENT_VALUE_TEXT + ChatColor.YELLOW + x,
				INCREASE_TEXT,
				DECREASE_TEXT);

		setItem(4, Material.WHITE_CONCRETE, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "y" + ChatColor.RESET + ChatColor.GOLD + LIMIT_TEXT, 
				CURRENT_VALUE_TEXT + ChatColor.YELLOW + y,
				INCREASE_TEXT,
				DECREASE_TEXT);
		
		setItem(5, Material.WHITE_CONCRETE, 
				ChatColor.WHITE + "" + ChatColor.BOLD + "z" + ChatColor.RESET + ChatColor.GOLD + LIMIT_TEXT, 
				CURRENT_VALUE_TEXT + ChatColor.YELLOW + z,
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