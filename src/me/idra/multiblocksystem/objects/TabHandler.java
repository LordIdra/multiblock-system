package me.idra.multiblocksystem.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.lists.ListCommands;



public class TabHandler implements TabCompleter {

	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
	    ArrayList<String> completions = new ArrayList<>();
	    ArrayList<String> possibilities = new ArrayList<>();
	    
	    
	    for (BaseCommand base_command : ListCommands.command_object_array) {
	    	
	    	// If the player has permission to use the command
	    	if (!sender.hasPermission(base_command.permission))
	    		continue;
	    	
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
