package anon.playground.multiblock;

import org.bukkit.block.Block;

public class MultiBlockStructure {
	private final Block[][][] blockStructure;

	/**
	 * Constructor for creating a MultiBlockStructure
	 *
	 * @param blockStructure 3D Block Array, being ordered by X -> Y -> Z
	 */
	public MultiBlockStructure(Block[][][] blockStructure) {
		this.blockStructure = blockStructure;
	}

	public Block getBlock(int x, int y, int z) {
		return blockStructure[x][y][z];
	}
}
