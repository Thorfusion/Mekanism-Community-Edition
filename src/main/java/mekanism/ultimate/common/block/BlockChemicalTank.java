package mekanism.ultimate.common.block;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import mekanism.common.Mekanism;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** One fixed-tier modern Chemical Tank block identity. */
public final class BlockChemicalTank extends BlockContainer
      implements ILegacyJsonModelBlock
{
    private final ChemicalTankTier tier;
    private int renderType;

    @SideOnly(Side.CLIENT)
    private IIcon tankIcon;

    @SideOnly(Side.CLIENT)
    private IIcon valveIcon;

    public BlockChemicalTank(ChemicalTankTier tier)
    {
        super(Material.iron);
        this.tier = tier == null ? ChemicalTankTier.BASIC : tier;
        setBlockName("ChemicalTank" + this.tier.getBaseTier().getName());
        setBlockBounds(3F / 16F, 0F, 3F / 16F,
              13F / 16F, 1F, 13F / 16F);
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    public ChemicalTankTier getTier()
    {
        return tier;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        String domain = MekanismUltimate.MODID.toLowerCase();
        tankIcon = register.registerIcon(domain + ":models/chemical_tank");
        valveIcon = register.registerIcon(domain + ":models/chemical_tank_"
              + tier.getName() + "_valve");
        blockIcon = tankIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 0 || side == 1 ? valveIcon : tankIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityChemicalTank(tier);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityChemicalTank))
        {
            return;
        }
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4F / 360F + 0.5D) & 3;
        int facing = side == 0 ? 2 : side == 1 ? 5 : side == 2 ? 3 : 4;
        TileEntityChemicalTank tank = (TileEntityChemicalTank)tile;
        tank.setTier(tier);
        tank.setFacing((short)facing);
        tank.redstone = world.isBlockIndirectlyGettingPowered(x, y, z);
        if (placer instanceof EntityPlayer)
        {
            tank.getSecurity().setOwner(
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
    public float getPlayerRelativeBlockHardness(EntityPlayer player,
          World world, int x, int y, int z)
    {
        return SecurityUtils.canAccess(player, world.getTileEntity(x, y, z))
              ? super.getPlayerRelativeBlockHardness(player, world, x, y, z)
              : 0F;
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
        if (!(tile instanceof TileEntityChemicalTank))
        {
            return false;
        }
        TileEntityChemicalTank tank = (TileEntityChemicalTank)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, tank))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }
            if (player.isSneaking())
            {
                dismantleBlock(world, x, y, z, false);
                return true;
            }
            Item tool = held.getItem();
            if (MekanismUtils.isBCWrench(tool))
            {
                ((IToolWrench)tool).wrenchUsed(player, x, y, z);
            }
            tank.setFacing((short)ForgeDirection.ROTATION_MATRIX[
                  ForgeDirection.UP.ordinal()][tank.facing]);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            return true;
        }
        if (!player.isSneaking())
        {
            if (SecurityUtils.canAccess(player, tank))
            {
                player.openGui(MekanismUltimate.instance, 2,
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
        ItemStack drop = new ItemStack(this);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityChemicalTank)
              || !(drop.getItem() instanceof ItemBlockChemicalTank))
        {
            return drop;
        }
        TileEntityChemicalTank tank = (TileEntityChemicalTank)tile;
        ItemBlockChemicalTank item = (ItemBlockChemicalTank)drop.getItem();
        item.setStoredChemical(drop, tank.chemicalTank.getStack());
        item.setDumpMode(drop, tank.dumping);

        ISecurityItem securityItem = item;
        securityItem.setOwner(drop,
              ((ISecurityTile)tank).getSecurity().getOwner());
        securityItem.setSecurity(drop,
              ((ISecurityTile)tank).getSecurity().getMode());

        if (drop.stackTagCompound == null)
        {
            drop.setTagCompound(new NBTTagCompound());
        }
        ((ISideConfiguration)tank).getConfig().write(
              drop.stackTagCompound);
        ((ISideConfiguration)tank).getEjector().write(
              drop.stackTagCompound);
        item.setInventory(((ISustainedInventory)tank).getInventory(), drop);
        return drop;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
          int metadata, int fortune)
    {
        if (world.isAirBlock(x, y, z))
        {
            return new ArrayList<ItemStack>();
        }
        return new ArrayList<ItemStack>(Arrays.asList(
              getPickBlock(null, world, x, y, z, null)));
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player,
          int x, int y, int z, boolean willHarvest)
    {
        if (!player.capabilities.isCreativeMode && !world.isRemote
              && willHarvest)
        {
            spawnDrop(world, x, y, z,
                  getPickBlock(null, world, x, y, z, player));
        }
        return world.setBlockToAir(x, y, z);
    }

    public ItemStack dismantleBlock(World world, int x, int y, int z,
          boolean returnBlock)
    {
        ItemStack stack = getPickBlock(null, world, x, y, z, null);
        world.setBlockToAir(x, y, z);
        if (!returnBlock)
        {
            spawnDrop(world, x, y, z, stack);
        }
        return stack;
    }

    private static void spawnDrop(World world, int x, int y, int z,
          ItemStack stack)
    {
        float motion = 0.7F;
        double motionX = world.rand.nextFloat() * motion
              + (1F - motion) * 0.5D;
        double motionY = world.rand.nextFloat() * motion
              + (1F - motion) * 0.5D;
        double motionZ = world.rand.nextFloat() * motion
              + (1F - motion) * 0.5D;
        world.spawnEntityInWorld(new EntityItem(world, x + motionX,
              y + motionY, z + motionZ, stack));
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
        return MekanismUltimate.MODID.toLowerCase()
              + ":block/chemical_tank/" + tier.getName();
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
        return facingRotation(tile instanceof TileEntityBasicBlock
              ? ((TileEntityBasicBlock)tile).facing : 2);
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        this.renderType = renderType;
    }

    private static int facingRotation(int facing)
    {
        return facing == 3 ? 180 : facing == 5 ? 90
              : facing == 4 ? -90 : 0;
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
        return tile instanceof TileEntityChemicalTank
              ? ((TileEntityChemicalTank)tile).getRedstoneLevel() : 0;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y,
          int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        ForgeDirection[] valid = new ForgeDirection[6];
        if (tile instanceof TileEntityBasicBlock)
        {
            TileEntityBasicBlock basic = (TileEntityBasicBlock)tile;
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                if (basic.canSetFacing(direction.ordinal()))
                {
                    valid[direction.ordinal()] = direction;
                }
            }
        }
        return valid;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z,
          ForgeDirection axis)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBasicBlock
              && ((TileEntityBasicBlock)tile).canSetFacing(axis.ordinal()))
        {
            ((TileEntityBasicBlock)tile).setFacing((short)axis.ordinal());
            return true;
        }
        return false;
    }
}
