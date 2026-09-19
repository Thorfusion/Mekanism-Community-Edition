package mekanism.ultimate.common.block;

import java.util.List;

import buildcraft.api.tools.IToolWrench;
import mekanism.api.MekanismConfig.ultimate;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DefIcon;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.base.IFactory;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.BlockMachine;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockUltimateFactory extends BlockMachine
{
    public BlockUltimateFactory()
    {
        super(MachineBlock.MACHINE_BLOCK_1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        BASE_ICON = register.registerIcon("mekanism:SteelCasing");

        for (RecipeType type : RecipeType.values())
        {
            MekanismRenderer.loadDynamicTextures(register,
                "factory/ultimate/" + type.getUnlocalizedName().toLowerCase() + "/" + BaseTier.ULTIMATE.getName() + type.getUnlocalizedName() + "Factory",
                factoryIcons[FactoryTier.ULTIMATE.ordinal()][type.ordinal()],
                DefIcon.getActivePair(register.registerIcon("mekanism:factory/ultimate/UltimateFactoryFront"), 2).setOverrides(false),
                DefIcon.getActivePair(register.registerIcon("mekanism:factory/ultimate/UltimateFactoryTop"), 1).setOverrides(false),
                DefIcon.getActivePair(register.registerIcon("mekanism:factory/ultimate/UltimateFactoryBottom"), 0).setOverrides(false),
                DefIcon.getActivePair(register.registerIcon("mekanism:factory/ultimate/UltimateFactorySide"), 3, 4, 5).setOverrides(false));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int side)
    {
        int recipe = ((IFactory)stack.getItem()).getRecipeType(stack);
        if (recipe < 0 || recipe >= RecipeType.values().length)
        {
            recipe = 0;
        }
        return factoryIcons[FactoryTier.ULTIMATE.ordinal()][recipe][side];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return factoryIcons[FactoryTier.ULTIMATE.ordinal()][RecipeType.SMELTING.ordinal()][side];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityFactory))
        {
            return getIcon(side, 0);
        }

        TileEntityFactory factory = (TileEntityFactory)tile;
        boolean active = MekanismUtils.isActive(world, x, y, z);
        int orientation = MekanismUtils.getBaseOrientation(side, factory.facing);
        return factoryIcons[FactoryTier.ULTIMATE.ordinal()][factory.recipeType.ordinal()][orientation + (active ? 6 : 0)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        if (!ultimate.factoryEnabled)
        {
            return;
        }

        for (RecipeType type : RecipeType.values())
        {
            ItemStack stack = new ItemStack(item);
            ((IFactory)stack.getItem()).setRecipeType(type.ordinal(), stack);
            list.add(stack);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityUltimateFactory();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityUltimateFactory();
    }

    @Override
    public int damageDropped(int metadata)
    {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float posX, float posY, float posZ)
    {
        if (world.isRemote)
        {
            return true;
        }

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityUltimateFactory))
        {
            return false;
        }

        TileEntityUltimateFactory factory = (TileEntityUltimateFactory)tile;
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, factory))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }

            if (player.isSneaking())
            {
                dismantleBlock(world, x, y, z, false);
                return true;
            }

            if (MekanismUtils.isBCWrench(held.getItem()))
            {
                ((IToolWrench)held.getItem()).wrenchUsed(player, x, y, z);
            }

            int change = ForgeDirection.ROTATION_MATRIX[ForgeDirection.UP.ordinal()][factory.facing];
            factory.setFacing((short)change);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            return true;
        }

        if (!player.isSneaking())
        {
            if (SecurityUtils.canAccess(player, factory))
            {
                player.openGui(MekanismUltimate.instance, 0, world, x, y, z);
            }
            else
            {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }

        return false;
    }
}
