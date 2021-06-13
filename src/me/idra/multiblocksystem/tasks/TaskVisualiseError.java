package me.idra.multiblocksystem.tasks;

import java.awt.Color;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.idra.multiblocksystem.lists.ListBlockErrors;
import me.idra.multiblocksystem.lists.ListPlayerSettings;
import me.idra.multiblocksystem.managers.ManagerBlockErrorVisualisation;
import me.idra.multiblocksystem.objects.BlockError;
import me.idra.multiblocksystem.objects.PlayerSettings;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;



public class TaskVisualiseError extends BukkitRunnable{
	
	static final String BLOCKS_LEFT = " blocks left";
	BlockError error;
	public int visual_time;
	public Vector offset;
	public int amount;
	PlayerSettings settings;
	
	
	public TaskVisualiseError(BlockError error, int visual_time) {
		super();
		
		// Set direct variables
		this.error = error;
		this.visual_time = visual_time;
		
		// Get particle offset from config
		settings = ListPlayerSettings.getPlayerSettings(error.player.getUniqueId());
		
		offset = new Vector(
				settings.getContainerValueAsInt("error_offset_x"),
				settings.getContainerValueAsInt("error_offset_y"),
				settings.getContainerValueAsInt("error_offset_z"));
		amount = settings.getContainerValueAsInt("error_particle_amount");
		
		// Generate main/sub titles
		String main_title = error.getErrorTitle(false);
		String sub_title;
		
		int blocks_left = 0;
		
		for (BlockError new_error : ListBlockErrors.block_errors.get(error.player)) {
			if (!new_error.isResolved()) {
				blocks_left++;
			}
		}
		
		sub_title = getSubTitle(blocks_left);
		
		// Send the titles
		error.player.sendTitle(
				main_title, sub_title, 
				2, 
				settings.getContainerValueAsInt("unresolved_error_time") * 20, 
				2);
	}
	
	private String getSubTitle(int blocks_left) {
		
		if (settings.auto_build_enabled) {
			if (error.isResolved())
				return ChatColor.GREEN + String.valueOf(blocks_left) + ChatColor.YELLOW + BLOCKS_LEFT;
			else
				return ChatColor.DARK_RED + String.valueOf(blocks_left) + ChatColor.YELLOW + BLOCKS_LEFT;
		
		} else {		
			return "";		
		}
	}
	
	public void visualise(int r, int g, int b) {
		
		// Initialize particle
		ParticleBuilder particle;
		Color dust_color = new Color(r, g, b);
		
		// X indicator
		particle = new ParticleBuilder(ParticleEffect.REDSTONE, error.current_item.location);
		particle.setOffsetX(offset.getBlockX());
		particle.setAmount(amount);
		particle.setColor(dust_color);
        particle.display();

		// Y indicator
		particle = new ParticleBuilder(ParticleEffect.REDSTONE, error.current_item.location);
		particle.setOffsetY(offset.getBlockY());
		particle.setAmount(amount);
		particle.setColor(dust_color);
        particle.display();
        
		// Z indicator
		particle = new ParticleBuilder(ParticleEffect.REDSTONE, error.current_item.location);
		particle.setOffsetZ(offset.getBlockZ());
		particle.setAmount(amount);
        particle.setColor(dust_color);
        particle.display();
	}

	
	@Override
	public void run() {
		
		// Cancel the task if the time has expired, or if the autobuild status has been changed
		if (visual_time <= 0) {

			error.player.sendTitle("", "", 0, 1, 0);
			cancel();
			
		// Keep running if it hasn't
		} else {
			
			// If the error isn't resolved
			if (!error.isResolved()) {
				
				// Decrement the time we'll continue to display this for
				visual_time--;
			
				// Create particles
				visualise(
					settings.getContainerValueAsInt("unresolved_error_r") * 25,
					settings.getContainerValueAsInt("unresolved_error_g") * 25,
					settings.getContainerValueAsInt("unresolved_error_b") * 25);
	
			
			// If the error has been resolved
			} else {
	
				// Generate main/sub titles
				String main_title = error.getErrorTitle(true);
				String sub_title;
				
				int blocks_left = 0;
				
				for (BlockError new_error : ListBlockErrors.block_errors.get(error.player)) {
					if (!new_error.isResolved()) {
						blocks_left++;
					}
				}
				
				sub_title = getSubTitle(blocks_left);
				
				// Send the titles
				error.player.sendTitle(
						main_title, sub_title, 
						2, 
						settings.getContainerValueAsInt("resolved_error_time") * 20, 
						2);
				
				// Generate particles
				visualise(
						settings.getContainerValueAsInt("resolved_error_r") * 25,
						settings.getContainerValueAsInt("resolved_error_g") * 25,
						settings.getContainerValueAsInt("resolved_error_b") * 25);
				
				// Schedule a new task to visualise the next error if auto-build is enabled
				if (ListPlayerSettings.getPlayerSettings(error.player.getUniqueId()).auto_build_enabled) {
					for (BlockError new_error : ListBlockErrors.block_errors.get(error.player)) {
						if (!new_error.isResolved()) {
							ManagerBlockErrorVisualisation.addError(new_error);
							break;
						}
					}
				}
				
				// Cancel the task; the error has been resolved
				cancel();
			}
		}
	}
}
