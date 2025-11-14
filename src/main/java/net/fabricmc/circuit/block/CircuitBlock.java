package net.fabricmc.circuit.block;

import net.fabricmc.circuit.tileentity.ChuteTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.TileEntity;

public class CircuitBlock {
    public static Block chute;
    public static void initBlock() {
        chute = new BlockChute(3333);
        Item.itemsList[chute.blockID] = new ItemBlock(chute.blockID - 256);

        createModTileEntityMappings();
    }

    public static void createModTileEntityMappings() {
        TileEntity.addMapping(ChuteTileEntity.class, "ccChute");
    }
}
