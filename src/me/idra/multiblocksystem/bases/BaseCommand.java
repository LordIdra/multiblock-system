package me.idra.multiblocksystem.bases;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;


public abstract class BaseCommand {
	
	public String[] name;
	public String description;
	public String[] arguments;
	public Permission permission;
	public boolean hidden;
	public boolean console;
	public ArrayList<ArrayList<String>> all_arguments = new ArrayList<ArrayList<String>> ();
	
	public void addName() {
		if (!hidden) {
			for (String s : name) {
				ArrayList<String> new_array = new ArrayList<String> ();
				new_array.add(s);
				all_arguments.add(new_array);
			}
		}
	}
	
	public void addPermission() {
		String formatted_command = "multiblocksystem.command.";
		
		for (String name : name)
			formatted_command += name + ".";
		
		formatted_command = formatted_command.substring(0, formatted_command.length() - 1);
		permission = new Permission(formatted_command);
	}
	
	public abstract boolean commandFunction(CommandSender sender, Command command, String label, String[] args);
}
