package me.idra.multiblocksystem.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.inventory.ClickType;

import me.idra.multiblocksystem.helpers.ConstantSettingNames;



public class PlayerSettings {
	
	public UUID player;
	public boolean auto_build_enabled = true;
	


	public static class SettingContainer {

		private int value;
		private final int lower_bound;
		private final int upper_bound;
		

		private SettingContainer(int new_value, int new_lower_bound, int new_upper_bound) {
			value = new_value;
			lower_bound = new_lower_bound;
			upper_bound = new_upper_bound;
		}

		
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
	}
	


	public Map<String,SettingContainer> settingContainerMap;
	
	public PlayerSettings() {
		
		settingContainerMap =  new HashMap<>(); 
		
		settingContainerMap.put(ConstantSettingNames.UNRESOLVED_ERROR_TIME, new SettingContainer(15,4,30));
		settingContainerMap.put(ConstantSettingNames.RESOLVED_ERROR_TIME, new SettingContainer(1,0,5));
		settingContainerMap.put(ConstantSettingNames.ERROR_PARTICLE_AMOUNT, new SettingContainer(1,20,100));
		settingContainerMap.put(ConstantSettingNames.LOCATION_PARTICLE_TIME, new SettingContainer(5,2,10));	
		settingContainerMap.put(ConstantSettingNames.LOCATION_PARTICLE_AMOUNT, new SettingContainer(5,2,10));	
		settingContainerMap.put(ConstantSettingNames.LIST_ITEMS_PER_PAGE, new SettingContainer(8,3,15));	
		
		settingContainerMap.put(ConstantSettingNames.ERROR_OFFSET_X, new SettingContainer(8,3,15));
		settingContainerMap.put(ConstantSettingNames.ERROR_OFFSET_Y, new SettingContainer(8,3,15));
		settingContainerMap.put(ConstantSettingNames.ERROR_OFFSET_Z, new SettingContainer(8,3,15));
		
		settingContainerMap.put(ConstantSettingNames.UNRESOLVED_ERROR_R, new SettingContainer(10,0,10));
		settingContainerMap.put(ConstantSettingNames.UNRESOLVED_ERROR_G, new SettingContainer(0,0,10));
		settingContainerMap.put(ConstantSettingNames.UNRESOLVED_ERROR_B, new SettingContainer(0,0,10));
		
		settingContainerMap.put(ConstantSettingNames.RESOLVED_ERROR_R, new SettingContainer(0,0,10));
		settingContainerMap.put(ConstantSettingNames.RESOLVED_ERROR_G, new SettingContainer(10,0,10));
		settingContainerMap.put(ConstantSettingNames.RESOLVED_ERROR_B, new SettingContainer(0,0,10));
		
		settingContainerMap.put(ConstantSettingNames.LOCATION_R, new SettingContainer(0,0,10));
		settingContainerMap.put(ConstantSettingNames.LOCATION_G, new SettingContainer(0,0,10));
		settingContainerMap.put(ConstantSettingNames.LOCATION_B, new SettingContainer(10,0,10));
	
	}
	
	public void checkBounds() {
		
		for (SettingContainer container : settingContainerMap.values()) {

			if (container.value < container.lower_bound) container.value = container.lower_bound;
			if (container.value > container.upper_bound) container.value = container.upper_bound;
		}
	}
	

	public void setContainerValue(String settingContainerKey, int new_value) {
		
		SettingContainer updatedSettingContainer = settingContainerMap.get(settingContainerKey);
		updatedSettingContainer.value = new_value;
		settingContainerMap.put(settingContainerKey, updatedSettingContainer);
		
	}
	
	public String getContainerValue(String settingContainerKey) {
		SettingContainer settingContainer = settingContainerMap.get(settingContainerKey);
		return String.valueOf(settingContainer.value);
	}
	
	public int getContainerValueAsInt(String settingContainerKey) {
		SettingContainer settingContainer = settingContainerMap.get(settingContainerKey);
		return settingContainer.value;
	}

	
	public void changeValue(String settingContainerKey, int increment_value) {
		SettingContainer settingContainer = settingContainerMap.get(settingContainerKey);
		settingContainer.value += increment_value;
		settingContainerMap.put(settingContainerKey, settingContainer);	
	}

	
	public void handleClick(String settingContainerKey, ClickType click) {
		
		if (click.isLeftClick())
			this.changeValue(settingContainerKey, 1);
		else if (click.isRightClick())
			this.changeValue(settingContainerKey, -1);
		
	}

	
	public String formattedBounds(String key) {
		SettingContainer container = settingContainerMap.get(key);
		return " (" + container.lower_bound + "-" + container.upper_bound + ")";
	}
}
