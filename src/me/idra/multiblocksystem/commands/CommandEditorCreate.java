package me.idra.multiblocksystem.commands;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.helpers.ItemStackHelper;
import me.idra.multiblocksystem.objects.ItemGroup;



public class CommandEditorCreate extends BaseCommand{
	

	public CommandEditorCreate() {
		super();
		
		name = new String[] {"editor", "create"};
		description = ChatColor.DARK_AQUA + "Creates a new structure file from an in-world structure";
		arguments = new String[] {"filename", "y", "x", "z"};
		hidden = false;
		console = true;
		
		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Get dimensions
		int dimension_y = Integer.valueOf(args[3]);
		int dimension_x = Integer.valueOf(args[4]);
		int dimension_z = Integer.valueOf(args[5]);

		// Get corner position
		Location player_location = ((Player) sender).getLocation();
		Location corner = player_location.add(1, 0, 1);

		// Build block array
		List<List<List<ItemGroup>>> stack_list = new ArrayList<> ();

		for (int y = 0; y < dimension_y; y++) {
			List<List<ItemGroup>> stack_y = new ArrayList<> ();

			for (int x = 0; x < dimension_x; x++) {
				List<ItemGroup> stack_x = new ArrayList<> ();

				for (int z = 0; z < dimension_z; z++) {

					Location location = corner.clone().add(x, y, z);
					Block block = location.getBlock();
					stack_x.add(ItemStackHelper.groupFromBlock(block));
				}

				stack_y.add(stack_x);
			}

			stack_list.add(stack_y);
		}
		
		// Successful execution
		return true;
	}
}
