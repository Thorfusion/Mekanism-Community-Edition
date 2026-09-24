package mekanism.nuclear.common.block;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.nuclear.common.content.fission.FissionReactorComponent;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

/** Plain exterior/interior reactor parts that do not own reactor state. */
public class BlockFissionReactorComponent extends Block {

    private final FissionReactorComponent component;

    public BlockFissionReactorComponent(FissionReactorComponent component) {
        super(component == FissionReactorComponent.GLASS ? Material.GLASS : Material.IRON);
        this.component = component;
        setHardness(component == FissionReactorComponent.GLASS ? 3F : 3.5F);
        setResistance(component == FissionReactorComponent.GLASS ? 12F : 16F);
        setSoundType(component == FissionReactorComponent.GLASS ? SoundType.GLASS : SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
        if (component == FissionReactorComponent.GLASS) {
            setLightOpacity(0);
        }
    }

    public FissionReactorComponent getComponent() {
        return component;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return component == FissionReactorComponent.GLASS ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return component != FissionReactorComponent.GLASS
              && component != FissionReactorComponent.FUEL_ASSEMBLY
              && component != FissionReactorComponent.CONTROL_ROD;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return component != FissionReactorComponent.GLASS
              && component != FissionReactorComponent.FUEL_ASSEMBLY
              && component != FissionReactorComponent.CONTROL_ROD;
    }
}
