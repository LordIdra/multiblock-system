package me.idra.multiblocksystem.tasks;

import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import me.idra.multiblocksystem.lists.ListPlayerSettings;
import me.idra.multiblocksystem.objects.PlayerSettings;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;



public class TaskVisualiseLocation extends BukkitRunnable{
	
	PlayerSettings player_settings;
	Location loc;
	Location offset;
	public int visual_time;
	public Color color;
	
	
	
	public TaskVisualiseLocation(Player player, Location in_loc) {
		super();

		// Player settings
		player_settings = ListPlayerSettings.getPlayerSettings(player.getUniqueId());
		
		// Time we should display this for
		visual_time = player_settings.location_particle_time;
		
		// Where we should display it
		loc = in_loc.add(new Location(in_loc.getWorld(), 0.5, 0.5, 0.5));
		
		// What colour we'll visualise the block with
		color = new Color(player_settings.location_r * 25, player_settings.location_g * 25, player_settings.location_b * 25);
	}
	

	
	@Override
	public void run() {
		
		// Cancel task if necessary
		if (visual_time <= 0) {
			cancel();
			
		// If task should still be running
		} else {
			
			// Decrement time we'll continue to display this for
			visual_time--;
			
			// Start generating particles
			ParticleBuilder particle = new ParticleBuilder(ParticleEffect.REDSTONE, loc);

			// Load amount of particles from config, and set colour of particles
			particle.setAmount(player_settings.location_particle_amount);
	        particle.setColor(color);
	        
	        // Display particles
	        particle.display();
		}
	}
}
