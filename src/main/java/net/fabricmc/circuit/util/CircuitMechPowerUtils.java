package net.fabricmc.circuit.util;

import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.block.blocks.AxleBlock;
import btw.block.blocks.GearBoxBlock;
import btw.block.util.MechPowerUtils;
import btw.world.util.BlockPos;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class CircuitMechPowerUtils {
    public static boolean isBlockPoweredByGearBoxToSide(World world, int i, int j, int k, int iSide) {
        BlockPos targetPos = new BlockPos(i, j, k);
        targetPos.addFacingAsOffset(iSide);
        int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
        return CircuitMechPowerUtils.isBlockIDGearBox(iTargetBlockID) &&
                // (gearBoxBlock = (GearBoxBlock)Block.blocksList[iTargetBlockID]).canInputAxlePowerToFacing(world, targetPos.x, targetPos.y, targetPos.z, iSide) &&
                ((GearBoxBlock)Block.blocksList[iTargetBlockID]).isGearBoxOn(world, targetPos.x, targetPos.y, targetPos.z);
    }

    public static boolean isBlockPoweredByGearBox(World world, int i, int j, int k, MechanicalBlock block) {
        for (int iFacing = 0; iFacing <= 5; ++iFacing) {
            if (!block.canInputAxlePowerToFacing(world, i, j, k, iFacing) || !CircuitMechPowerUtils.isBlockPoweredByGearBoxToSide(world, i, j, k, iFacing)) continue;
            return true;
        }
        return false;
    }

    public static boolean isBlockIDGearBox(int iBlockID) {
        return iBlockID == BTWBlocks.gearBox.blockID;
    }
}
