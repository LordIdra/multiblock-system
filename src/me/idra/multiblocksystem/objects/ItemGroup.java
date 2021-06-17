package me.idra.multiblocksystem.objects;

import java.util.List;

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



    String name;
    String tag;
    List<ItemStack> stacks;
}