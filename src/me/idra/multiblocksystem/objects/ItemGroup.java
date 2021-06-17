package me.idra.multiblocksystem.objects;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;



public class ItemGroup {

    public ItemGroup(String name, String tag, List<ItemStack> stacks) {
        this.name = name;
        this.tag = tag;
        this.stacks = stacks;
    }

    public ItemGroup(String name, List<ItemStack> stacks) {
        this(name, null, stacks);
    }



    public boolean containsMaterial(Material material) {

        for (ItemStack stack : stacks) {
            if (stack != null && stack.getType() == material) {
                return true;
            }
        }

        return false;
    }



    String name;
    String tag;
    List<ItemStack> stacks;
}