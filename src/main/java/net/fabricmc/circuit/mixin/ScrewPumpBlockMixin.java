package net.fabricmc.circuit.mixin;

import btw.block.blocks.ScrewPumpBlock;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScrewPumpBlock.class)
public class ScrewPumpBlockMixin {
    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z"))
    public boolean setWater(World world, int i, int j, int k, int iBlockID, int iMetadata) {
        return world.setBlock(i, j, k, Block.waterStill.blockID);
    }
}
