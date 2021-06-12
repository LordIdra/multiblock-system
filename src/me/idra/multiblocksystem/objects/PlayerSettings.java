package me.idra.multiblocksystem.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.inventory.ClickType;

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
		
		settingContainerMap.put("unresolved_error_time", new SettingContainer(15,4,30));
		settingContainerMap.put("resolved_error_time", new SettingContainer(1,0,5));
		settingContainerMap.put("error_particle_amount", new SettingContainer(1,20,100));
		settingContainerMap.put("location_particle_time", new SettingContainer(5,2,10));	
		settingContainerMap.put("location_particle_amount", new SettingContainer(5,2,10));	
		settingContainerMap.put("list_items_per_page", new SettingContainer(8,3,15));	
		
		settingContainerMap.put("error_offset_x", new SettingContainer(8,3,15));
		settingContainerMap.put("error_offset_y", new SettingContainer(8,3,15));
		settingContainerMap.put("error_offset_z", new SettingContainer(8,3,15));
		
		settingContainerMap.put("unresolved_error_r", new SettingContainer(10,0,10));
		settingContainerMap.put("unresolved_error_g", new SettingContainer(0,0,10));
		settingContainerMap.put("unresolved_error_b", new SettingContainer(0,0,10));
		
		settingContainerMap.put("resolved_error_r", new SettingContainer(0,0,10));
		settingContainerMap.put("resolved_error_g", new SettingContainer(10,0,10));
		settingContainerMap.put("resolved_error_b", new SettingContainer(0,0,10));
		
		settingContainerMap.put("location_r", new SettingContainer(0,0,10));
		settingContainerMap.put("location_g", new SettingContainer(0,0,10));
		settingContainerMap.put("location_b", new SettingContainer(10,0,10));
	
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
