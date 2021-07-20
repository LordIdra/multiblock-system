package me.idra.multiblocksystem.tasks;

import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import me.idra.multiblocksystem.helpers.ConstantSettingNames;
import me.idra.multiblocksystem.lists.ListPlayerSettings;
import me.idra.multiblocksystem.objects.PlayerSettings;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;



public class TaskVisualiseLocation extends BukkitRunnable{
	
	PlayerSettings player_settings;
	Location loc;
	public int visual_time;
	public Color color;
	
	
	
	public TaskVisualiseLocation(Player player, Location in_loc) {
		super();

		// Player settings
		player_settings = ListPlayerSettings.getPlayerSettings(player.getUniqueId());
		
		// Time we should display this for
		visual_time = player_settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_PARTICLE_TIME) * 3;
		
		// Where we should display it
		loc = in_loc.add(new Location(in_loc.getWorld(), 0.5, 0.5, 0.5));
		
		// What colour we'll visualise the block with
		color = new Color(
			player_settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_R) * 25, 
			player_settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_G) * 25, 
			player_settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_B) * 25);
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
			particle.setAmount(player_settings.getContainerValueAsInt(ConstantSettingNames.LOCATION_PARTICLE_AMOUNT));
	        particle.setColor(color);
	        
	        // Display particles
	        particle.display();
		}
	}
}
