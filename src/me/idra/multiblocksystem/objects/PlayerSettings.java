package me.idra.multiblocksystem.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.inventory.ClickType;

import me.idra.multiblocksystem.helpers.ConstantSettingNames;

public class PlayerSettings {
	
	public UUID player;
	public boolean auto_build_enabled = true;
	
	public class SettingContainer {
		private int value;
		private int lower_band;
		private int upper_band;
		
		private SettingContainer(int new_value, int new_lower_band, int new_upper_band) {
			value = new_value;
			lower_band = new_lower_band;
			upper_band = new_upper_band;
		};
		
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		
		public int getLower_band() {
			return lower_band;
		}
		public void setLower_band(int lower_band) {
			this.lower_band = lower_band;
		}
		
		public int getUpper_band() {
			return upper_band;
		}
		public void setUpper_band(int upper_band) {
			this.upper_band = upper_band;
		}
		
	}
	
	public Map<String,SettingContainer> settingContainerMap;
	
	public PlayerSettings() {
		
		settingContainerMap =  new HashMap<String, SettingContainer>(); 
		
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
	
	public void setBounds() {
		
		settingContainerMap.forEach((key, value) -> settingContainerMap.put(key, 
				new SettingContainer(value.value, 
						             value.value < value.lower_band ? value.lower_band : value.value, 
				            		 value.value > value.upper_band ? value.upper_band : value.value)));
		
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
	
}
