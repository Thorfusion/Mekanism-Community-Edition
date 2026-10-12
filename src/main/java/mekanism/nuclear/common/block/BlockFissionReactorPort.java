package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import mekanism.common.CTMData;
import mekanism.common.Mekanism;
import mekanism.common.base.IBlockCTM;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.content.fission.FissionPortMode;
import mekanism.nuclear.common.content.fission.FissionReactorPosition;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import mekanism.nuclear.common.content.fission.FissionReactorValidator;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Formation entry point and persisted controller node for a Fission Reactor. */
public class BlockFissionReactorPort extends BlockContainer
      implements IBlockCTM
{
    @SideOnly(Side.CLIENT)
    private IIcon[] modeIcons;
    private CTMData[] modeCTM;

    public BlockFissionReactorPort()
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
        modeIcons = new IIcon[FissionPortMode.values().length];
        modeCTM = new CTMData[FissionPortMode.values().length];
        String[] textures = {"fission_reactor_port_input",
              "fission_reactor_port_output_waste",
              "fission_reactor_port_output_coolant"};
        for (FissionPortMode mode : FissionPortMode.values())
        {
            CTMData data = new CTMData(MekanismNuclear.MODID + ":"
                  + textures[mode.ordinal()], this, Arrays.asList(0))
                  .addOtherBlockConnectivities(
                        NuclearBlocks.FissionReactorCasing, Arrays.asList(0))
                  .addOtherBlockConnectivities(
                        NuclearBlocks.FissionReactorLogicAdapter,
                        Arrays.asList(0)).registerIcons(register);
            modeCTM[mode.ordinal()] = data;
            modeIcons[mode.ordinal()] = data.getIcon(0);
        }
        blockIcon = modeIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return modeIcons == null ? blockIcon : modeIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityFissionReactorPort && modeIcons != null
              ? modeIcons[((TileEntityFissionReactorPort)tile).getMode().ordinal()]
              : getIcon(side, 0);
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
        if (!(tile instanceof TileEntityFissionReactorPort))
        {
            return false;
        }
        TileEntityFissionReactorPort port =
              (TileEntityFissionReactorPort)tile;
        if (player.isSneaking())
        {
            FissionPortMode mode = port.cycleMode();
            player.addChatMessage(new ChatComponentTranslation(
                  "fission.mekanismnuclear.port_mode",
                  new ChatComponentTranslation(
                        "fission.mekanismnuclear.port_mode." + mode.getName())));
            return true;
        }
        TileEntityFissionReactorPort controller = port.getControllerTile();
        if (controller != null && controller.getReactorState().isFormed())
        {
            player.openGui(MekanismNuclear.instance, 1, world, x, y, z);
            return true;
        }
        FissionReactorValidator.Result result = port.validateStructure();
        if (result.isFormed())
        {
            player.addChatMessage(new ChatComponentTranslation(
                  "fission.mekanismnuclear.formed", result.getWidth(),
                  result.getHeight(), result.getLength(),
                  result.getFuelAssemblies()));
            player.openGui(MekanismNuclear.instance, 1, world, x, y, z);
        }
        else if (result.getFailurePos() == null)
        {
            player.addChatMessage(new ChatComponentTranslation(
                  result.getFailure().getTranslationKey()));
        }
        else
        {
            FissionReactorPosition failure = result.getFailurePos();
            player.addChatMessage(new ChatComponentTranslation(
                  result.getFailure().getTranslationKey(), failure.toString()));
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
            if (tile instanceof TileEntityFissionReactorPort)
            {
                ((TileEntityFissionReactorPort)tile).queueValidation();
            }
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
        if (tile instanceof TileEntityFissionReactorPort)
        {
            TileEntityFissionReactorPort controller =
                  ((TileEntityFissionReactorPort)tile).getControllerTile();
            if (controller != null)
            {
                FissionReactorState reactor = controller.getReactorState();
                long capacity = reactor.getFuelCapacity();
                if (capacity > 0 && reactor.getFissileFuel() > 0)
                {
                    return 1 + (int)Math.floor(14D
                          * reactor.getFissileFuel() / capacity);
                }
            }
        }
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityFissionReactorPort();
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
        TileEntity tile = world.getTileEntity(x, y, z);
        int mode = tile instanceof TileEntityFissionReactorPort
              ? ((TileEntityFissionReactorPort)tile).getMode().ordinal() : 0;
        return modeCTM == null ? null : modeCTM[mode];
    }

    @Override
    public boolean shouldRenderBlock(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return true;
    }
}
