package defense.common.block;

import java.util.List;
import java.util.Random;

import mekanism.common.Tier.BaseTier;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ITier;
import defense.client.render.block.BlockRenderingHandler;
import defense.common.DefenseTechBlocks;
import defense.common.DefenseTechItems;
import defense.common.base.IBlockActivate;
import defense.common.item.ItemBlockMachine;
import defense.common.tile.TileCruiseLauncher;
import defense.common.tile.TileEMPTower;
import defense.common.tile.TileLauncherBase;
import defense.common.tile.TileLauncherFrame;
import defense.common.tile.TileLauncherScreen;
import defense.common.tile.TileRadarStation;

public class BlockMachine extends BlockBase
{
    public enum MachineData
    {
        LauncherBase("LauncherBase", TileLauncherBase.class, true),
        LauncherScreen("LauncherScreen", TileLauncherScreen.class, true),
        LauncherFrame("LauncherFrame", TileLauncherFrame.class, true),
        RadarStation("RadarStation", TileRadarStation.class, false),
        EmpTower("EmpTower", TileEMPTower.class, false),
        CruiseLauncher("CruiseLauncher", TileCruiseLauncher.class, false);

        public String unlocalized;
        public Class<? extends TileEntity> tileEntity;
        public boolean hasTier;

        MachineData(String s, Class<? extends TileEntity> tile, boolean tier)
        {
        	unlocalized = s;
            tileEntity = tile;
            hasTier = tier;
        }

        public static MachineData get(int id)
        {
            if(id < MachineData.values().length && id >= 0)
            {
                return MachineData.values()[id];
            }

            return null;
        }
    }

    public BlockMachine()
    {
        super("machine", Material.iron);
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		if(!world.isRemote)
		{
			if(tileEntity instanceof TileEntityBasicBlock)
			{
				((TileEntityBasicBlock)tileEntity).onAdded();
			}
		}
	}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack)
    {
    	TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);
		int side = MathHelper.floor_double((entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int height = Math.round(entityliving.rotationPitch);
		int change = 3;

		if(tileEntity == null)
		{
			return;
		}

		if(tileEntity.canSetFacing(0) && tileEntity.canSetFacing(1))
		{
			if(height >= 65)
			{
				change = 1;
			}
			else if(height <= -65)
			{
				change = 0;
			}
		}

		if(change != 0 && change != 1)
		{
			switch(side)
			{
				case 0: change = 2; break;
				case 1: change = 5; break;
				case 2: change = 3; break;
				case 3: change = 4; break;
			}
		}

		tileEntity.setFacing((short)change);
        
        if(tileEntity instanceof IBoundingBlock)
		{
			((IBoundingBlock)tileEntity).onPlace();
		}
    }

    public static boolean canBePlacedAt(World world, int x, int y, int z, int m, int side)
    {
        ForgeDirection d = MekanismUtils.getRight(side);

        if(m == MachineData.LauncherBase.ordinal())
        {
            for(int yp = 0; yp <= 2; yp++)
            {
            	Block b = world.getBlock(x + d.offsetX, y + yp, z + d.offsetZ);
            	
                if(!b.isReplaceable(world, x + d.offsetX, y + yp, z + d.offsetZ))
                    return false;
                if(!b.isReplaceable(world, x - d.offsetX, y + yp, z - d.offsetZ))
                    return false;
            }
            
            return world.getBlock(x, y, z).isReplaceable(world, x, y, z);
        }
        else if(m == MachineData.LauncherFrame.ordinal())
        {
            return world.getBlock(x, y - 1, z).getMaterial().isSolid() && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z);
        }
        else if(m == MachineData.EmpTower.ordinal())
        {
            return world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z);
        }

        return world.getBlock(x, y - 1, z).getMaterial().isSolid();
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        int direction = 0;
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityBasicBlock)
        {
            direction = ((TileEntityBasicBlock) tileEntity).facing;
        }

        return canBePlacedAt(world, x, y, z, world.getBlockMetadata(x, y, z), direction);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);

			if(tileEntity instanceof TileEntityBasicBlock)
			{
				((TileEntityBasicBlock)tileEntity).onNeighborChange(block);
			}
		}
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if(player.inventory.getCurrentItem() != null)
        {
            if(player.inventory.getCurrentItem().getItem() == DefenseTechItems.itemLaserDesignator)
            {
                return false;
            }
            else if(player.inventory.getCurrentItem().getItem() == DefenseTechItems.itemRadarGun)
            {
                return false;
            }
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if(tileEntity != null)
        {
            if(tileEntity instanceof IBlockActivate)
            {
                return ((IBlockActivate)tileEntity).onActivated(player);
            }
        }

        return false;
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        return this.onMachineActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }
    
    @Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		if(!player.capabilities.isCreativeMode && !world.isRemote && canHarvestBlock(player, world.getBlockMetadata(x, y, z)))
		{
			TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);

			float motion = 0.7F;
			double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
			double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
			double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

			EntityItem entityItem = new EntityItem(world, x + motionX, y + motionY, z + motionZ, getPickBlock(null, world, x, y, z, player));

			world.spawnEntityInWorld(entityItem);
		}

		return world.setBlockToAir(x, y, z);
	}

    /** If this block doesn't render as an ordinary block it will return False (examples: signs,
     * buttons, stairs, etc) */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity tileEntity = (TileEntity)world.getTileEntity(x, y, z);

		if(tileEntity instanceof IBoundingBlock)
		{
			((IBoundingBlock)tileEntity).onBreak();
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if(MachineData.get(metadata) != null)
        {
            try {
                return MachineData.get(metadata).tileEntity.newInstance();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return BlockRenderingHandler.ID;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(MachineData data : MachineData.values())
        {
        	ItemStack stack = new ItemStack(this, 1, data.ordinal());
        	par3List.add(stack);
        	
        	if(data.hasTier)
        	{
        		ItemStack tier2 = stack.copy();
        		((ItemBlockMachine)tier2.getItem()).setBaseTier(tier2, BaseTier.ADVANCED);
        		par3List.add(tier2);
        		
        		ItemStack tier3 = stack.copy();
        		((ItemBlockMachine)tier3.getItem()).setBaseTier(tier3, BaseTier.ELITE);
        		par3List.add(tier3);
        	}
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntityBasicBlock tileEntity = (TileEntityBasicBlock)world.getTileEntity(x, y, z);
        ItemStack stack = new ItemStack(DefenseTechBlocks.blockMachine, 1, world.getBlockMetadata(x, y, z));
        
        if(tileEntity instanceof ITier)
        {
        	((ItemBlockMachine)stack.getItem()).setBaseTier(stack, BaseTier.values()[((ITier)tileEntity).getTier()]);
        }
        
        return stack;
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    public static String getJiQiMing(TileEntity tileEntity)
    {
    	ItemStack stack = tileEntity.getBlockType().getPickBlock(null, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, null);
    	return stack.getDisplayName();
    }
}
