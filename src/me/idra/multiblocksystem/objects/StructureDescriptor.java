package me.idra.multiblocksystem.objects;



import java.util.List;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import me.idra.multiblocksystem.helpers.Logger;



public class StructureDescriptor {
	
	public int number_of_blocks;
	public int number_of_air_blocks;
	public int number_of_solid_blocks;
	
	
	
	public Vector central_block = new Vector();
	public Vector dimension = new Vector();
	public List<List<List<AbstractMixedItemStack>>> blocks;
	
	
	
	public StructureDescriptor(String name, List<List<List<AbstractMixedItemStack>>> layer_blocks) {
		
		// Set direct variables
		dimension.setY(layer_blocks.size());
		dimension.setX(layer_blocks.get(0).size());
		dimension.setZ(layer_blocks.get(0).get(0).size());
		blocks = layer_blocks;
		
		// Figure out if the structure contains a lectern (and if so, where it is)
		boolean found_lectern = false;
		
		// For each XYZ
		for (int y = 0; y < dimension.getY(); y++) {
			for (int x = 0; x < dimension.getX(); x++) {
				for (int z = 0; z < dimension.getZ(); z++) {
					
					// Get the block
					AbstractMixedItemStack block = layer_blocks.get(y).get(x).get(z);
					
					// If it doesn't exist move on
					if (block == null) {
						continue;
					}
					
					// If it does exist, check it's a lectern
					if (block.containsMaterial(Material.LECTERN)) {	
						
						// Literally what it says
						found_lectern = true;
						
						central_block.setX(x);
						central_block.setZ(z);
						central_block.setY(y);
						
						// Found the lectern, no need to continue checking each block
						break;
					}
				}
			}
		}
		
		
		// Lectern wasn't found, let's display an error
		if (!found_lectern) {
			Logger.log(
					Logger.getWarning("abstract-lectern-not-found")
					.replace("%multiblock%", name),
					true);
		}
		
		// Calculate number of solid blocks and air blocks in the structure
		number_of_blocks = dimension.getBlockX() * dimension.getBlockY() * dimension.getBlockZ();
		number_of_air_blocks = 0;
		
		// Loop through every block and check if it's an air block. If it is, increment air block counter
		for (List<List<AbstractMixedItemStack>> item_a_a : blocks)
			for (List<AbstractMixedItemStack> item_a : item_a_a)
				for (AbstractMixedItemStack item : item_a)
					if (item.containsMaterial(Material.AIR))
						number_of_air_blocks++;
		
		// Calculate number of solid blocks
		number_of_solid_blocks = number_of_blocks - number_of_air_blocks;
	}
}
