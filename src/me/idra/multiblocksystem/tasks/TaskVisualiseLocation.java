package me.idra.multiblocksystem.tasks;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import me.idra.multiblocksystem.managers.ManagerPlugin;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;



public class TaskVisualiseLocation extends BukkitRunnable{
	
	Location loc;
	Location offset;
	public int visual_time;
	public Color color;
	
	
	
	public TaskVisualiseLocation(Location in_loc, int in_visual_time) {
		super();
		
		// Time we should display this for
		visual_time = in_visual_time;
		
		// Where we should display it
		loc = in_loc.add(new Location(in_loc.getWorld(), 0.5, 0.5, 0.5));
		
		// What colour we'll visualise the block with
		color = new Color(
				ManagerPlugin.config.getInt("LocationVisualisation.particle-color.r"),
				ManagerPlugin.config.getInt("LocationVisualisation.particle-color.g"),
				ManagerPlugin.config.getInt("LocationVisualisation.particle-color.b"));
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
			particle.setAmount(ManagerPlugin.config.getInt("LocationVisualisation.particle-amount"));
	        particle.setColor(color);
	        
	        // Display particles
	        particle.display();
		}
	}
}
