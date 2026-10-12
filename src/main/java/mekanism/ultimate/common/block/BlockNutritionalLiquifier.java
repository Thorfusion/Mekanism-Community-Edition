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
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Native 1.7 block shell for the Nutritional Liquifier. */
public final class BlockNutritionalLiquifier extends BlockContainer
      implements ILegacyJsonModelBlock
{
    private int renderType;

    public BlockNutritionalLiquifier()
    {
        super(Material.iron);
        setBlockName("NutritionalLiquifier");
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(
              MekanismUltimate.MODID.toLowerCase()
                    + ":nutritional_liquifier");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityNutritionalLiquifier();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityNutritionalLiquifier))
        {
            return;
        }
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4F / 360F + 0.5D) & 3;
        int facing = side == 0 ? 2 : side == 1 ? 5 : side == 2 ? 3 : 4;
        TileEntityNutritionalLiquifier liquifier =
              (TileEntityNutritionalLiquifier)tile;
        liquifier.setFacing((short)facing);
        liquifier.redstone = world.isBlockIndirectlyGettingPowered(x, y, z);
        if (placer instanceof EntityPlayer)
        {
            liquifier.getSecurity().setOwner(
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
        if (!(tile instanceof TileEntityNutritionalLiquifier))
        {
            return false;
        }
        TileEntityNutritionalLiquifier liquifier =
              (TileEntityNutritionalLiquifier)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, liquifier))
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
            liquifier.setFacing((short)ForgeDirection.ROTATION_MATRIX[
                  ForgeDirection.UP.ordinal()][liquifier.facing]);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            return true;
        }
        if (!player.isSneaking())
        {
            if (SecurityUtils.canAccess(player, liquifier))
            {
                player.openGui(MekanismUltimate.instance, 1,
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
        return tile instanceof TileEntityNutritionalLiquifier
              ? ((TileEntityNutritionalLiquifier)tile).getRedstoneLevel() : 0;
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
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public int getRenderType()
    {
        return renderType;
    }

    @Override
    public String getLegacyModel(int metadata)
    {
        return MekanismUltimate.MODID.toLowerCase()
              + ":block/nutritional_liquifier";
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
