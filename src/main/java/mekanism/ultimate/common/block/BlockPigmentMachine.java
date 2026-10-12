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
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Native rotating block shell shared by the Ultimate pigment machines. */
public final class BlockPigmentMachine extends BlockContainer
      implements ILegacyJsonModelBlock
{
    private final MachineType machineType;
    private final IIcon[] icons = new IIcon[12];
    private int legacyRenderType;

    public BlockPigmentMachine(MachineType type)
    {
        super(Material.iron);
        machineType = type;
        setBlockName(type.unlocalizedName);
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    public MachineType getMachineType()
    {
        return machineType;
    }

    public int getGuiId()
    {
        return machineType.guiId;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
          int z)
    {
        /* The mixer JSON is a two-block-tall model. The other pigment
         * machines remain ordinary full cubes. */
        setBlockBounds(0F, 0F, 0F, 1F,
              machineType == MachineType.MIXER ? 2F : 1F, 1F);
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        String domain = MekanismUltimate.MODID.toLowerCase();
        String path = machineType.texturePath;
        if (machineType == MachineType.MIXER)
        {
            IIcon inactive = register.registerIcon(domain + ":" + path
                  + "/base");
            IIcon active = register.registerIcon(domain + ":" + path
                  + "/base_active");
            for (int side = 0; side < 6; side++)
            {
                icons[side] = inactive;
                icons[side + 6] = active;
            }
        }
        else
        {
            icons[0] = register.registerIcon(domain + ":" + path
                  + "/bottom");
            icons[1] = register.registerIcon(domain + ":" + path
                  + "/top");
            icons[2] = register.registerIcon(domain + ":" + path
                  + "/front");
            icons[3] = register.registerIcon(domain + ":" + path
                  + "/back");
            icons[4] = register.registerIcon(domain + ":" + path
                  + "/left");
            icons[5] = register.registerIcon(domain + ":" + path
                  + "/right");
            icons[6] = icons[0];
            icons[7] = icons[1];
            icons[8] = register.registerIcon(domain + ":" + path
                  + "/front_active");
            icons[9] = icons[3];
            icons[10] = register.registerIcon(domain + ":" + path
                  + "/left_active");
            icons[11] = register.registerIcon(domain + ":" + path
                  + "/right_active");
        }
        blockIcon = icons[2];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side >= 0 && side < 6 && icons[side] != null
              ? icons[side] : blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPigmentMachine))
        {
            return getIcon(side, 0);
        }
        TileEntityPigmentMachine machine = (TileEntityPigmentMachine)tile;
        int orientation = MekanismUtils.getBaseOrientation(side,
              machine.facing);
        return icons[orientation + (machine.getActive() ? 6 : 0)];
    }

    @Override
    public boolean isOpaqueCube()
    {
        return machineType != MachineType.MIXER;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return machineType != MachineType.MIXER;
    }

    @Override
    public int getRenderType()
    {
        return machineType == MachineType.MIXER ? legacyRenderType : 0;
    }

    @Override
    public String getLegacyModel(int metadata)
    {
        return MekanismUltimate.MODID.toLowerCase()
              + ":block/pigment_mixer";
    }

    @Override
    public String getLegacyModel(IBlockAccess world, int x, int y, int z,
          int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityPigmentMixer
              && ((TileEntityPigmentMixer)tile).getActive()
              ? MekanismUltimate.MODID.toLowerCase()
                    + ":block/pigment_mixer_active"
              : getLegacyModel(metadata);
    }

    @Override
    public String[] getLegacyModels()
    {
        return new String[] {getLegacyModel(0),
              MekanismUltimate.MODID.toLowerCase()
                    + ":block/pigment_mixer_active"};
    }

    @Override
    public int getLegacyModelRotationX(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return 0;
    }

    @Override
    public int getLegacyModelRotationY(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPigmentMachine))
        {
            return 0;
        }
        switch (((TileEntityPigmentMachine)tile).facing)
        {
            case 3:
                return 180;
            case 4:
                return -90;
            case 5:
                return 90;
            default:
                return 0;
        }
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        legacyRenderType = renderType;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return machineType.createTile();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPigmentMachine))
        {
            return;
        }
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4F / 360F + 0.5D) & 3;
        int facing = side == 0 ? 2 : side == 1 ? 5 : side == 2 ? 3 : 4;
        TileEntityPigmentMachine machine =
              (TileEntityPigmentMachine)tile;
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
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPigmentMachine))
        {
            return false;
        }
        TileEntityPigmentMachine machine =
              (TileEntityPigmentMachine)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, machine))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }
            if (world.isRemote)
            {
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
            machine.setFacing((short)ForgeDirection.ROTATION_MATRIX[
                  ForgeDirection.UP.ordinal()][machine.facing]);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            return true;
        }

        if (!player.isSneaking())
        {
            if (!world.isRemote)
            {
                if (SecurityUtils.canAccess(player, machine))
                {
                    player.openGui(MekanismUltimate.instance,
                          machineType.guiId, world, x, y, z);
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
        if (tile instanceof TileEntityPigmentExtractor)
        {
            return ((TileEntityPigmentExtractor)tile).getRedstoneLevel();
        }
        if (tile instanceof TileEntityPigmentMixer)
        {
            return ((TileEntityPigmentMixer)tile).getRedstoneLevel();
        }
        return tile instanceof TileEntityPaintingMachine
              ? ((TileEntityPaintingMachine)tile).getRedstoneLevel() : 0;
    }

    public enum MachineType
    {
        EXTRACTOR("PigmentExtractor", "pigment_extractor", 4),
        MIXER("PigmentMixer", "pigment_mixer", 5),
        PAINTING("PaintingMachine", "painting_machine", 6);

        private final String unlocalizedName;
        private final String texturePath;
        private final int guiId;

        MachineType(String unlocalizedName, String texturePath, int guiId)
        {
            this.unlocalizedName = unlocalizedName;
            this.texturePath = texturePath;
            this.guiId = guiId;
        }

        private TileEntityPigmentMachine createTile()
        {
            switch (this)
            {
                case EXTRACTOR:
                    return new TileEntityPigmentExtractor();
                case MIXER:
                    return new TileEntityPigmentMixer();
                case PAINTING:
                    return new TileEntityPaintingMachine();
                default:
                    throw new IllegalStateException(
                          "Unknown pigment machine: " + this);
            }
        }
    }
}
