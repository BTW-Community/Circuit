package net.fabricmc.circuit.mixin;

import btw.block.BTWBlocks;
import btw.block.tileentity.MillstoneTileEntity;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MillstoneTileEntity.class)
public class MillstoneTileEntityMixin {
    @Redirect(method = "grindContents", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/MillstoneTileEntity;ejectStackOnMilled(Lnet/minecraft/src/ItemStack;)V"))
    public void outStackOnMilled(MillstoneTileEntity millstoneTile, ItemStack stack) {
        int x = millstoneTile.xCoord;
        int y = millstoneTile.yCoord - 1;
        int z = millstoneTile.zCoord;
        if (millstoneTile.worldObj.getBlockId(x, y, z) == BTWBlocks.hopper.blockID) {
            TileEntity tileEntity = millstoneTile.worldObj.getBlockTileEntity(x, y, z);
            if (tileEntity instanceof IInventory hopperInventory) {
                boolean into = InventoryUtils.addItemStackToInventory(hopperInventory, stack);
                if (into) {
                    return;
                }
            }
        }
        millstoneTile.ejectStackOnMilled(stack);
    }
}
