package mekanism.mekasuit.common.block;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import mekanism.common.Mekanism;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import mekanism.ultimate.common.block.ILegacyJsonModelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/** Native 1.7.10 block shell for the Modification Station workflow. */
public final class BlockModificationStation extends BlockContainer
      implements ILegacyJsonModelBlock
{
    private static final float MODEL_HEIGHT = 30F / 16F;
    private int renderType;

    public BlockModificationStation()
    {
        super(Material.iron);
        setBlockName("ModificationStation");
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(MekanismMekaSuit.MODID
              + ":models/modification_station");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityModificationStation();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
          int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        int facing = tile instanceof TileEntityBasicBlock
              ? ((TileEntityBasicBlock)tile).facing : 2;
        if (facing == 3)
        {
            setBlockBounds(0F, 0F, 0F, 2F, MODEL_HEIGHT, 1F);
        }
        else if (facing == 5)
        {
            setBlockBounds(0F, 0F, -1F, 1F, MODEL_HEIGHT, 1F);
        }
        else if (facing == 4)
        {
            setBlockBounds(0F, 0F, 0F, 1F, MODEL_HEIGHT, 2F);
        }
        else
        {
            setBlockBounds(-1F, 0F, 0F, 1F, MODEL_HEIGHT, 1F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
          int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
          int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityModificationStation))
        {
            return;
        }
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4F / 360F + 0.5D) & 3;
        int facing = side == 0 ? 2 : side == 1 ? 5 : side == 2 ? 3 : 4;
        TileEntityModificationStation station =
              (TileEntityModificationStation)tile;
        station.setFacing((short)facing);
        station.redstone = world.isBlockIndirectlyGettingPowered(x, y, z);
        if (placer instanceof EntityPlayer)
        {
            station.getSecurity().setOwner(
                  ((EntityPlayer)placer).getCommandSenderName());
        }
        if (!world.isRemote)
        {
            station.onPlace();
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntityBasicBlock)
        {
            ((TileEntityBasicBlock)tile).onAdded();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z,
          Block block)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntityBasicBlock)
        {
            ((TileEntityBasicBlock)tile).onNeighborChange(block);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
          EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityModificationStation))
        {
            return false;
        }
        TileEntityModificationStation station =
              (TileEntityModificationStation)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, station))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }
            if (player.isSneaking())
            {
                world.setBlockToAir(x, y, z);
                world.spawnEntityInWorld(new EntityItem(world, x + 0.5D,
                      y + 0.5D, z + 0.5D, new ItemStack(this)));
                return true;
            }
            Item tool = held.getItem();
            if (MekanismUtils.isBCWrench(tool))
            {
                ((IToolWrench)tool).wrenchUsed(player, x, y, z);
            }
            short newFacing = (short)ForgeDirection.ROTATION_MATRIX[
                  ForgeDirection.UP.ordinal()][station.facing];
            if (!station.canFormBoundingBlocks(newFacing))
            {
                return true;
            }
            station.removeBoundingBlocks();
            station.setFacing(newFacing);
            station.onPlace();
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            return true;
        }

        if (!player.isSneaking())
        {
            if (SecurityUtils.canAccess(player, station))
            {
                player.openGui(MekanismMekaSuit.instance, 0,
                      world, x, y, z);
            }
            else
            {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world,
          int x, int y, int z, EntityPlayer player)
    {
        return new ItemStack(this);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block,
          int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof IBoundingBlock)
        {
            ((IBoundingBlock)tile).onBreak();
        }
        if (!world.isRemote && tile instanceof IInventory)
        {
            dropInventory(world, x, y, z, (IInventory)tile);
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }

    private static void dropInventory(World world, int x, int y, int z,
          IInventory inventory)
    {
        Random random = world.rand;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++)
        {
            ItemStack stored = inventory.getStackInSlot(slot);
            if (stored == null)
            {
                continue;
            }
            while (stored.stackSize > 0)
            {
                int amount = Math.min(stored.stackSize,
                      random.nextInt(21) + 10);
                stored.stackSize -= amount;
                ItemStack split = new ItemStack(stored.getItem(), amount,
                      stored.getItemDamage());
                if (stored.hasTagCompound())
                {
                    split.setTagCompound((NBTTagCompound)stored
                          .getTagCompound().copy());
                }
                EntityItem entity = new EntityItem(world,
                      x + random.nextFloat() * 0.8F + 0.1F,
                      y + random.nextFloat() * 0.8F + 0.1F,
                      z + random.nextFloat() * 0.8F + 0.1F, split);
                entity.motionX = random.nextGaussian() * 0.05D;
                entity.motionY = random.nextGaussian() * 0.05D + 0.2D;
                entity.motionZ = random.nextGaussian() * 0.05D;
                world.spawnEntityInWorld(entity);
            }
            inventory.setInventorySlotContents(slot, null);
        }
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z,
          int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityModificationStation
              ? ((TileEntityModificationStation)tile).getRedstoneLevel() : 0;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return renderType;
    }

    @Override
    public String getLegacyModel(int metadata)
    {
        return MekanismMekaSuit.MODID + ":block/modification_station";
    }

    @Override
    public int getLegacyModelRotationX(net.minecraft.world.IBlockAccess world,
          int x, int y, int z, int metadata)
    {
        return 0;
    }

    @Override
    public int getLegacyModelRotationY(net.minecraft.world.IBlockAccess world,
          int x, int y, int z, int metadata)
    {
        TileEntity tile = world == null ? null : world.getTileEntity(x, y, z);
        int facing = tile instanceof TileEntityBasicBlock
              ? ((TileEntityBasicBlock)tile).facing : 2;
        return facing == 3 ? 180 : facing == 5 ? 90
              : facing == 4 ? -90 : 0;
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        this.renderType = renderType;
    }
}
