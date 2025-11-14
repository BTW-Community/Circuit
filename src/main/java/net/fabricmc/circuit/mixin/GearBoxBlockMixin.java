package net.fabricmc.circuit.mixin;

import btw.block.MechanicalBlock;
import btw.block.blocks.GearBoxBlock;
import btw.block.util.MechPowerUtils;
import net.fabricmc.circuit.util.CircuitMechPowerUtils;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GearBoxBlock.class)
public class GearBoxBlockMixin {
    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lbtw/block/util/MechPowerUtils;isBlockPoweredByAxle(Lnet/minecraft/src/World;IIILbtw/block/MechanicalBlock;)Z"))
    public boolean isBlockPoweredByGearBoxToSide(World world, int i, int j, int k, MechanicalBlock block) {
        return MechPowerUtils.isBlockPoweredByAxle(world, i, j, k, block) ||
                CircuitMechPowerUtils.isBlockPoweredByGearBox(world, i, j, k, block);
    }

    @Redirect(method = "isInputtingMechanicalPower", at = @At(value = "INVOKE", target = "Lbtw/block/util/MechPowerUtils;isBlockPoweredByAxleToSide(Lnet/minecraft/src/World;IIII)Z"))
    public boolean isBlockPoweredByGearBoxToSide1(World world, int i, int j, int k, int face) {
        return MechPowerUtils.isBlockPoweredByAxleToSide(world, i, j, k, face) ||
                CircuitMechPowerUtils.isBlockPoweredByGearBoxToSide(world, i, j, k, face);
    }
}
