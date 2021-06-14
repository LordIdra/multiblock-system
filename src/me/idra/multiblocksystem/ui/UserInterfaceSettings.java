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
	
	private PlayerSettings settings = null;
	private Map<String, UserInterfaceColorAdjuster> color_adjusters = new HashMap<> ();
	private Map<String, UserInterfaceVectorAdjuster> vector_adjusters = new HashMap<> ();
	
	
	
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
		
		UserInterfaceVectorAdjuster error_offset_adjuster = vector_adjusters.get("error-particle-offset");
		if (error_offset_adjuster != null) {
			settings.setContainerValue("error_offset_x", error_offset_adjuster.x);
			settings.setContainerValue("error_offset_y", error_offset_adjuster.y);
			settings.setContainerValue("error_offset_z", error_offset_adjuster.z);
		}
		
		// Save settings permanently
		FileHandlerPlayerData.saveSettings(player.getUniqueId(), settings);
		
		
		
		if (settings.auto_build_enabled)
			setItem(0, Material.GREEN_CONCRETE, 
					ChatColor.GOLD + "" + ChatColor.BOLD + "Automatic Error Switching", 
					CURRENT_VALUE + ChatColor.GREEN + "enabled",
					ChatColor.GRAY + ">> click to toggle");
		else
			setItem(0, Material.RED_CONCRETE,
					ChatColor.GOLD + "" + ChatColor.BOLD + "Automatic Error Switching", 
					CURRENT_VALUE + ChatColor.RED + "disabled",
					ChatColor.GRAY + ">> click to toggle");

		
		setItem(9, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Unresolved Error Display Time" + ChatColor.RESET + ChatColor.YELLOW + " (4-30)", 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue("unresolved_error_time"),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(10, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Resolved Error Display Time" + ChatColor.RESET + ChatColor.YELLOW + " (0-5)", 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue("resolved_error_time"),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(11, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Error Particle Amount" + ChatColor.RESET + ChatColor.YELLOW + " (20-100)", 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue("error_particle_amount"),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(12, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Multiblock Location Particle Time" + ChatColor.RESET + ChatColor.YELLOW + " (2-10)", 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue("location_particle_time"),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(13, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Multiblock Location Particle Amount" + ChatColor.RESET + ChatColor.YELLOW + " (2-10)", 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue("location_particle_amount"),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		setItem(14, Material.YELLOW_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "List Items Per Page" + ChatColor.RESET + ChatColor.YELLOW + " (3-15)", 
				CURRENT_VALUE + ChatColor.AQUA + settings.getContainerValue("llist_items_per_page"),
				DECREASE_TEXT,
				INCREASE_TEXT);
		
		
		setItem(18, Material.BLUE_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Unresolved Error Particle Color", 
				CURRENT_VALUE 
						+ ChatColor.RED + settings.getContainerValue("unresolved_error_r") + " " 
						+ ChatColor.GREEN + settings.getContainerValue("unresolved_error_g") + " " 
						+ ChatColor.BLUE + settings.getContainerValue("unresolved_error_b"),
				ChatColor.GRAY + ADJUST_TEXT);
		
		setItem(19, Material.BLUE_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Resolved Error Particle Color", 
				CURRENT_VALUE 
						+ ChatColor.RED + settings.getContainerValue("resolved_error_r") + " " 
						+ ChatColor.GREEN + settings.getContainerValue("resolved_error_g") + " " 
						+ ChatColor.BLUE + settings.getContainerValue("resolved_error_b"),
				ChatColor.GRAY + ADJUST_TEXT);
		
		setItem(20, Material.BLUE_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Location Particle Color", 
				CURRENT_VALUE 
						+ ChatColor.RED + settings.getContainerValue("location_r") + " " 
						+ ChatColor.GREEN + settings.getContainerValue("location_g") + " " 
						+ ChatColor.BLUE + settings.getContainerValue("location_b"),
				ChatColor.GRAY + ADJUST_TEXT);
		
		
		setItem(27, Material.LIGHT_GRAY_CONCRETE,
				ChatColor.GOLD + "" + ChatColor.BOLD + "Error Particle Length", 
				CURRENT_VALUE 
						+ ChatColor.WHITE + settings.getContainerValue("error_offset_x") + " " 
						+ ChatColor.WHITE + settings.getContainerValue("error_offset_y") + " " 
						+ ChatColor.WHITE + settings.getContainerValue("error_offset_z"),
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
			if (settings.auto_build_enabled)
				settings.auto_build_enabled = false;
			else
				settings.auto_build_enabled = true;
			break;
			
		
		case 9:
			settings.handleClick("unresolved_error_time", click);
			break;
			
		case 10:
			settings.handleClick("resolved_error_time", click);
			break;
			
		case 11:
			settings.handleClick("error_particle_amount", click);
			break;
			
		case 12:
			settings.handleClick("location_particle_time", click);
			break;
			
		case 13:
			settings.handleClick("location_particle_amount", click);
			break;
			
		case 14:
			settings.handleClick("list_items_per_page", click);
			break;
			
			
		case 18:
			color_adjusters.put(UNRESOLVED_PARTICLE_COLOR, 
					            new UserInterfaceColorAdjuster(player, 
                                                               this, 
                                                               settings.getContainerValueAsInt("unresolved_error_r"),
                                                               settings.getContainerValueAsInt("unresolved_error_g"), 
                                                    		   settings.getContainerValueAsInt("unresolved_error_b")));
			color_adjusters.get(UNRESOLVED_PARTICLE_COLOR).display();
			break;

		case 19:
			color_adjusters.put(RESOLVED_PARTICLE_COLOR, 
								new UserInterfaceColorAdjuster(player, 
															 this, 
															 settings.getContainerValueAsInt("resolved_error_r"), 
															 settings.getContainerValueAsInt("resolved_error_g"),
															 settings.getContainerValueAsInt("resolved_error_b")));
			color_adjusters.get(RESOLVED_PARTICLE_COLOR).display();
			break;
			
		case 20:
			color_adjusters.put(LOCATION_PARTICLE_COLOR, new UserInterfaceColorAdjuster(player, 
					                                                                    this, 
					                                                                    settings.getContainerValueAsInt("location_r"), 
					                                                                    settings.getContainerValueAsInt("settings.location_g"), 
					                                                                    settings.getContainerValueAsInt("settings.location_b")));
			color_adjusters.get(LOCATION_PARTICLE_COLOR).display();
			break;

			
		case 27:
			vector_adjusters.put("error-particle-offset", new UserInterfaceVectorAdjuster(player, 
					                                      this, 
					                                      settings.getContainerValueAsInt("error_offset_x"),
					                                      settings.getContainerValueAsInt("settings.error_offset_y"), 
					                                      settings.getContainerValueAsInt("error_offset_z")));
			vector_adjusters.get("error-particle-offset").display();
			break;
		

		default:
			break;
		}
		
		// Bound checking
		settings.setBounds();

		// Reset our items, as settings have been changed
		initializeItems();
		
		// Save settings permanently
		FileHandlerPlayerData.saveSettings(player.getUniqueId(), settings);
	}
}