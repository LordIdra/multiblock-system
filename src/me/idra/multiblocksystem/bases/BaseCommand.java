package me.idra.multiblocksystem.bases;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseCommand {

	public String[] name;
	public String description;
	public String[] arguments;
	public Permission permission;
	public boolean hidden;
	public boolean console;
	public List<List<String>> all_arguments = new ArrayList<>();

	public void addName() {
		if (!hidden) {
			for (String s : name) {
				List<String> new_array = new ArrayList<>();
				new_array.add(s);
				all_arguments.add(new_array);
			}
		}
	}

	public void addPermission() {

		// Create new StringBuilder
		StringBuilder formatted_command = new StringBuilder("multiblocksystem.command.");

		// Add each name to the permission
		for (String s : name) {
			formatted_command.append(s + ".");
		}

		// Remove the final '.'
		String formatted_command_string = formatted_command.substring(0, formatted_command.length() - 1);

		// Generate new permission
		permission = new Permission(formatted_command_string);
	}

	public abstract boolean commandFunction(CommandSender sender, Command command, String label, String[] args);
}
