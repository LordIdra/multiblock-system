package me.idra.multiblocksystem.lists;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;



public class ListAbstractMultiblocks {

	private static final List<String> multiblock_names = new ArrayList<> ();
	public static Map<String, AbstractMultiblock> structures = new HashMap<> ();

	private ListAbstractMultiblocks() {
		// Empty constructor
	}



	
	public static void initialize() {
		
		// Load multiblock names
		File[] multiblock_folder = new File(ManagerPlugin.plugin.getDataFolder(), "multiblocks").listFiles();
		if (multiblock_folder == null) {
			return;
		}

		for (File multiblock_file : multiblock_folder) {
			
			// Append base name to multiblock name array
			multiblock_names.add(multiblock_file.getName());
		}
		
		
		// Load multiblock abstract structure objects
		for (String name : multiblock_names) {
			
			// Add the name, in uppercase, mapped to an abstract multiblock object instantiated using the multiblock's name
			structures.put(name.toUpperCase(), new AbstractMultiblock(name));
		}
	}
}
