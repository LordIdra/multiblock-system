package me.idra.multiblocksystem.commands;


import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.filehandlers.FileHandlerPermanentVariables;
import me.idra.multiblocksystem.helpers.ConstantPlaceholders;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListAbstractMultiblocks;
import me.idra.multiblocksystem.lists.ListBlockErrors;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.AbstractMultiblock;
import me.idra.multiblocksystem.objects.BlockError;
import me.idra.multiblocksystem.objects.ItemGroup;
import me.idra.multiblocksystem.objects.StructureDescriptor;
import me.idra.multiblocksystem.tasks.TaskVisualiseLocation;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CommandAssemble extends BaseCommand {

	public CommandAssemble() {
		super();

		name = new String[]{"assemble"};
		description = ChatColor.DARK_AQUA + "Assembles a specified multiblock";
		arguments = new String[]{"multiblock"};
		hidden = false;
		console = false;

		addPermission();
		addName();

		File multiblock_folder = new File(ManagerPlugin.plugin.getDataFolder(), "multiblocks");

		List<String> new_array = new ArrayList<>();
		if (multiblock_folder.exists()) {
			for (File file : multiblock_folder.listFiles()) {
				new_array.add(file.getName());
			}

			all_arguments.add(new_array);
		}
	}

	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {

		// Create descriptor for the abstract structure we're trying to assemble
		AbstractMultiblock abstract_structure_object = ListAbstractMultiblocks.structures.get(args[1].toUpperCase());

		// Check that the specified multiblock exists
		if (abstract_structure_object == null) {
			MessageHandler.send(sender,
					MessageHandler.getError("multiblock-not-recognised")
							.replace(ConstantPlaceholders.arg1, args[1]));
			return false;
		}

		// Check that the user has permission to assemble the multiblock
		if (!sender.hasPermission("multiblocksystem.multiblock." + args[1].toLowerCase())) {
			MessageHandler.send(sender,
					MessageHandler.getError("no-permission-multiblock"));
			return false;
		}

		// Find coordinates and block of the location the player is looking at
		Player player = (Player) sender;
		BlockIterator block_iterator = new BlockIterator(player, 5);
		Block central_block = null;

		while (block_iterator.hasNext()) { // While there are still elements in the block_iterator

			Block next_block = block_iterator.next(); // Get the next element from block_iterator (does not move cursor forwards)

			if (next_block.getType() == Material.LECTERN) {
				central_block = next_block;
				break;
			}
		}

		// Check block is not null
		if (central_block == null) {
			MessageHandler.send(sender,
					MessageHandler.getError("not-looking-at-lectern"));
			return false;
		}

		Location central_block_location = central_block.getLocation();

		// Find orientation of lectern
		BlockFace central_block_orientation = ((Directional) central_block.getBlockData()).getFacing();

		// Check if the lectern is already in use
		if (ListWorldMultiblocks.getMultiblockFromLocation(central_block_location) != null) { // Checking if the Lectern is already part in a different multiblock structure?
			MessageHandler.send(sender,
					MessageHandler.getError("lectern-already-used"));
			return false;
		}

		// Get the structure's descriptor
		StructureDescriptor abstract_descriptor = abstract_structure_object.structure;


		// Create structures to store errors, location->tag map, and item->block info map
		Map<Block, ItemGroup> block_to_group_map = AbstractMultiblock.getStructureFromStartingPoint(player, central_block_location, central_block_orientation, abstract_descriptor);
		List<BlockError> block_error_list = BlockError.getBlockErrorsFromInfoMap(player, block_to_group_map);

		// Calculate number of matching blocks and then percentage of blocks that match in each match[n]
		int correct_blocks = abstract_descriptor.number_of_solid_blocks - block_error_list.size();
		int percentage = Math.round((correct_blocks * 100) / (float) abstract_descriptor.number_of_solid_blocks);

		// Check if multiblock is complete or not
		if (percentage == 100) {

			// Multiblock complete, let's assemble it. Start by creating a map of locations to tags
			List<BlockPosition> assembled_block_position_list = new ArrayList<>();

			// For every BlockInfo in the structure
			for (Block block : block_to_group_map.keySet()) {

				// Convert location to block position
				BlockPosition block_position = new BlockPosition(block.getLocation());

				assembled_block_position_list.add(block_position);
			}

			// Actually create the multiblock
			int ID = FileHandlerPermanentVariables.currentID();
			ListWorldMultiblocks.instantiateWorldMultiblock(abstract_structure_object, assembled_block_position_list, player.getUniqueId(), ID);

			// Notify the user
			MessageHandler.send(sender,
					MessageHandler.getSuccess("multiblock-assembled"));


		} else {

			// Display % completion
			MessageHandler.send(sender,
					MessageHandler.getInfo("multiblock-incomplete")
							.replace(ConstantPlaceholders.arg1, String.valueOf(percentage)));

			// Display block error list
			ListBlockErrors.setBlockErrors(player, block_error_list);
			ListBlockErrors.display(player);

			// Also display the location of each block, so it's clear where the multiblock is
			for (Block block : block_to_group_map.keySet()) {
				TaskVisualiseLocation task = new TaskVisualiseLocation(player, block.getLocation());
				task.runTaskTimer(ManagerPlugin.plugin, 0, ManagerPlugin.config.getInt("LocationVisualisation.particle-interval"));
			}
		}

		// Successfully executed so return true
		return true;
	}
}
