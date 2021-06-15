package me.idra.multiblocksystem.lists;



import java.util.Map;

import org.bukkit.Material;

import me.idra.multiblocksystem.managers.ManagerPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;



public class ListMaterialGroups {

	private ListMaterialGroups() {
		// Empty constructor
	}



	Map<String, List<Material>> material_groups = new HashMap<> ();

	public void initialize() {

		// Check config file exists
		File material_group_file = new File(ManagerPlugin.plugin.getDataFolder(), "material-groups.yml");

		if (material_group_file.exists()) {
			try {
				material_group_file.createNewFile();
			} catch (IOException e) {
				// This should never happen
			}
		}

		
	}
}
