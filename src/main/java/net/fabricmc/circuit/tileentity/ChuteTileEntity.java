package net.fabricmc.circuit.tileentity;

import btw.inventory.util.InventoryUtils;
import net.minecraft.src.*;

public class ChuteTileEntity extends TileEntity implements IInventory {
    private ItemStack[] inventory = new ItemStack[1];
    private String customName;

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[0];
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        // 每8 tick执行一次操作
        if (worldObj.getTotalWorldTime() % 8 == 0) {
            // 1. 尝试从上方容器吸取物品
            tryPullFromAbove();

            // 2. 尝试输出物品
            if (this.getStackInSlot(0) != null) {
                tryPushItem(metadata);
            }
        }
    }

    /**
     * 从上方容器吸取物品
     */
    private void tryPullFromAbove() {
        // 如果当前槽位已满，不吸取
        ItemStack currentStack = this.getStackInSlot(0);
        if (currentStack != null && currentStack.stackSize >= currentStack.getMaxStackSize()) {
            return;
        }

        // 获取上方方块的TileEntity
        TileEntity tileAbove = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);

        if (tileAbove instanceof IInventory invAbove) {

            // 从上方容器随机选择一个有物品的槽位
            int sourceSlot = InventoryUtils.getRandomOccupiedStackInRange(
                    invAbove,
                    worldObj.rand,
                    0,
                    invAbove.getSizeInventory() - 1
            );

            if (sourceSlot >= 0) {
                // 获取源物品栈
                ItemStack sourceStack = invAbove.getStackInSlot(sourceSlot);

                if (sourceStack != null) {
                    // 尝试转移一个物品到当前容器
                    if (currentStack == null) {
                        // 当前槽位为空，直接放入一个物品
                        ItemStack transferStack = sourceStack.copy();
                        transferStack.stackSize = 1;
                        this.setInventorySlotContents(0, transferStack);

                        InventoryUtils.decreaseStackSize(invAbove, sourceSlot, 1);
                    } else if (currentStack.itemID == sourceStack.itemID &&
                            currentStack.getItemDamage() == sourceStack.getItemDamage() &&
                            ItemStack.areItemStackTagsEqual(currentStack, sourceStack) &&
                            currentStack.stackSize < currentStack.getMaxStackSize()) {
                        // 当前槽位有相同物品且未满，合并一个
                        currentStack.stackSize++;
                        InventoryUtils.decreaseStackSize(invAbove, sourceSlot, 1);
                    }

                    this.onInventoryChanged();
                    invAbove.onInventoryChanged();
                }
            }
        }
    }

    /**
     * 尝试输出物品到目标容器或掉落
     */
    private void tryPushItem(int metadata) {
        int[] offset = getOffsetFromMetadata(metadata);

        // 目标坐标
        int targetX = xCoord + offset[0];
        int targetY = yCoord + offset[1];
        int targetZ = zCoord + offset[2];

        // 获取目标位置的TileEntity
        TileEntity targetTile = worldObj.getBlockTileEntity(targetX, targetY, targetZ);

        if (targetTile instanceof IInventory targetInv) {
            // 输出到容器
            ItemStack currentStack = this.getStackInSlot(0);

            if (currentStack != null) {
                // 创建一个只包含1个物品的副本用于转移
                ItemStack transferStack = currentStack.copy();
                transferStack.stackSize = 1;

                // 尝试添加到目标容器
                if (InventoryUtils.addItemStackToInventory(targetInv, transferStack)) {
                    // 成功转移，减少源物品栈
                    InventoryUtils.decreaseStackSize(this, 0, 1);
                    this.onInventoryChanged();
                    targetInv.onInventoryChanged();
                }
            }
        } else {
            // 如果目标不是容器，检查是否可以掉落物品（目标位置为空气或可替换方块）
            Block targetBlock = Block.blocksList[worldObj.getBlockId(targetX, targetY, targetZ)];

            if (targetBlock == null || targetBlock.blockID == 0 ||
                    targetBlock.blockMaterial.isReplaceable()) {
                // 掉落物品
                ItemStack currentStack = this.getStackInSlot(0);
                if (currentStack != null) {
                    ItemStack toEject = currentStack.copy();
                    toEject.stackSize = 1;
                    ejectStack(toEject, offset);

                    InventoryUtils.decreaseStackSize(this, 0, 1);
                    this.onInventoryChanged();
                }
            }
        }
    }

    public static int[] getOffsetFromMetadata(int metadata) {
        return switch (metadata) {
            case 2 -> new int[]{0, 0, -1}; // 北
            case 5 -> new int[]{1, 0, 0}; // 东
            case 3 -> new int[]{0, 0, 1}; // 南
            case 4 -> new int[]{-1, 0, 0}; // 西
            case 0 -> new int[]{0, -1, 0}; // 下
            default -> new int[]{0, 0, 0}; // 无效或默认无偏移
        };
    }

    private void ejectStack(ItemStack stack, int[] offset) {
        float xOffset = this.worldObj.rand.nextFloat() * 0.1f + 0.45f + offset[0];
        float yOffset = -0.35f * offset[1];
        float zOffset = this.worldObj.rand.nextFloat() * 0.1f + 0.45f + offset[2];
        EntityItem entityitem = new EntityItem(this.worldObj, (float)this.xCoord + xOffset, (float)this.yCoord + yOffset, (float)this.zCoord + zOffset, stack);
        entityitem.motionX = 0.0;
        entityitem.motionY = -0.01f;
        entityitem.motionZ = 0.0;
        entityitem.delayBeforeCanPickup = 10;
        this.worldObj.spawnEntityInWorld(entityitem);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (inventory[0] != null) {
            ItemStack stack;

            if (inventory[0].stackSize <= amount) {
                stack = inventory[0];
                inventory[0] = null;
                this.onInventoryChanged();
                return stack;
            } else {
                stack = inventory[0].splitStack(amount);

                if (inventory[0].stackSize == 0) {
                    inventory[0] = null;
                }

                this.onInventoryChanged();
                return stack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (inventory[0] != null) {
            ItemStack stack = inventory[0];
            inventory[0] = null;
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory[0] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    @Override
    public String getInvName() {
        return this.hasCustomName() ? this.customName : "container.chute";
    }

    @Override
    public boolean isInvNameLocalized() {
        return this.hasCustomName();
    }

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openChest() {}

    @Override
    public void closeChest() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        NBTTagList tagList = nbt.getTagList("Items");
        this.inventory = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound)tagList.tagAt(i);
            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }

        if (nbt.hasKey("CustomName")) {
            this.customName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                this.inventory[i].writeToNBT(tag);
                tagList.appendTag(tag);
            }
        }

        nbt.setTag("Items", tagList);

        if (this.hasCustomName()) {
            nbt.setString("CustomName", this.customName);
        }
    }
}