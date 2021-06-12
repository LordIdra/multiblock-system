package me.idra.multiblocksystem.objects;

import java.util.UUID;



public class PlayerSettings {
	
	public UUID player;
	public boolean auto_build_enabled = true;
	
	public int unresolved_error_time = 15;
	public int resolved_error_time = 1;
	public int error_particle_amount = 50;
	
	public int location_particle_time = 5;
	public int location_particle_amount = 5;
	
	public int list_items_per_page = 8;
	
	public int error_offset_x = 8;
	public int error_offset_y = 8;
	public int error_offset_z = 8;
	
	public int unresolved_error_r = 10;
	public int unresolved_error_g = 0;
	public int unresolved_error_b = 0;

	public int resolved_error_r = 0;
	public int resolved_error_g = 10;
	public int resolved_error_b = 0;
	
	public int location_r = 0;
	public int location_g = 0;
	public int location_b = 10;
}
