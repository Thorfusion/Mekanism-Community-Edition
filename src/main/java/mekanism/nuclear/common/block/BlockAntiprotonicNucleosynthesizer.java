package mekanism.nuclear.common.block;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import mekanism.common.Mekanism;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.tile.TileEntityAntiprotonicNucleosynthesizer;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Native six-face block presentation for the Nucleosynthesizer. */
public class BlockAntiprotonicNucleosynthesizer extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon frontIcon;
    @SideOnly(Side.CLIENT)
    private IIcon backIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockAntiprotonicNucleosynthesizer()
    {
        super(Material.iron);
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        String base = MekanismNuclear.MODID
              + ":model/antiprotonic_nucleosynthesizer_";
        frontIcon = register.registerIcon(base + "front");
        backIcon = register.registerIcon(base + "back");
        sideIcon = register.registerIcon(base + "side");
        topIcon = register.registerIcon(base + "top");
        bottomIcon = register.registerIcon(base + "bottom");
        blockIcon = frontIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return iconForSide(side, ForgeDirection.NORTH.ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        int facing = tile instanceof TileEntityAntiprotonicNucleosynthesizer
              ? ((TileEntityAntiprotonicNucleosynthesizer)tile).facing
              : ForgeDirection.NORTH.ordinal();
        return iconForSide(side, facing);
    }

    @SideOnly(Side.CLIENT)
    private IIcon iconForSide(int side, int facing)
    {
        if (side == ForgeDirection.DOWN.ordinal())
        {
            return bottomIcon;
        }
        if (side == ForgeDirection.UP.ordinal())
        {
            return topIcon;
        }
        if (side == facing)
        {
            return frontIcon;
        }
        ForgeDirection direction = ForgeDirection.getOrientation(facing);
        if (direction != ForgeDirection.UNKNOWN
              && side == direction.getOpposite().ordinal())
        {
            return backIcon;
        }
        return sideIcon;
    }

    @Override
    public int getRenderType()
    {
        return 0;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityAntiprotonicNucleosynthesizer();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityAntiprotonicNucleosynthesizer))
        {
            return;
        }
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4F / 360F + 0.5D) & 3;
        int facing = side == 0 ? 2 : side == 1 ? 5 : side == 2 ? 3 : 4;
        TileEntityAntiprotonicNucleosynthesizer machine =
              (TileEntityAntiprotonicNucleosynthesizer)tile;
        machine.setFacing((short)facing);
        machine.redstone = world.isBlockIndirectlyGettingPowered(x, y, z);
        if (placer instanceof EntityPlayer)
        {
            machine.getSecurity().setOwner(
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
        if (world.isRemote)
        {
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityAntiprotonicNucleosynthesizer))
        {
            return false;
        }

        TileEntityAntiprotonicNucleosynthesizer machine =
              (TileEntityAntiprotonicNucleosynthesizer)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, machine))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }
            if (player.isSneaking())
            {
                dismantleBlock(world, x, y, z);
                return true;
            }
            Item tool = held.getItem();
            if (MekanismUtils.isBCWrench(tool))
            {
                ((IToolWrench)tool).wrenchUsed(player, x, y, z);
            }
            int change = ForgeDirection.ROTATION_MATRIX[
                  ForgeDirection.UP.ordinal()][machine.facing];
            machine.setFacing((short)change);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            return true;
        }

        if (!player.isSneaking())
        {
            if (SecurityUtils.canAccess(player, machine))
            {
                player.openGui(MekanismNuclear.instance, 3,
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

    private void dismantleBlock(World world, int x, int y, int z)
    {
        world.setBlockToAir(x, y, z);
        EntityItem dropped = new EntityItem(world, x + 0.5D, y + 0.5D,
              z + 0.5D, new ItemStack(this));
        world.spawnEntityInWorld(dropped);
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
        return tile instanceof TileEntityAntiprotonicNucleosynthesizer
              ? ((TileEntityAntiprotonicNucleosynthesizer)tile)
                    .getRedstoneLevel() : 0;
    }

}
