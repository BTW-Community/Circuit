package net.fabricmc.circuit;

import btw.crafting.recipe.RecipeManager;
import net.fabricmc.circuit.block.CircuitBlock;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class CircuitInitializer {

    public static void initMod() {
        addCraftingRecipes();
    }

    private static void addCraftingRecipes(){
        RecipeManager.addRecipe(new ItemStack(CircuitBlock.chute, 1), new Object[]{"# #", "# #", "# #", '#', Item.ingotIron});
    }
}
