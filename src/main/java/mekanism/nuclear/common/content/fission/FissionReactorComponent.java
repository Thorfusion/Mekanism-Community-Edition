package mekanism.nuclear.common.content.fission;

import mekanism.generators.common.GeneratorsBlocks;
import mekanism.generators.common.block.states.BlockStateReactor.ReactorBlockType;
import mekanism.nuclear.common.NuclearBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

/** Block roles understood by the 1.12 fission reactor validator. */
public enum FissionReactorComponent {
    AIR,
    CASING,
    GLASS,
    PORT,
    LOGIC_ADAPTER,
    FUEL_ASSEMBLY,
    CONTROL_ROD,
    INVALID;

    public boolean isExterior() {
        return this == CASING || this == GLASS || this == PORT || this == LOGIC_ADAPTER;
    }

    public static FissionReactorComponent fromState(IBlockState state) {
        if (state == null || state.getMaterial() == Material.AIR) {
            return AIR;
        }
        Block block = state.getBlock();
        if (block == NuclearBlocks.FissionReactorCasing) {
            return CASING;
        } else if (block == GeneratorsBlocks.ReactorGlass
              && ReactorBlockType.get(block, block.getMetaFromState(state)) == ReactorBlockType.REACTOR_GLASS) {
            return GLASS;
        } else if (block == NuclearBlocks.FissionReactorPort) {
            return PORT;
        } else if (block == NuclearBlocks.FissionReactorLogicAdapter) {
            return LOGIC_ADAPTER;
        } else if (block == NuclearBlocks.FissionFuelAssembly) {
            return FUEL_ASSEMBLY;
        } else if (block == NuclearBlocks.ControlRodAssembly) {
            return CONTROL_ROD;
        }
        return INVALID;
    }
}
