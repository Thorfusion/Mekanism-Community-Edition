package defense.common.block;

import java.util.Random;

import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.CreativeTabHandler;
import defense.common.Reference;
import defense.common.base.IRedstoneProvider;

public class BlockBase extends Block implements ITileEntityProvider
{
    protected IIcon iconTop, iconSide, iconBottom;
    protected boolean requireSidedTextures = false;

    public BlockBase(String name)
    {
    	this(name, Material.iron);
    }
    
    public BlockBase(String name, Material material)
    {
        super(material);
        setBlockName(Reference.PREFIX + name);
        setBlockTextureName(Reference.PREFIX + name);
        setHardness(0.6F);
        setCreativeTab(CreativeTabHandler.INSTANCE);
        isBlockContainer = true;
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
        this.dropEntireInventory(world, x, y, z, par5, par6);
        super.breakBlock(world, x, y, z, par5, par6);
        world.removeTileEntity(x, y, z);
    }
    
    /** Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it
     * on to the tile entity at this location. Args: world, x, y, z, blockID, EventID, event
     * parameter */
    @Override
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }

    /** Override this if you don't need it. This will eject all items out of this machine if it has
     * an inventory. */
    public void dropEntireInventory(World world, int x, int y, int z, Block par5, int par6)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity != null)
        {
            if (tileEntity instanceof IInventory)
            {
                IInventory inventory = (IInventory) tileEntity;

                for (int i = 0; i < inventory.getSizeInventory(); ++i)
                {
                    ItemStack var7 = inventory.getStackInSlot(i);

                    if (var7 != null)
                    {
                        Random random = new Random();
                        float var8 = random.nextFloat() * 0.8F + 0.1F;
                        float var9 = random.nextFloat() * 0.8F + 0.1F;
                        float var10 = random.nextFloat() * 0.8F + 0.1F;

                        while (var7.stackSize > 0)
                        {
                            int var11 = random.nextInt(21) + 10;

                            if (var11 > var7.stackSize)
                            {
                                var11 = var7.stackSize;
                            }

                            var7.stackSize -= var11;
                            EntityItem var12 = new EntityItem(world, (x + var8), (y + var9), (z + var10), new ItemStack(var7.getItem(), var11, var7.getItemDamage()));

                            if (var7.hasTagCompound())
                            {
                                var12.getEntityItem().setTagCompound((NBTTagCompound) var7.getTagCompound().copy());
                            }

                            float var13 = 0.05F;
                            var12.motionX = ((float) random.nextGaussian() * var13);
                            var12.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
                            var12.motionZ = ((float) random.nextGaussian() * var13);
                            world.spawnEntityInWorld(var12);

                            if (var7.stackSize <= 0)
                            {
                                inventory.setInventorySlotContents(i, null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /** DO NOT OVERRIDE THIS FUNCTION! Called when the block is right clicked by the player. This
     * modified version detects electric items and wrench actions on your machine block. Do not
     * override this function. Use onMachineActivated instead! (It does the same thing)
     * 
     * @param world The World Object.
     * @param x , y, z The coordinate of the block.
     * @param side The side the player clicked on.
     * @param hitX , hitY, hitZ The position the player clicked on relative to the block. */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
    	ItemStack tool = entityPlayer.inventory.getCurrentItem();
        int metadata = world.getBlockMetadata(x, y, z);

        /** Check if the player is holding a wrench or an electric item. If so, call the wrench
         * event. */
        if (MekanismUtils.hasUsableWrench(entityPlayer, x, y, z))
        {
        	if(MekanismUtils.isBCWrench(tool.getItem()))
			{
				((IToolWrench)tool.getItem()).wrenchUsed(entityPlayer, x, y, z);
			}
        	
            if (entityPlayer.isSneaking())
            {
                if (this.onSneakUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ))
                {
                    return true;
                }
            }

            if (this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ))
            {
                return true;
            }

            return false;
        }

        if (entityPlayer.isSneaking())
        {
            if (this.onSneakMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ))
            {
                return true;
            }
        }

        return this.onMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }

    /** Called when the machine is right clicked by the player
     * 
     * @return True if something happens */
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /** Called when the machine is being wrenched by a player while sneaking.
     * 
     * @return True if something happens */
    public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /** Called when a player uses a wrench on the machine
     * 
     * @return True if some happens */
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    /** Called when a player uses a wrench on the machine while sneaking. Only works with the UE
     * wrench.
     * 
     * @return True if some happens */
    public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        return this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        super.registerBlockIcons(iconRegister);

        if (this.requireSidedTextures)
        {
            this.iconTop = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_top");
            this.iconSide = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_side");
            this.iconBottom = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_bottom");
        }
    }

    /** Is this block powering the block on the specified side */
    @Override
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        TileEntity tileEntity = par1IBlockAccess.getTileEntity(x, y, z);

        if (tileEntity instanceof IRedstoneProvider)
        {
            return ((IRedstoneProvider) tileEntity).isPoweringTo(ForgeDirection.getOrientation(side)) ? 15 : 0;
        }

        return 0;
    }

    /** Is this block indirectly powering the block on the specified side */
    @Override
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        TileEntity tileEntity = par1IBlockAccess.getTileEntity(x, y, z);

        if (tileEntity instanceof IRedstoneProvider)
        {
            return ((IRedstoneProvider) tileEntity).isIndirectlyPoweringTo(ForgeDirection.getOrientation(side)) ? 15 : 0;
        }

        return 0;
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) 
	{
		return null;
	}
}
