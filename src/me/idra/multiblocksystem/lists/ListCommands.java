package me.idra.multiblocksystem.lists;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.commands.CommandAdminLogClear;
import me.idra.multiblocksystem.commands.CommandAdminLogView;
import me.idra.multiblocksystem.commands.CommandAssemble;
import me.idra.multiblocksystem.commands.CommandEditorCreate;
import me.idra.multiblocksystem.commands.CommandHelp;
import me.idra.multiblocksystem.commands.CommandListMultiblocks;
import me.idra.multiblocksystem.commands.CommandListTrusted;
import me.idra.multiblocksystem.commands.CommandPage;
import me.idra.multiblocksystem.commands.CommandSettings;
import me.idra.multiblocksystem.commands.CommandShowError;
import me.idra.multiblocksystem.commands.CommandIOToggle;
import me.idra.multiblocksystem.commands.CommandIOView;
import me.idra.multiblocksystem.commands.CommandFilter;
import me.idra.multiblocksystem.commands.CommandTrust;



public class ListCommands {

	public static final BaseCommand[] command_object_array = {
			new CommandHelp(),
			new CommandAdminLogClear(),
			new CommandAdminLogView(),
			new CommandAssemble(),
			new CommandEditorCreate(),
			new CommandFilter(),
			new CommandListMultiblocks(),
			new CommandListTrusted(),
			new CommandPage(),
			new CommandSettings(),
			new CommandShowError(),
			new CommandIOToggle(),
			new CommandIOView(),
			new CommandTrust()
	};

	private ListCommands() {
		// Empty constructor
	}

}
