package me.idra.multiblocksystem.lists;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;



public class ListAbstractMultiblocks {
	
	public static ArrayList<String> multiblock_names = new ArrayList<String> ();
	public static HashMap<String, AbstractMultiblock> structures = new HashMap<String, AbstractMultiblock> ();
	
	
	public static void initialize() {
		
		// Load multiblock names
		for (File multiblock_file : new File(ManagerPlugin.plugin.getDataFolder(), "multiblocks").listFiles()) {
			
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
