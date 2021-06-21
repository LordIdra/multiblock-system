package me.idra.multiblocksystem.multiblocks;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public class PROCESSINGMACHINE extends BaseWorldMultiblock {

	
	
	public PROCESSINGMACHINE(AbstractMultiblock abstract_multiblock, List<BlockPosition> blocks, UUID owner, int ID) {
		super(abstract_multiblock, blocks, owner, ID);


		// Start ticking
		startTick(true);
	}

	
	
	@Override
	public void tick() {
		tickRecipes();
	}
}
