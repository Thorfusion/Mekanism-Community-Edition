package mekanism.ultimate.common.block;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import mekanism.common.Mekanism;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.item.ItemBlockPersonalBarrel;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/** Secure, portable 54-slot personal storage. */
public final class BlockPersonalBarrel extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;

    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockPersonalBarrel()
    {
        super(Material.wood);
        setBlockName("PersonalBarrel");
        setHardness(2.5F);
        setResistance(6000000F);
        setStepSound(soundTypeWood);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        String base = "mekanismultimate:personal_barrel/";
        blockIcon = register.registerIcon(base + "side");
        topIcon = register.registerIcon(base + "top");
        bottomIcon = register.registerIcon(base + "bottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 0 ? bottomIcon : side == 1 ? topIcon : blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityPersonalBarrel();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBasicBlock)
        {
            int side = MathHelper.floor_double(
                  placer.rotationYaw * 4F / 360F + 0.5D) & 3;
            ((TileEntityBasicBlock)tile).setFacing((short)(side == 0 ? 2
                  : side == 1 ? 5 : side == 2 ? 3 : 4));
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
          EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPersonalBarrel))
        {
            return false;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && MekanismUtils.hasUsableWrench(player, x, y, z))
        {
            if (!SecurityUtils.canAccess(player, tile))
            {
                SecurityUtils.displayNoAccess(player);
                return true;
            }
            if (!world.isRemote && player.isSneaking())
            {
                ItemStack drop = createDrop(world, x, y, z);
                world.setBlockToAir(x, y, z);
                spawnDrop(world, x, y, z, drop);
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
                if (SecurityUtils.canAccess(player, tile))
                {
                    player.openGui(MekanismUltimate.instance, 3, world,
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
    public ItemStack getPickBlock(MovingObjectPosition target, World world,
          int x, int y, int z, EntityPlayer player)
    {
        return createDrop(world, x, y, z);
    }

    private ItemStack createDrop(World world, int x, int y, int z)
    {
        ItemStack drop = new ItemStack(this);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityPersonalBarrel
              && drop.getItem() instanceof ItemBlockPersonalBarrel)
        {
            ItemBlockPersonalBarrel item =
                  (ItemBlockPersonalBarrel)drop.getItem();
            item.setInventory(((ISustainedInventory)tile).getInventory(),
                  drop);
            item.setOwner(drop,
                  ((ISecurityTile)tile).getSecurity().getOwner());
            item.setSecurity(drop,
                  ((ISecurityTile)tile).getSecurity().getMode());
        }
        return drop;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
          int metadata, int fortune)
    {
        return world.isAirBlock(x, y, z)
              ? new ArrayList<ItemStack>()
              : new ArrayList<ItemStack>(Arrays.asList(
                    createDrop(world, x, y, z)));
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x,
          int y, int z, boolean willHarvest)
    {
        if (!SecurityUtils.canAccess(player, world.getTileEntity(x, y, z)))
        {
            return false;
        }
        if (!player.capabilities.isCreativeMode && !world.isRemote
              && willHarvest)
        {
            spawnDrop(world, x, y, z, createDrop(world, x, y, z));
        }
        return world.setBlockToAir(x, y, z);
    }

    private static void spawnDrop(World world, int x, int y, int z,
          ItemStack stack)
    {
        Random random = world.rand;
        EntityItem entity = new EntityItem(world,
              x + 0.15D + random.nextFloat() * 0.7D,
              y + 0.15D + random.nextFloat() * 0.7D,
              z + 0.15D + random.nextFloat() * 0.7D, stack);
        world.spawnEntityInWorld(entity);
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
    public float getExplosionResistance(Entity entity, World world, int x,
          int y, int z, double explosionX, double explosionY,
          double explosionZ)
    {
        return -1F;
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
        return tile instanceof TileEntityPersonalBarrel
              ? Container.calcRedstoneFromInventory(
                    (TileEntityPersonalBarrel)tile) : 0;
    }
}
