package me.idra.multiblocksystem.multiblocks;

import java.util.Map;
import java.util.UUID;

import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;



public class LARGEGOLDPAN extends BaseWorldMultiblock {

	
	
	public LARGEGOLDPAN(AbstractMultiblock abstract_multiblock, Map<BlockPosition, String[]> blocks, UUID owner, int ID) {
		super(abstract_multiblock, blocks, owner, ID);

		// Start ticking
		startTick(true);
	}

	
	
	@Override
	public void tick() {
		// handleRecipes();
	}
}
