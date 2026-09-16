package defense.common.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mekanism.api.Pos3D;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ExplosionEvent.ExplosivePreDetonationEvent;
import defense.api.ExplosiveType;
import defense.client.render.tile.RenderBombBlock;
import defense.common.CreativeTabHandler;
import defense.common.entity.EntityExplosive;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.tile.TileExplosive;

public class BlockExplosive extends BlockBase
{
    private final Map<Integer, IIcon[]> explosiveIcons = new HashMap<Integer, IIcon[]>();

    public BlockExplosive()
    {
        super("explosives", Material.tnt);
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        setCreativeTab(CreativeTabHandler.INSTANCE);
    }

    /** gets the way this piston should face for that entity that placed it. */
    private static byte determineOrientation(World world, int x, int y, int z, EntityLivingBase entityLiving)
    {
        if(entityLiving != null)
        {
            if(MathHelper.abs((float) entityLiving.posX - x) < 2.0F && MathHelper.abs((float) entityLiving.posZ - z) < 2.0F)
            {
                double var5 = entityLiving.posY + 1.82D - entityLiving.yOffset;

                if(var5 - y > 2.0D)
                {
                    return 1;
                }

                if(y - var5 > 0.0D)
                {
                    return 0;
                }
            }

            int rotation = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            return (byte)(rotation == 0 ? 2 : (rotation == 1 ? 5 : (rotation == 2 ? 3 : (rotation == 3 ? 4 : 0))));
        }
        
        return 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
        TileEntity tileEntity = par1IBlockAccess.getTileEntity(x, y, z);

        if(tileEntity != null)
        {
            if(tileEntity instanceof TileExplosive)
            {
                if(((TileExplosive)tileEntity).explosiveID == Explosive.sMine.getID())
                {
                    setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
                    return;
                }
            }
        }

        setBlockBounds(0, 0, 0, 1f, 1f, 1f);
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0, 0, 0, 1f, 1f, 1f);
    }

    /** Returns a bounding box from the pool of bounding boxes (this means this box can change after
     * the pool has been cleared to be reused) */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
    {
        TileEntity tileEntity = par1World.getTileEntity(x, y, z);

        if(tileEntity != null)
        {
            if(tileEntity instanceof TileExplosive)
            {
                if(((TileExplosive)tileEntity).explosiveID == Explosive.sMine.getID())
                {
                    return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + 0.2, z + maxZ);
                }
            }
        }

        return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
    }

    /** Called when the block is placed in the world. */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        ((TileExplosive)world.getTileEntity(x, y, z)).explosiveID = itemStack.getItemDamage();
        int explosiveID = ((TileExplosive) world.getTileEntity(x, y, z)).explosiveID;

        if(!world.isRemote)
        {
            ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, x, y, z, ExplosiveType.BLOCK, ExplosiveRegistry.get(explosiveID));
            MinecraftForge.EVENT_BUS.post(evt);

            if(evt.isCanceled())
            {
                dropBlockAsItem(world, x, y, z, explosiveID, 0);
                world.setBlockToAir(x, y, z);
                return;
            }
        }

        world.setBlockMetadataWithNotify(x, y, z, MekanismUtils.getBaseOrientation(ForgeDirection.NORTH.ordinal(), determineOrientation(world, x, y, z, entityLiving)), 2);

        if(world.isBlockIndirectlyGettingPowered(x, y, z))
        {
            BlockExplosive.detonate(world, x, y, z, explosiveID, 0);
        }

        // Check to see if there is fire nearby.
        // If so, then detonate.
        for(byte i = 0; i < 6; i++)
        {
            Pos3D position = new Pos3D(x, y, z);
            position.translate(ForgeDirection.getOrientation(i), 1);

            Block block = position.getCoord(world.provider.dimensionId).getBlock(world);

            if(block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava)
            {
                BlockExplosive.detonate(world, x, y, z, explosiveID, 2);
            }
        }

        if(entityLiving != null)
        {
            FMLLog.fine(entityLiving.getCommandSenderName() + " placed " + ExplosiveRegistry.get(explosiveID).getExplosiveName() + " in: " + x + ", " + y + ", " + z + ".");
        }
    }

    /** Returns the block texture based on the side being looked at. Args: side */
    @Override
    public IIcon getIcon(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        TileEntity tileEntity = par1IBlockAccess.getTileEntity(x, y, z);
        return tileEntity instanceof TileExplosive ? getIcon(side, ((TileExplosive)tileEntity).explosiveID) : Blocks.tnt.getIcon(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int explosiveID)
    {
        IIcon[] icons = explosiveIcons.get(explosiveID);
        if(icons == null)
        {
            return Blocks.tnt.getIcon(side, 0);
        }

        return icons[side == 0 ? 0 : side == 1 ? 1 : 2];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        explosiveIcons.clear();

        for(Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            if(explosive.hasBlockForm() && explosive != Explosive.sMine)
            {
                String base = "explosive_" + explosive.getUnlocalizedName() + "_";
                explosiveIcons.put(explosive.getID(), new IIcon[] {
                        iconRegister.registerIcon("defense:" + base + "bottom"),
                        iconRegister.registerIcon("defense:" + base + "top"),
                        iconRegister.registerIcon("defense:" + base + "side")
                });
            }
        }
    }

    @Override
    public void onBlockAdded(World par1World, int x, int y, int z)
    {
        super.onBlockAdded(par1World, x, y, z);

        par1World.func_147479_m(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        int explosiveID = ((TileExplosive) world.getTileEntity(x, y, z)).explosiveID;

        if (world.isBlockIndirectlyGettingPowered(x, y, z))
        {
            BlockExplosive.detonate(world, x, y, z, explosiveID, 0);
        }
        else if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava)
        {
            BlockExplosive.detonate(world, x, y, z, explosiveID, 2);
        }
    }

    /*
     * Called to detonate the TNT. Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional,
     * 1, exploded, 2 burned)
     */
    public static void detonate(World world, int x, int y, int z, int explosiveID, int causeOfExplosion)
    {
        if(!world.isRemote)
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);

            if(tileEntity != null)
            {
                if(tileEntity instanceof TileExplosive)
                {
                    ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, x, y, z, ExplosiveType.BLOCK, ExplosiveRegistry.get(((TileExplosive) tileEntity).explosiveID));
                    MinecraftForge.EVENT_BUS.post(evt);

                    if(!evt.isCanceled())
                    {
                        ((TileExplosive)tileEntity).exploding = true;
                        EntityExplosive eZhaDan = new EntityExplosive(world, new Pos3D(x, y, z).translate(0.5, 0.5, 0.5), ((TileExplosive)tileEntity).explosiveID, (byte)world.getBlockMetadata(x, y, z), ((TileExplosive)tileEntity).nbtData);

                        switch(causeOfExplosion)
                        {
                            case 2:
                                eZhaDan.setFire(100);
                                break;
                        }

                        world.spawnEntityInWorld(eZhaDan);
                        world.setBlockToAir(x, y, z);
                    }
                }
            }
        }
    }

    /** Called upon the block being destroyed by an explosion */
    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
    {
        if(world.getTileEntity(x, y, z) != null)
        {
            int explosiveID = ((TileExplosive) world.getTileEntity(x, y, z)).explosiveID;
            BlockExplosive.detonate(world, x, y, z, explosiveID, 1);
        }

        super.onBlockExploded(world, x, y, z, explosion);
    }

    /** Called upon block activation (left or right click on the block.). The three integers
     * represent x,y,z of the block. */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if(entityPlayer.getCurrentEquippedItem() != null)
        {
            if(entityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
            {
                int explosiveID = ((TileExplosive) tileEntity).explosiveID;
                BlockExplosive.detonate(world, x, y, z, explosiveID, 0);
                return true;
            }
            else if(MekanismUtils.hasUsableWrench(entityPlayer, x, y, z))
            {
                byte change = 3;

                // Reorient the block
                switch(world.getBlockMetadata(x, y, z))
                {
                    case 0:
                        change = 2;
                        break;
                    case 2:
                        change = 5;
                        break;
                    case 5:
                        change = 3;
                        break;
                    case 3:
                        change = 4;
                        break;
                    case 4:
                        change = 1;
                        break;
                    case 1:
                        change = 0;
                        break;
                }

                world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(change).ordinal(), 3);
                world.notifyBlockChange(x, y, z, this);
               
                return true;
            }
        }

        if(tileEntity instanceof TileExplosive)
        {
            return ExplosiveRegistry.get(((TileExplosive)tileEntity).explosiveID).onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return RenderBombBlock.ID;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        if(world.getTileEntity(x, y, z) != null)
        {
            int explosiveID = ((TileExplosive)world.getTileEntity(x, y, z)).explosiveID;

            return new ItemStack(this, 1, explosiveID);
        }

        return null;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if(tileEntity != null)
        {
            if(tileEntity instanceof TileExplosive)
            {
                if(!((TileExplosive)tileEntity).exploding)
                {
                    int explosiveID = ((TileExplosive)tileEntity).explosiveID;
                    Item item = getItemDropped(world.getBlockMetadata(x, y, z), world.rand, 0);

                    dropBlockAsItem(world, x, y, z, new ItemStack(item, 1, explosiveID));
                }
            }
        }

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            if(explosive.hasBlockForm())
            {
                par3List.add(new ItemStack(par1, 1, explosive.getID()));
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        return new TileExplosive();
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
}
