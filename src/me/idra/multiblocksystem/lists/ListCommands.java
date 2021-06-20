package me.idra.multiblocksystem.lists;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.commands.CommandAdminLogClear;
import me.idra.multiblocksystem.commands.CommandAdminLogView;
import me.idra.multiblocksystem.commands.CommandAssemble;
import me.idra.multiblocksystem.commands.CommandHelp;
import me.idra.multiblocksystem.commands.CommandListMultiblocks;
import me.idra.multiblocksystem.commands.CommandListTrusted;
import me.idra.multiblocksystem.commands.CommandPage;
import me.idra.multiblocksystem.commands.CommandSettings;
import me.idra.multiblocksystem.commands.CommandShowError;
import me.idra.multiblocksystem.commands.CommandFilter;
import me.idra.multiblocksystem.commands.CommandTrust;



public class ListCommands {

	private ListCommands() {
		// Empty constructor
	}


	
	public static final BaseCommand[] command_object_array = {
			new CommandHelp(),
			new CommandListMultiblocks(),
			new CommandListTrusted(),
			new CommandShowError(),
			new CommandAssemble(),
			new CommandTrust(),
			new CommandPage(),
			new CommandAdminLogView(),
			new CommandAdminLogClear(),
			new CommandFilter(),
			new CommandSettings()
	};
}
