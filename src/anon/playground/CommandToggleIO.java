package anon.playground;

import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.bases.BaseWorldMultiblock;
import me.idra.multiblocksystem.helpers.MessageHandler;
import me.idra.multiblocksystem.lists.ListWorldMultiblocks;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.BlockIterator;

public class CommandToggleIO extends BaseCommand {
	public CommandToggleIO() {
		super();

		name = new String[]{"toggle", "io"};
		description = ChatColor.DARK_AQUA + "Toggles the inventory as either a Input or Output";
		arguments = null;
		hidden = false;
		console = false;

		addPermission();
		addName();
	}

	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		BlockIterator block_iterator = new BlockIterator(player, 5);
		BaseWorldMultiblock multi_block;


		Inventory container_block = null;


		while (block_iterator.hasNext()) { // While there are still elements in the block_iterator

			Block next_block = block_iterator.next(); // Get the next element from block_iterator (does not move cursor forwards)

			if (next_block.getState() instanceof Container) { // If block is a container
				container_block = ((Container) next_block.getState()).getInventory();
				break;
			}
		}
		if (container_block == null || container_block.getLocation() == null) {
			MessageHandler.send(player, MessageHandler.getError("not-looking-at-container"));
			return false;
		} else {
			multi_block = ListWorldMultiblocks.getMultiblockFromLocation(container_block.getLocation());
			if (multi_block == null) {
				MessageHandler.send(player, MessageHandler.getError("not-part-of-multiblock"));
				return false;
			}
			multi_block.flipIO(container_block);
		}
		return true;
	}
}
