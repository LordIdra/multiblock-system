package me.idra.multiblocksystem.commands;



import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.idra.multiblocksystem.bases.BaseCommand;
import me.idra.multiblocksystem.lists.ListChatListers;



public class CommandPage extends BaseCommand{
	

	public CommandPage() {
		super();
		
		name = new String[] {"page"};
		description = ChatColor.DARK_AQUA + "View a specific page from the last list you have accessed.";
		arguments = new String[] {"page"};
		hidden = true;
		console = true;

		addPermission();
		addName();
	}
	
	@Override
	public boolean commandFunction(CommandSender sender, Command command, String label, String[] args) {
		
		// Convert page argument to integer
		int page = Integer.parseInt(args[1]);
		
		// Display requested page
		ListChatListers.chat_listers.get(sender).display(page);
		
		// Successful execution
		return true;
	}
}
