package me.idra.multiblocksystem.objects;

import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.Logger;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.List;

public class StructureDescriptor {

	public int number_of_blocks;
	public int number_of_air_blocks;
	public int number_of_solid_blocks;

	public Vector central_block;
	public Vector structure_dimensions;
	public List<List<List<ItemGroup>>> groups;

	/**
	 * Constructor for a Wrapper Class for Multiblocks. Containing Structures Dimensions, Location of Center Block and Amount of Total Blocks, Air Blocks and Solid Blocks
	 * @param multiblock_name Name of the Multiblock
	 * @param layer_groups YXZ list of Blocks stored as ItemGroups
	 */
	public StructureDescriptor(String multiblock_name, List<List<List<ItemGroup>>> layer_groups) {
		groups = layer_groups;
		structure_dimensions = new Vector();
		structure_dimensions.setX(groups.size());
		structure_dimensions.setY(groups.get(0).size());
		structure_dimensions.setZ(groups.get(0).get(0).size());

		setCentralBlock(multiblock_name);
		calculateAmountsOfDifferentBlocks();
	}

	/**
	 * Sets the Vector for Central Block to the first Lectern found in the lists of blocks
	 */
	private void setCentralBlock(String multiblock_name) {
		for (int y = 0; y < structure_dimensions.getY(); y++) {
			for (int x = 0; x < structure_dimensions.getX(); x++) {
				for (int z = 0; z < structure_dimensions.getZ(); z++) {
					if (groups.get(y).get(x).get(z).containsMaterial(Material.LECTERN)) {
						central_block = new Vector(x, y, z);
						return;
					}
				}
			}
		}
		Logger.log(
				Logger.getWarning("abstract-lectern-not-found")
				.replace(ConstantPlaceholders.arg1, multiblock_name),
				true);
	}

	/**
	 * Calculates and Sets the amount of different blocks
	 */
	private void calculateAmountsOfDifferentBlocks() {
		number_of_blocks = structure_dimensions.getBlockX() * structure_dimensions.getBlockY() * structure_dimensions.getBlockZ();
		number_of_air_blocks = 0;
		for (List<List<ItemGroup>> slice : groups) {
			for (List<ItemGroup> line : slice) {
				for (ItemGroup block : line) {
					if (block.containsMaterial(Material.AIR)) {
						number_of_air_blocks++;
					}
				}
			}
		}
		number_of_solid_blocks = number_of_blocks - number_of_air_blocks;
	}
}
