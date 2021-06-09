package me.idra.multiblocksystem.bases;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.idra.multiblocksystem.managers.ManagerPlugin;



public abstract class BaseUserInterface implements Listener {

	private final Inventory inv;
	protected final Player player;
	
	
	
	public BaseUserInterface (Player player, int size, String title) {
		
		this.player = player;
		
		inv = Bukkit.createInventory(null, size, title);
		
		ManagerPlugin.plugin.getServer().getPluginManager().registerEvents(this, ManagerPlugin.plugin);
	}
	
	
	
    public void display() {
        player.openInventory(inv);
    }
	
	protected void setItem(int slot, Material material, String name, String... lore) {
		
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }
	
	
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		
        if (event.getInventory() != inv)
        	return;
        
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) 
        	return;
        
        onClick(player, event.getRawSlot(), event.getClick());
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent event) {
    	
        if (event.getInventory().equals(inv))
          event.setCancelled(true);
    }

    
    
	protected abstract void onClick(Player player, int slot, ClickType click);
}
