package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import mekanism.common.CTMData;
import mekanism.common.Mekanism;
import mekanism.common.base.IBlockCTM;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.content.fission.FissionReactorLogicMode;
import mekanism.nuclear.common.tile.TileEntityFissionReactorLogicAdapter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Configurable redstone input/output face for a Fission Reactor. */
public class BlockFissionReactorLogicAdapter extends BlockContainer
      implements IBlockCTM
{
    private CTMData ctm;

    public BlockFissionReactorLogicAdapter()
    {
        super(Material.iron);
        setHardness(3.5F);
        setResistance(16F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        ctm = new CTMData(MekanismNuclear.MODID
              + ":fission_reactor_logic_adapter", this, Arrays.asList(0))
              .addOtherBlockConnectivities(
                    NuclearBlocks.FissionReactorCasing, Arrays.asList(0))
              .addOtherBlockConnectivities(
                    NuclearBlocks.FissionReactorPort, Arrays.asList(0))
              .registerIcons(register);
        blockIcon = ctm.getIcon(0);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
          EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityFissionReactorLogicAdapter)
            {
                FissionReactorLogicMode mode =
                      ((TileEntityFissionReactorLogicAdapter)tile).cycleMode();
                player.addChatMessage(new ChatComponentTranslation(
                      "fission.mekanismnuclear.logic_mode",
                      new ChatComponentTranslation(
                            "fission.mekanismnuclear.logic_mode."
                                  + mode.getName())));
            }
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z,
          Block neighbor)
    {
        if (!world.isRemote)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityFissionReactorLogicAdapter)
            {
                ((TileEntityFissionReactorLogicAdapter)tile).updateLogic();
            }
        }
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z,
          int side)
    {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
          int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityFissionReactorLogicAdapter
              ? ((TileEntityFissionReactorLogicAdapter)tile).getRedstoneLevel(
                    ForgeDirection.getOrientation(side)) : 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityFissionReactorLogicAdapter();
    }

    @Override
    public int getRenderType()
    {
        return Mekanism.proxy.CTM_RENDER_ID;
    }

    @Override
    public CTMData getCTMData(IBlockAccess world, int x, int y, int z,
          int metadata)
    {
        return ctm;
    }

    @Override
    public boolean shouldRenderBlock(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return true;
    }
}
