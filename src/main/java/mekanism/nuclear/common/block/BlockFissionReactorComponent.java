package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import mekanism.common.CTMData;
import mekanism.common.Mekanism;
import mekanism.common.base.IBlockCTM;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.content.fission.FissionReactorComponent;
import mekanism.ultimate.common.block.ILegacyJsonModelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/** Plain exterior/interior reactor part that does not own reactor state. */
public class BlockFissionReactorComponent extends Block
      implements ILegacyJsonModelBlock, IBlockCTM
{
    private final FissionReactorComponent component;
    private int renderType;
    private CTMData casingCTM;

    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockFissionReactorComponent(FissionReactorComponent component)
    {
        super(Material.iron);
        this.component = component;
        setHardness(3.5F);
        setResistance(16F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 2);
    }

    public FissionReactorComponent getComponent()
    {
        return component;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        if (component == FissionReactorComponent.FUEL_ASSEMBLY)
        {
            sideIcon = register.registerIcon(MekanismNuclear.MODID
                  + ":model/fuel_assembly/fuel_rod_assembly_side");
            topIcon = register.registerIcon(MekanismNuclear.MODID
                  + ":model/fuel_assembly/fuel_rod_assembly_rods_top");
            bottomIcon = register.registerIcon(MekanismNuclear.MODID
                  + ":model/fuel_assembly/fuel_rod_assembly_base");
        }
        else if (component == FissionReactorComponent.CONTROL_ROD)
        {
            sideIcon = register.registerIcon(MekanismNuclear.MODID
                  + ":model/fuel_assembly/control_rod_assembly");
            topIcon = register.registerIcon(MekanismNuclear.MODID
                  + ":model/fuel_assembly/control_rod_assembly_components");
            bottomIcon = sideIcon;
        }
        else
        {
            casingCTM = new CTMData(MekanismNuclear.MODID
                  + ":fission_reactor_casing", this, Arrays.asList(0))
                  .addOtherBlockConnectivities(
                        NuclearBlocks.FissionReactorPort, Arrays.asList(0))
                  .addOtherBlockConnectivities(
                        NuclearBlocks.FissionReactorLogicAdapter,
                        Arrays.asList(0)).registerIcons(register);
            sideIcon = casingCTM.getIcon(0);
            topIcon = sideIcon;
            bottomIcon = sideIcon;
        }
        blockIcon = sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 1 ? topIcon : side == 0 ? bottomIcon : sideIcon;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return component != FissionReactorComponent.FUEL_ASSEMBLY
              && component != FissionReactorComponent.CONTROL_ROD;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return isOpaqueCube();
    }

    @Override
    public int getRenderType()
    {
        return component == FissionReactorComponent.CASING
              ? Mekanism.proxy.CTM_RENDER_ID : renderType;
    }

    @Override
    public String getLegacyModel(int metadata)
    {
        String model = component == FissionReactorComponent.CONTROL_ROD
              ? "control_rod_assembly" : "fuel_assembly";
        return MekanismNuclear.MODID + ":block/fission/" + model;
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
        return 0;
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        this.renderType = renderType;
    }

    @Override
    public CTMData getCTMData(net.minecraft.world.IBlockAccess world,
          int x, int y, int z, int metadata)
    {
        return component == FissionReactorComponent.CASING
              ? casingCTM : null;
    }

    @Override
    public boolean shouldRenderBlock(net.minecraft.world.IBlockAccess world,
          int x, int y, int z, int metadata)
    {
        return true;
    }
}
