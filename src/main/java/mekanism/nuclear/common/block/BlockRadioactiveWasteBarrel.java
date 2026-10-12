package mekanism.nuclear.common.block;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.tile.TileEntityRadioactiveWasteBarrel;
import mekanism.ultimate.common.block.ILegacyJsonModelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/** Radioactive-only vertical storage with stable barrel bounds and behavior. */
public class BlockRadioactiveWasteBarrel extends BlockContainer
      implements ILegacyJsonModelBlock
{
    private int renderType;
    @SideOnly(Side.CLIENT)
    private IIcon barrelIcon;

    public BlockRadioactiveWasteBarrel()
    {
        super(Material.iron);
        setBlockBounds(0.125F, 0, 0.125F, 0.875F, 1, 0.875F);
        setHardness(3.5F);
        setResistance(8F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        barrelIcon = register.registerIcon(
              MekanismNuclear.MODID + ":models/radioactive_waste_barrel");
        blockIcon = barrelIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return barrelIcon;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
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
        return MekanismNuclear.MODID + ":block/radioactive_waste_barrel";
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
        return metadata == 3 ? 180 : metadata == 5 ? 90
              : metadata == 4 ? -90 : 0;
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        this.renderType = renderType;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityRadioactiveWasteBarrel();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        int side = MathHelper.floor_double(
              placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int facing = side == 0 ? 2 : side == 1 ? 5 : side == 2 ? 3 : 4;
        world.setBlockMetadataWithNotify(x, y, z, facing, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
          EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityRadioactiveWasteBarrel))
        {
            return false;
        }

        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (world.isRemote)
            {
                return true;
            }
            Item tool = held.getItem();
            if (MekanismUtils.isBCWrench(tool))
            {
                ((IToolWrench)tool).wrenchUsed(player, x, y, z);
            }
            world.setBlockToAir(x, y, z);
            world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D,
                  z + 0.5D, new ItemStack(this)));
            return true;
        }

        if (player.isSneaking())
        {
            if (!world.isRemote)
            {
                TileEntityRadioactiveWasteBarrel barrel =
                      (TileEntityRadioactiveWasteBarrel)tile;
                if (barrel.getStoredGas() == null)
                {
                    player.addChatMessage(new ChatComponentTranslation("gui.empty"));
                }
                else
                {
                    player.addChatMessage(new ChatComponentTranslation(
                          "tooltip.RadioactiveWasteBarrel.stored",
                          barrel.getStoredGas().getLocalizedName(), barrel.getStored(),
                          barrel.getCapacity()));
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
    public void breakBlock(World world, int x, int y, int z,
          Block block, int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntityRadioactiveWasteBarrel)
        {
            ((TileEntityRadioactiveWasteBarrel)tile).releaseContentsToRadiation();
        }
        super.breakBlock(world, x, y, z, block, metadata);
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
        return tile instanceof TileEntityRadioactiveWasteBarrel
              ? ((TileEntityRadioactiveWasteBarrel)tile).getRedstoneLevel() : 0;
    }
}
