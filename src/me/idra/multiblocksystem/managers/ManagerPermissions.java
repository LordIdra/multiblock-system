package me.idra.multiblocksystem.managers;

import java.util.ArrayList;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.lists.ListAbstractMultiblocks;
import me.idra.multiblocksystem.lists.ListCommands;
import me.idra.multiblocksystem.objects.AbstractMultiblock;



public class ManagerPermissions {

	private ManagerPermissions() {
		// Empty constructor
	}



	private static ArrayList<Permission> command_permissions = new ArrayList<Permission> ();
	private static ArrayList<Permission> multiblock_permissions = new ArrayList<Permission> ();
	
	
	
	public static void initialize() {
		
		// Command permissions
		for (BaseCommand command : ListCommands.command_object_array)
			command_permissions.add(command.permission);
		
		// Multiblock permissions
		for (AbstractMultiblock multiblock : ListAbstractMultiblocks.structures.values()) {
			
			String formatted_name = "multiblocksystem.multiblock." + multiblock.name.toLowerCase();
			multiblock_permissions.add(new Permission(formatted_name));
		}
		
		// Register all permissions
		PluginManager plugin_manager = ManagerPlugin.plugin.getServer().getPluginManager();
		
		for (Permission permission : command_permissions)
			plugin_manager.addPermission(permission);
		
		for (Permission permission : multiblock_permissions)
			plugin_manager.addPermission(permission);
	}
}
