package mekanism.ultimate.common.block;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import mekanism.common.Mekanism;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
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
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class BlockDimensionalStabilizer extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    @SideOnly(Side.CLIENT)
    private IIcon topIcon;

    @SideOnly(Side.CLIENT)
    private IIcon activeTopIcon;

    @SideOnly(Side.CLIENT)
    private IIcon activeSideIcon;

    public BlockDimensionalStabilizer()
    {
        super(Material.iron);
        setBlockName("DimensionalStabilizer");
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        String base = "mekanismultimate:dimensional_stabilizer/";
        blockIcon = register.registerIcon(base + "side");
        bottomIcon = register.registerIcon(base + "bottom");
        topIcon = register.registerIcon(base + "top");
        activeSideIcon = register.registerIcon(base + "side_active");
        activeTopIcon = register.registerIcon(base + "top_active");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 0 ? bottomIcon : side == 1 ? topIcon : blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        boolean active = tile instanceof TileEntityDimensionalStabilizer
              && ((TileEntityDimensionalStabilizer)tile).getActive();
        if (side == 0)
        {
            return bottomIcon;
        }
        if (side == 1)
        {
            return active ? activeTopIcon : topIcon;
        }
        return active ? activeSideIcon : blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityDimensionalStabilizer();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityDimensionalStabilizer))
        {
            return;
        }
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4F / 360F + 0.5D) & 3;
        TileEntityDimensionalStabilizer stabilizer =
              (TileEntityDimensionalStabilizer)tile;
        stabilizer.setFacing((short)(side == 0 ? 2 : side == 1 ? 5
              : side == 2 ? 3 : 4));
        stabilizer.redstone = world.isBlockIndirectlyGettingPowered(
              x, y, z);
        if (placer instanceof EntityPlayer)
        {
            stabilizer.getSecurity().setOwner(
                  ((EntityPlayer)placer).getCommandSenderName());
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
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityDimensionalStabilizer))
        {
            return false;
        }
        TileEntityDimensionalStabilizer stabilizer =
              (TileEntityDimensionalStabilizer)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, stabilizer))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }
            if (player.isSneaking())
            {
                if (!world.isRemote)
                {
                    world.setBlockToAir(x, y, z);
                    world.spawnEntityInWorld(new EntityItem(world,
                          x + 0.5D, y + 0.5D, z + 0.5D,
                          new ItemStack(this)));
                }
            }
            else if (!world.isRemote)
            {
                stabilizer.setFacing((short)ForgeDirection.ROTATION_MATRIX[
                      ForgeDirection.UP.ordinal()][stabilizer.facing]);
            }
            Item item = held.getItem();
            if (MekanismUtils.isBCWrench(item))
            {
                ((IToolWrench)item).wrenchUsed(player, x, y, z);
            }
            return true;
        }
        if (!player.isSneaking())
        {
            if (!world.isRemote)
            {
                if (SecurityUtils.canAccess(player, stabilizer))
                {
                    player.openGui(MekanismUltimate.instance, 7, world,
                          x, y, z);
                }
                else
                {
                    SecurityUtils.displayNoAccess(player);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block,
          int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
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
            ItemStack drop = stored.copy();
            if (stored.hasTagCompound())
            {
                drop.setTagCompound((NBTTagCompound)stored
                      .getTagCompound().copy());
            }
            world.spawnEntityInWorld(new EntityItem(world,
                  x + 0.15D + random.nextFloat() * 0.7D,
                  y + 0.15D + random.nextFloat() * 0.7D,
                  z + 0.15D + random.nextFloat() * 0.7D, drop));
            inventory.setInventorySlotContents(slot, null);
        }
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return true;
    }
}
