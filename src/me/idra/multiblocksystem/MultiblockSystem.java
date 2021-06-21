package me.idra.multiblocksystem;


import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.idra.multiblocksystem.filehandlers.FileHandlerWorldMultiblocks;
import me.idra.multiblocksystem.managers.ManagerCommands;
import me.idra.multiblocksystem.managers.ManagerPlugin;
import me.idra.multiblocksystem.objects.JoinListener;
import me.idra.multiblocksystem.objects.MultiblockListener;
import me.idra.multiblocksystem.objects.PlayerInteractListener;
import me.idra.multiblocksystem.objects.TabHandler;
import org.bukkit.plugin.java.JavaPlugin;


public final class MultiblockSystem extends JavaPlugin implements SlimefunAddon {


	@Override
	public void onEnable() {

		// Perform initialization (outsourced to Chi- I mean, HelperPlugin)
		ManagerPlugin.initialize(this);

		// Set up commands
		getCommand("multiblock").setExecutor(new ManagerCommands());
		getCommand("multiblock").setTabCompleter(new TabHandler());

		// Set up listeners
		getServer().getPluginManager().registerEvents(new MultiblockListener(), this);
		getServer().getPluginManager().registerEvents(new JoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);

		// Load world multiblocks
		FileHandlerWorldMultiblocks.loadMultiblocks();
	}


	@Override
	public void onDisable() {

		FileHandlerWorldMultiblocks.saveMultiblocks();
	}


	@Override
	public JavaPlugin getJavaPlugin() {
		return this;
	}


	@Override
	public String getBugTrackerURL() {
		// no
		return null;
	}
}