package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import mekanism.common.CTMData;
import mekanism.common.Mekanism;
import mekanism.common.base.IBlockCTM;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.content.sps.SPSPosition;
import mekanism.nuclear.common.content.sps.SPSState;
import mekanism.nuclear.common.content.sps.SPSValidator;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
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

/** Formation entry point and persisted controller node for an SPS. */
public class BlockSPSPort extends BlockContainer implements IBlockCTM
{
    @SideOnly(Side.CLIENT)
    private IIcon inputIcon;
    @SideOnly(Side.CLIENT)
    private IIcon outputIcon;
    private CTMData inputCTM;
    private CTMData outputCTM;

    public BlockSPSPort()
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
        inputCTM = createCTM(register, "sps_port");
        outputCTM = createCTM(register, "sps_port_output");
        inputIcon = inputCTM.getIcon(0);
        outputIcon = outputCTM.getIcon(0);
        blockIcon = inputIcon;
    }

    private CTMData createCTM(IIconRegister register, String texture)
    {
        return new CTMData(MekanismNuclear.MODID + ":" + texture,
              this, Arrays.asList(0)).addOtherBlockConnectivities(
                    NuclearBlocks.SPSCasing, Arrays.asList(0))
              .registerIcons(register);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return inputIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntitySPSPort
              && ((TileEntitySPSPort)tile).isOutput()
              ? outputIcon : inputIcon;
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
        if (!(tile instanceof TileEntitySPSPort))
        {
            return false;
        }
        TileEntitySPSPort port = (TileEntitySPSPort)tile;
        if (player.isSneaking())
        {
            boolean output = port.toggleOutput();
            player.addChatMessage(new ChatComponentTranslation(
                  "sps.mekanismnuclear.port_mode",
                  new ChatComponentTranslation(output
                        ? "sps.mekanismnuclear.port_mode.output"
                        : "sps.mekanismnuclear.port_mode.input")));
            return true;
        }

        TileEntitySPSPort controller = port.getControllerTile();
        if (controller != null && controller.getSPSState().isFormed())
        {
            player.openGui(MekanismNuclear.instance, 2, world, x, y, z);
            return true;
        }

        SPSValidator.Result result = port.validateStructure();
        if (result.isFormed())
        {
            player.addChatMessage(new ChatComponentTranslation(
                  "sps.mekanismnuclear.formed", result.getPorts().size(),
                  result.getCoils().size()));
            player.openGui(MekanismNuclear.instance, 2, world, x, y, z);
        }
        else if (result.getFailurePos() == null)
        {
            player.addChatMessage(new ChatComponentTranslation(
                  result.getFailure().getTranslationKey()));
        }
        else
        {
            SPSPosition failure = result.getFailurePos();
            player.addChatMessage(new ChatComponentTranslation(
                  result.getFailure().getTranslationKey(),
                  failure.toString()));
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
            if (tile instanceof TileEntitySPSPort)
            {
                ((TileEntitySPSPort)tile).queueValidation();
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
        if (tile instanceof TileEntitySPSPort)
        {
            TileEntitySPSPort controller =
                  ((TileEntitySPSPort)tile).getControllerTile();
            if (controller != null)
            {
                SPSState state = controller.getSPSState();
                long capacity = state.getPoloniumCapacity();
                if (capacity > 0 && state.getPolonium() > 0)
                {
                    return 1 + (int)Math.floor(14D
                          * state.getPolonium() / capacity);
                }
            }
        }
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntitySPSPort();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block,
          int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntitySPSPort)
        {
            ((TileEntitySPSPort)tile).prepareForRemoval();
        }
        super.breakBlock(world, x, y, z, block, metadata);
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
        return tile instanceof TileEntitySPSPort
              && ((TileEntitySPSPort)tile).isOutput()
              ? outputCTM : inputCTM;
    }

    @Override
    public boolean shouldRenderBlock(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return true;
    }
}
