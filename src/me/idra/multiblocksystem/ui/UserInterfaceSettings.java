package me.idra.multiblocksystem.ui;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.idra.multiblocksystem.bases.BaseUserInterface;
import me.idra.multiblocksystem.filehandlers.FileHandlerPlayerData;
import me.idra.multiblocksystem.helpers.ConstantSettingNames;
import me.idra.multiblocksystem.lists.ListPlayerSettings;
import me.idra.multiblocksystem.objects.PlayerSettings;
import net.md_5.bungee.api.ChatColor;



public class UserInterfaceSettings extends BaseUserInterface {

	private static final String INCREASE_TEXT = ChatColor.GRAY + ">> left click to increase";
	private static final String DECREASE_TEXT = ChatColor.GRAY + ">> right click to decrease";
	private static final String ADJUST_TEXT = ChatColor.GRAY + ">> click to adjust";
	private static final String CURRENT_VALUE = ChatColor.DARK_AQUA + "current value: ";

	private static final String UNRESOLVED_PARTICLE_COLOR = "unresolved-particle-color";
	private static final String RESOLVED_PARTICLE_COLOR = "resolved-particle-color";
	private static final String LOCATION_PARTICLE_COLOR = "location-particle-color";
	
	private static final String ERROR_PARTICLE_OFFSET = "error-particle-offset";
	
	private final PlayerSettings settings;
	private final Map<String, UserInterfaceColorAdjuster> color_adjusters = new HashMap<> ();
	private final Map<String, UserInterfaceVectorAdjuster> vector_adjusters = new HashMap<> ();
	
	
	
	public void initializeItems() {
		
		UserInterfaceColorAdjuster unresolved_particle_color_adjuster = color_adjusters.get(UNRESOLVED_PARTICLE_COLOR);
		if (unresolved_particle_color_adjuster != null) {
			
			settings.setContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_R, unresolved_particle_color_adjuster.r);
			settings.setContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_G, unresolved_particle_color_adjuster.g);
			settings.setContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_B, unresolved_particle_color_adjuster.b);
	
		}
		
		UserInterfaceColorAdjuster resolved_particle_color_adjuster = color_adjusters.get(RESOLVED_PARTICLE_COLOR);
		if (resolved_particle_color_adjuster != null) {
			settings.setContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_R, resolved_particle_color_adjuster.r);
			settings.setContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_G, resolved_particle_color_adjuster.g);
			settings.setContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_B, resolved_particle_color_adjuster.b);
		}
		
		UserInterfaceColorAdjuster location_particle_color_adjuster = color_adjusters.get(LOCATION_PARTICLE_COLOR);
		if (location_particle_color_adjuster != null) {
			settings.setContainerValue(ConstantSettingNames.LOCATION_R, location_particle_color_adjuster.r);
			settings.setContainerValue(ConstantSettingNames.LOCATION_G, location_particle_color_adjuster.g);
			settings.setContainerValue(ConstantSettingNames.LOCATION_B, location_particle_color_adjuster.b);
		}
		
		UserInterfaceVectorAdjuster error_offset_adjuster = vector_adjusters.get(ERROR_PARTICLE_OFFSET);
		if (error_offset_adjuster != null) {
			settings.setContainerValue(ConstantSettingNames.ERROR_OFFSET_X, error_offset_adjuster.x);
			settings.setContainerValue(ConstantSettingNames.ERROR_OFFSET_Y, error_offset_adjuster.y);
			settings.setContainerValue(ConstantSettingNames.ERROR_OFFSET_Z, error_offset_adjuster.z);
		}
		
		// Save settings permanently
		FileHandlerPlayerData.saveSettings(player.getUniqueId(), settings);
		
		
		
		if (settings.auto_build_enabled) {
			setItem(0, Material.GREEN_CONCRETE, 
					ChatColor.GOLD + "" + ChatColor.BOLD + "Automatic Error Switching", 
					CURRENT_VALUE + ChatColor.GREEN + "enabled",
					ChatColor.GRAY + ">> click to toggle");
		} else {
			setItem(0, Material.RED_CONCRETE,
					ChatColor.GOLD + "" + ChatColor.BOLD + "Automatic Error Switching", 
					CURRENT_VALUE + ChatColor.RED + "disabled",
					ChatColor.GRAY + ">> click to toggle");
		}
		
		setItem(9, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Unresolved Error Display Time" + 
					ChatColor.RESET + ChatColor.YELLOW + settings.formattedBounds(ConstantSettingNames.UNRESOLVED_ERROR_TIME), 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_TIME),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(10, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Resolved Error Display Time" + ChatColor.RESET + ChatColor.YELLOW + settings.formattedBounds(ConstantSettingNames.RESOLVED_ERROR_TIME), 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue(ConstantSettingNames.RESOLVED_ERROR_TIME),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(11, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Error Particle Amount" + ChatColor.RESET + ChatColor.YELLOW + settings.formattedBounds(ConstantSettingNames.ERROR_PARTICLE_AMOUNT), 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue(ConstantSettingNames.ERROR_PARTICLE_AMOUNT),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(12, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Multiblock Location Particle Time" + ChatColor.RESET + ChatColor.YELLOW + settings.formattedBounds(ConstantSettingNames.LOCATION_PARTICLE_TIME), 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue(ConstantSettingNames.LOCATION_PARTICLE_TIME),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(13, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Multiblock Location Particle Amount" + ChatColor.RESET + ChatColor.YELLOW + settings.formattedBounds(ConstantSettingNames.LOCATION_PARTICLE_AMOUNT), 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue(ConstantSettingNames.LOCATION_PARTICLE_AMOUNT),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(14, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "List Items Per Page" + ChatColor.RESET + ChatColor.YELLOW + settings.formattedBounds(ConstantSettingNames.LIST_ITEMS_PER_PAGE), 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue(ConstantSettingNames.LIST_ITEMS_PER_PAGE),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		
		setItem(18, Material.BLUE_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Unresolved Error Particle Color", 
				CURRENT_VALUE 
						+ ChatColor.RED + settings.getContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_R) + " " 
						+ ChatColor.GREEN + settings.getContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_G) + " " 
						+ ChatColor.BLUE + settings.getContainerValue(ConstantSettingNames.UNRESOLVED_ERROR_B),
				ChatColor.GRAY + ADJUST_TEXT);
		
		setItem(19, Material.BLUE_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Resolved Error Particle Color", 
				CURRENT_VALUE 
						+ ChatColor.RED + settings.getContainerValue(ConstantSettingNames.RESOLVED_ERROR_R) + " " 
						+ ChatColor.GREEN + settings.getContainerValue(ConstantSettingNames.RESOLVED_ERROR_G) + " " 
						+ ChatColor.BLUE + settings.getContainerValue(ConstantSettingNames.RESOLVED_ERROR_B),
				ChatColor.GRAY + ADJUST_TEXT);
		
		setItem(20, Material.BLUE_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Location Particle Color", 
				CURRENT_VALUE 
						+ ChatColor.RED + settings.getContainerValue(ConstantSettingNames.LOCATION_R) + " " 
						+ ChatColor.GREEN + settings.getContainerValue(ConstantSettingNames.LOCATION_G) + " " 
						+ ChatColor.BLUE + settings.getContainerValue(ConstantSettingNames.LOCATION_B),
				ChatColor.GRAY + ADJUST_TEXT);
		
		
		setItem(27, Material.LIGHT_GRAY_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Error Particle Length", 
				CURRENT_VALUE 
						+ ChatColor.WHITE + settings.getContainerValue(ConstantSettingNames.ERROR_OFFSET_X) + " " 
						+ ChatColor.WHITE + settings.getContainerValue(ConstantSettingNames.ERROR_OFFSET_Y) + " " 
						+ ChatColor.WHITE + settings.getContainerValue(ConstantSettingNames.ERROR_OFFSET_Z),
				ChatColor.GRAY + ADJUST_TEXT);
	}
	
	
	
	public UserInterfaceSettings(Player player) {
		super(player, 36, "General Settings");
		
		settings = ListPlayerSettings.getPlayerSettings(player.getUniqueId());
		
		initializeItems();
	}
	
	

	@Override
	protected void onClick(Player player, int slot, ClickType click) {
		
		switch (slot) {
		
		// Auto build
		case 0:
			settings.auto_build_enabled = !settings.auto_build_enabled;
			break;
		
		case 9:
			settings.handleClick(ConstantSettingNames.UNRESOLVED_ERROR_TIME, click);
			break;
			
		case 10:
			settings.handleClick(ConstantSettingNames.RESOLVED_ERROR_TIME, click);
			break;
			
		case 11:
			settings.handleClick(ConstantSettingNames.ERROR_PARTICLE_AMOUNT, click);
			break;
			
		case 12:
			settings.handleClick(ConstantSettingNames.LOCATION_PARTICLE_TIME, click);
			break;
			
		case 13:
			settings.handleClick(ConstantSettingNames.LOCATION_PARTICLE_AMOUNT, click);
			break;
			
		case 14:
			settings.handleClick(ConstantSettingNames.LIST_ITEMS_PER_PAGE, click);
			break;
			
			
		case 18:
			color_adjusters.put(UNRESOLVED_PARTICLE_COLOR, new UserInterfaceColorAdjuster(player, 
                    this, 
                    settings.getContainerValueAsInt(ConstantSettingNames.UNRESOLVED_ERROR_R),
                    settings.getContainerValueAsInt(ConstantSettingNames.UNRESOLVED_ERROR_G), 
                    settings.getContainerValueAsInt(ConstantSettingNames.UNRESOLVED_ERROR_B)));
			color_adjusters.get(UNRESOLVED_PARTICLE_COLOR).display();
			break;

		case 19:
			color_adjusters.put(RESOLVED_PARTICLE_COLOR, new UserInterfaceColorAdjuster(player, 
					this, 
					settings.getContainerValueAsInt(ConstantSettingNames.RESOLVED_ERROR_R), 
					settings.getContainerValueAsInt(ConstantSettingNames.RESOLVED_ERROR_G),
					settings.getContainerValueAsInt(ConstantSettingNames.RESOLVED_ERROR_B)));
			color_adjusters.get(RESOLVED_PARTICLE_COLOR).display();
			break;
			
		case 20:
			color_adjusters.put(LOCATION_PARTICLE_COLOR, new UserInterfaceColorAdjuster(player, 
					this, 
					settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_R), 
					settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_G), 
					settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_B)));
			color_adjusters.get(LOCATION_PARTICLE_COLOR).display();
			break;

			
		case 27:
			vector_adjusters.put(ERROR_PARTICLE_OFFSET, new UserInterfaceVectorAdjuster(player, 
					this, 
					settings.getContainerValueAsInt(ConstantSettingNames.ERROR_OFFSET_X),
					settings.getContainerValueAsInt(ConstantSettingNames.ERROR_OFFSET_Y), 
					settings.getContainerValueAsInt(ConstantSettingNames.ERROR_OFFSET_Z)));
			vector_adjusters.get(ERROR_PARTICLE_OFFSET).display();
			break;
		

		default:
			break;
		}
		
		// Bound checking
		settings.checkBounds();

		// Reset our items, as settings have been changed
		initializeItems();
		
		// Save settings permanently
		FileHandlerPlayerData.saveSettings(player.getUniqueId(), settings);
	}
}