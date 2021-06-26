package me.idra.multiblocksystem.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.StringUtil;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.commands.CommandFilter;
import me.idra.multiblocksystem.lists.ListCommands;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.mrCookieSlime.Slimefun.api.BlockStorage;



public class TabHandler implements TabCompleter {

	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
	    List<String> completions = new ArrayList<> ();
	    List<String> possibilities = new ArrayList<> ();
	    
	    
	    for (BaseCommand base_command : ListCommands.command_object_array) {
	    	
	    	// If the player has permission to use the command
	    	if (!sender.hasPermission(base_command.permission))
	    		continue;

			// If the command is /mb tag
			if (base_command instanceof CommandFilter && args.length == 2) {
				
				BlockIterator block_iterator = new BlockIterator((Player) sender, 5);
				Block target_block = null;
				
				while (block_iterator.hasNext()) {
					
					Block next_block = block_iterator.next(); 
					
					if (next_block.getType() != Material.AIR) {
						target_block = next_block;
						break;
					}
				}

				if (target_block == null) {
					continue;
				}
		
				BaseWorldMultiblock multiblock = ListWorldMultiblocks.getMultiblockFromLocation(target_block.getLocation());

				if (multiblock == null) {
					continue;
				}

				// TODO ????????????
				if (target_block.getType() == Material.CHEST || target_block.getType() == Material.BARREL) {

//					for (String tag : multiblock.abstract_multiblock.inventory_tags) {
//						possibilities.add(tag);
//					}

				} else if ((BlockStorage.check(target_block) != null) && (BlockStorage.check(target_block).getId().contains("CAPACITOR"))) {
//					for (String tag : multiblock.abstract_multiblock.energy_tags) {
//						possibilities.add(tag);
//					}
				
				} else {
//					for (String tag : multiblock.abstract_multiblock.position_tags.keySet()) {
//						possibilities.add(tag);
//					}
				}

				continue;
			}

	    	
	    	// If there are less arguments inputted than the number of arguments in the command
	    	if (args.length <= base_command.all_arguments.size()) {
	    		
	    		// If the previous arguments all match
	    		boolean args_match = true;
	    		
	    		for (int i = 0; i < args.length - 1; i++) {
	    			if (!base_command.all_arguments.get(i).contains(args[i])) {
	    				args_match = false;
	    				break;
	    			}
	    		}
	    		
	    		if (args_match)
	    			for (String arg : base_command.all_arguments.get(args.length-1))
	    				possibilities.add(arg);
	    	}
	    }
	    
	    
	    StringUtil.copyPartialMatches(args[args.length - 1], possibilities, completions);
	    Collections.sort(completions);
	    
	    return completions;
	}
}
