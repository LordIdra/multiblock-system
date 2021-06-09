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
	
	BlockError error;
	public int visual_time;
	public Vector offset;
	public int amount;
	public boolean originally_was_autobuild = false;
	PlayerSettings settings;
	
	
	public TaskVisualiseError(BlockError error, int visual_time) {
		super();
		
		// Set direct variables
		this.error = error;
		this.visual_time = visual_time;
		
		// Get particle offset from config
		settings = ListPlayerSettings.getPlayerSettings(error.player.getUniqueId());
		
		offset = new Vector(
				 settings.error_offset_x,
				 settings.error_offset_y,
				 settings.error_offset_z);
		amount = settings.error_particle_amount;
		
		// Find if auto-build is currently enabled
		if (settings.auto_build_running)
			originally_was_autobuild = true;
		
		// Generate main/sub titles
		String main_title = error.getErrorTitle(false);
		String sub_title;
		
		int blocks_left = 0;
		
		for (BlockError new_error : ListBlockErrors.block_errors.get(error.player)) {
			if (!new_error.isResolved()) {
				blocks_left++;
			}
		}
		
		if (error.isResolved())
			sub_title = ChatColor.GREEN + String.valueOf(blocks_left) + ChatColor.YELLOW + " blocks left";
		else
			sub_title = ChatColor.DARK_RED + String.valueOf(blocks_left) + ChatColor.YELLOW + " blocks left";
		
		// Send the titles
		error.player.sendTitle(
				main_title, sub_title, 
				2, 
				settings.unresolved_error_time, 
				2);
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
		
		} else if (originally_was_autobuild != ListPlayerSettings.getPlayerSettings(error.player.getUniqueId()).auto_build_running) {
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
					settings.unresolved_error_r,
					settings.unresolved_error_g,
					settings.unresolved_error_b);
	
			
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
				
				if (error.isResolved())
					sub_title = ChatColor.GREEN + String.valueOf(blocks_left) + ChatColor.YELLOW + " blocks left";
				else
					sub_title = ChatColor.DARK_RED + String.valueOf(blocks_left) + ChatColor.YELLOW + " blocks left";
				
				// Send the titles
				error.player.sendTitle(
						main_title, sub_title, 
						2, 
						settings.resolved_error_time, 
						2);
				
				// Generate particles
				visualise(
						2,
						settings.resolved_error_time,
						2);
				
				// Schedule a new task to visualise the next error if auto-build is enabled
				if (ListPlayerSettings.getPlayerSettings(error.player.getUniqueId()).auto_build_running) {
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
