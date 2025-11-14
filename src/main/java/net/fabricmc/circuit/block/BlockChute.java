package net.fabricmc.circuit.block;

import btw.inventory.util.InventoryUtils;
import net.fabricmc.circuit.tileentity.ChuteTileEntity;
import net.minecraft.src.*;

public class BlockChute extends BlockContainer {

    public BlockChute(int id) {
        super(id, Material.iron);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setStepSound(Block.soundMetalFootstep);
        this.setUnlocalizedName("ccBlockChute");
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.initBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ChuteTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int facing = determineOrientation(entity);
        world.setBlockMetadataWithNotify(x, y, z, facing, 2);
    }

    private int determineOrientation(EntityLivingBase entity) {
        if (entity.rotationPitch > 60 || !entity.isSneaking()) {
            return 0;
        }

        // 返回朝向 (2=北, 3=南, 4=西, 5=东, 0=下, 1=上)
        int yaw = (int)(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        return switch (yaw) {
            case 0 -> 2; // 北
            case 1 -> 5; // 东
            case 2 -> 3; // 南
            case 3 -> 4; // 西
            default -> 0; // 下
        };
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && entity instanceof EntityItem entityItem) {
            float chuteHeight = 1.0f;
            AxisAlignedBB collectionZone = AxisAlignedBB.getAABBPool().getAABB(x, (float)y + chuteHeight, z, x + 1, (float)y + chuteHeight + 0.05f, z + 1);
            if (entityItem.boundingBox.intersectsWith(collectionZone) && !entityItem.isDead) {
                ChuteTileEntity chuteTileEntity = (ChuteTileEntity) world.getBlockTileEntity(x, y, z);
                if (InventoryUtils.addItemStackToInventoryInSlotRange(chuteTileEntity, entityItem.getEntityItem(), 0, 0)) {
                    world.playAuxSFX(2231, x, y, z, 0);
                    entityItem.setDead();
                }
            }
        }
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, int iBlockID, int iMetadata) {
        InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory) world.getBlockTileEntity(i, j, k));
        super.breakBlock(world, i, j, k, iBlockID, iMetadata);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}